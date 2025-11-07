package com.example.umc_android_mission2

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerService : Service() {

    private val binder = MusicPlayerBinder()
    private var song: Song = Song()

    // --- SharedPreferences와 상태 저장을 위한 Key 정의 ---
    companion object {
        const val PREFS_NAME = "MusicPlayerPrefs"
        const val KEY_SONG_DATA = "song_data"
    }

    private val sharedPreferences by lazy {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 1. 코루틴 스코프와 타이머 작업을 위한 Job 생성
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    // 각 화면(Activity)이 상태 변화를 감지할 수 있도록 콜백 함수들을 정의
    var onSongChanged: ((Song) -> Unit)? = null
    var onSecondChanged: ((Int) -> Unit)? = null
    var onStateChanged: ((Boolean) -> Unit)? = null

    // 서비스가 생성될 때 마지막 상태를 복원
    override fun onCreate() {
        super.onCreate()
        loadState()
    }

    // Activity가 Service에 연결될 때 호출됨
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    // Activity가 Service와 통신할 수 있는 통로 역할
    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    fun setSong(newSong: Song) {
        // 기존 타이머 코루틴이 있다면 취소
        timerJob?.cancel()
        this.song = newSong

        // 코루틴 스코프가 Main Dispatcher를 사용하므로 Handler 없이 직접 호출 가능
        onSongChanged?.invoke(this.song)
        onStateChanged?.invoke(this.song.isPlaying)
        onSecondChanged?.invoke(this.song.second)

        if (this.song.isPlaying) {
            startTimer()
        }
    }

    fun play() {
        if (!song.isPlaying) {
            song.isPlaying = true
            startTimer()
            onStateChanged?.invoke(true)
        }
    }

    fun pause() {
        if (song.isPlaying) {
            song.isPlaying = false
            // 타이머 코루틴 취소
            timerJob?.cancel()
            onStateChanged?.invoke(false)
            // 사용자가 직접 멈출 때 상태 저장
            saveState()
        }
    }

    fun getCurrentSong(): Song {
        return song
    }

    // 2. Timer 대신 코루틴을 사용한 타이머 구현
    private fun startTimer() {
        timerJob = serviceScope.launch {
            while (isActive) {
                if (song.second >= song.playtime) {
                    song.isPlaying = false
                    onStateChanged?.invoke(false)
                    saveState() // 노래가 끝나도 상태 저장
                    // 루프를 멈춰 코루틴 종료
                    break
                }
                song.second++
                onSecondChanged?.invoke(song.second)
                delay(1000)
            }
        }
    }

    // 3. Service가 소멸될 때 CoroutineScope를 취소하고 상태를 저장
    override fun onDestroy() {
        saveState()
        super.onDestroy()
        serviceScope.cancel()
    }

    // --- 상태 저장 및 복원 함수 ---
    private fun saveState() {
        if (song.title.isBlank()) return // 저장할 제목이 없으면 저장하지 않음
        
        // 구분자(delimiter)를 사용해 Song 데이터를 하나의 문자열로 합침
        val songDataString = listOf(
            song.title,
            song.singer,
            song.second.toString(),
            song.playtime.toString(),
            song.isPlaying.toString(),
            song.coverImg.toString()
        ).joinToString(";;;")

        sharedPreferences.edit {
            putString(KEY_SONG_DATA, songDataString)
        }
    }

    private fun loadState() {
        val songDataString = sharedPreferences.getString(KEY_SONG_DATA, null)
        if (songDataString.isNullOrBlank()) {
            song = Song()
            return
        }

        // 저장된 문자열을 구분자로 잘라서(split) 데이터 복원
        val parts = songDataString.split(";;;")
        if (parts.size == 6) {
            song = Song(
                title = parts[0],
                singer = parts[1],
                second = parts[2].toIntOrNull() ?: 0,
                playtime = parts[3].toIntOrNull() ?: 0,
                // 중요: 복원 시에는 항상 '멈춤' 상태로 로드하여 사용자가 직접 재생하도록 유도
                isPlaying = false,
                coverImg = parts[5].toIntOrNull()
            )
        }
    }
}