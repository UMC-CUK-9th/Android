package com.example.umc_android_mission2

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.edit
// import com.google.firebase.Firebase // 주석 처리
// import com.google.firebase.database.database // 주석 처리
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
    private var coverImg: Int? = null // 이 변수는 이제 상태 복원용으로만 사용
    private var isPlaying: Boolean = false
    private var second: Int = 0

    // Firebase (주석 처리)
    // private val database = Firebase.database
    // private val userLikedSongsRef = database.getReference("users/testUser/likedSongs")

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

    fun setPlaylist(newSongs: List<Song>, startPos: Int) {
        this.songs = newSongs
        this.nowPos = startPos
        // this.coverImg는 더 이상 여기서 설정하지 않음
        playSongAtIndex(nowPos)
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

        // 1. RoomDB에 isLike 상태를 업데이트하는 코드를 활성화합니다.
        serviceScope.launch(Dispatchers.IO) {
            db.songDao().updateLike(currentSong.songIdx, currentSong.isLike)
        }

        /* 2. Firebase 관련 코드는 모두 주석 처리합니다.
        if (currentSong.isLike) {
            val likedSongData = mapOf(
                "songIdx" to currentSong.songIdx,
                "title" to currentSong.title,
                "artist" to currentSong.artist,
                "coverImg" to currentSong.coverImg,
            )
            userLikedSongsRef.child(currentSong.songIdx.toString()).setValue(likedSongData)
        } else {
            userLikedSongsRef.child(currentSong.songIdx.toString()).removeValue()
        }
        */

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
                coverImg = song.coverImg, // 3. 개별 Song 객체의 coverImg를 사용하도록 수정
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

        // coverImg를 현재 곡의 것으로 업데이트 (상태 복원 시 필요)
        this.coverImg = songs.getOrNull(index)?.coverImg

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

        val restoredSongs = withContext(Dispatchers.IO) { db.songDao().getSongsInAlbum(loadedSong.albumIdx) }
        val restoredPos = restoredSongs.indexOfFirst { it.songIdx == songId }

        if (restoredPos != -1) {
            this.songs = restoredSongs
            this.nowPos = restoredPos
            this.coverImg = loadedSong.coverImg // 복원 시에도 song의 coverImg 사용
            this.second = 0
            this.isPlaying = false

            // UI 업데이트 로직 직접 호출
            onSongChanged?.invoke(getCurrentSong())
            onStateChanged?.invoke(false)
        }
    }
}
