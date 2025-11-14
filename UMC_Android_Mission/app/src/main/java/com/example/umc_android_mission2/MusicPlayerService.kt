package com.example.umc_android_mission2

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.edit
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicPlayerService : Service() {

    private val binder = MusicPlayerBinder()

    private var songs: List<Song> = emptyList()
    private var nowPos: Int = 0
    private var coverImg: Int? = null
    private var isPlaying: Boolean = false
    private var second: Int = 0

    // Firebase
    private val database = Firebase.database

    private val userLikedSongsRef = database.getReference("users/testUser/likedSongs")

    // Coroutine, DB
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val db by lazy { AlbumDatabase.getInstance(this)!! }

    // SharedPreferences
    companion object {
        const val PREFS_NAME = "MusicPlayerPrefs"
        const val KEY_SONG_ID = "song_id"
    }
    private val sharedPreferences by lazy {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Callbacks
    var onSongChanged: ((SongState) -> Unit)? = null
    var onSecondChanged: ((Int) -> Unit)? = null
    var onStateChanged: ((Boolean) -> Unit)? = null

    // Lifecycle and Binder
    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            loadState()
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    // --- Public API ---

    fun setPlaylist(newSongs: List<Song>, startPos: Int, albumCover: Int?) {
        this.songs = newSongs
        this.nowPos = startPos
        this.coverImg = albumCover
        syncLikesAndUpdate() // 재생 전, Firebase와 '좋아요' 상태를 먼저 동기화합니다.
    }

    fun playNext() {
        val nextPos = if (songs.isEmpty()) -1 else (nowPos + 1) % songs.size
        playSongAtIndex(nextPos)
    }

    fun playPrevious() {
        val prevPos = if (songs.isEmpty()) -1 else (nowPos - 1 + songs.size) % songs.size
        playSongAtIndex(prevPos)
    }

    fun play() {
        if (songs.isNotEmpty() && !isPlaying) {
            this.isPlaying = true
            startTimer()
            onStateChanged?.invoke(true)
        }
    }

    fun pause() {
        if (isPlaying) {
            this.isPlaying = false
            timerJob?.cancel()
            onStateChanged?.invoke(false)
        }
    }

    fun toggleLike() {
        if (songs.isEmpty() || nowPos >= songs.size) return

        val currentSong = songs[nowPos]
        currentSong.isLike = !currentSong.isLike

        // RoomDB 업데이트 코드는 주석 처리
        // serviceScope.launch(Dispatchers.IO) {
        //     db.songDao().updateLike(currentSong.songIdx, currentSong.isLike)
        // }

        // Firebase에 '좋아요' 상태를 업데이트
        if (currentSong.isLike) {
            val likedSongData = mapOf(
                "songIdx" to currentSong.songIdx,
                "title" to currentSong.title,
                "artist" to currentSong.artist,
                "coverImg" to this.coverImg, // 서비스에 저장된 현재 앨범 커버
            )
            // songId를 key로 하여 전체 데이터 객체를 저장
            userLikedSongsRef.child(currentSong.songIdx.toString()).setValue(likedSongData)
        } else {
            // '좋아요'를 취소하면 해당 데이터를 삭제
            userLikedSongsRef.child(currentSong.songIdx.toString()).removeValue()
        }

        // UI 즉시 업데이트
        onSongChanged?.invoke(getCurrentSong())
    }

    fun getCurrentSong(): SongState {
        return if (songs.isNotEmpty() && nowPos < songs.size) {
            val song = songs[nowPos]
            SongState(
                songIdx = song.songIdx,
                title = song.title,
                artist = song.artist,
                playtime = song.playtime,
                coverImg = this.coverImg,
                second = this.second,
                isPlaying = this.isPlaying,
                isLike = song.isLike
            )
        } else {
            SongState()
        }
    }

    // --- Internal Logic ---

    private fun playSongAtIndex(index: Int) {
        if (songs.isEmpty() || index == -1) {
            stopAndClear()
            return
        }

        this.nowPos = index
        this.second = 0
        this.isPlaying = true

        timerJob?.cancel()
        startTimer()

        onSongChanged?.invoke(getCurrentSong())
        onStateChanged?.invoke(true)
        saveState()
    }

    private fun startTimer() {
        timerJob?.cancel()
        val currentPlaytime = songs.getOrNull(nowPos)?.playtime ?: 0
        if (currentPlaytime == 0) return

        timerJob = serviceScope.launch {
            while (isActive) {
                if (second >= currentPlaytime) {
                    playNext()
                    break
                }
                delay(1000)
                second++
                onSecondChanged?.invoke(second)
            }
        }
    }

    private fun stopAndClear() {
        timerJob?.cancel()
        songs = emptyList()
        nowPos = 0
        isPlaying = false
        second = 0
        onSongChanged?.invoke(getCurrentSong())
        onStateChanged?.invoke(false)
    }

    override fun onDestroy() {
        saveState()
        super.onDestroy()
        serviceScope.cancel()
    }

    // --- State Persistence & Sync ---

    private fun saveState() {
        if (songs.isNotEmpty() && nowPos < songs.size) {
            val songId = songs[nowPos].songIdx
            sharedPreferences.edit {
                putInt(KEY_SONG_ID, songId)
            }
        }
    }

    private suspend fun loadState() {
        val songId = sharedPreferences.getInt(KEY_SONG_ID, 0)
        if (songId == 0) return

        val loadedSong: Song? = withContext(Dispatchers.IO) { db.songDao().getSong(songId) }
        if (loadedSong == null) return

        val loadedAlbum = withContext(Dispatchers.IO) { db.albumDao().getAlbum(loadedSong.albumIdx) }
        if (loadedAlbum != null) {
            val restoredSongs = withContext(Dispatchers.IO) { db.songDao().getSongsInAlbum(loadedSong.albumIdx) }
            val restoredPos = restoredSongs.indexOfFirst { it.songIdx == songId }

            if (restoredPos != -1) {
                this.songs = restoredSongs
                this.nowPos = restoredPos
                this.coverImg = loadedAlbum.coverImg
                this.second = 0
                this.isPlaying = false // 앱 시작 시에는 항상 정지 상태

                syncLikesAndUpdate(false) // 앱 시작 시에는 재생하지 않고 UI만 업데이트
            }
        }
    }

    /**
     * Firebase와 '좋아요' 상태를 동기화하고, 재생 또는 UI 업데이트를 수행합니다.
     */
    private fun syncLikesAndUpdate(andPlay: Boolean = true) {
        if (songs.isEmpty()) {
            if (andPlay) playSongAtIndex(-1)
            return
        }

        userLikedSongsRef.get().addOnSuccessListener { dataSnapshot ->
            // 이제 Firebase에 객체가 저장되지만, isLike 플래그를 동기화하는 로직은 여전히 유효합니다.
            // 키(songId)가 존재하는지만 확인하면 되기 때문입니다.
            val likedIds = dataSnapshot.children.mapNotNull { it.key?.toInt() }.toSet()
            songs.forEach { song ->
                song.isLike = song.songIdx in likedIds
            }

            if (andPlay) {
                playSongAtIndex(nowPos)
            } else {
                onSongChanged?.invoke(getCurrentSong())
                onStateChanged?.invoke(false)
            }
        }.addOnFailureListener {
            // Firebase 로드 실패 시에도 기존 로직대로 재생은 되어야 합니다.
            if (andPlay) playSongAtIndex(nowPos)
            else {
                onSongChanged?.invoke(getCurrentSong())
                onStateChanged?.invoke(false)
            }
        }
    }
}