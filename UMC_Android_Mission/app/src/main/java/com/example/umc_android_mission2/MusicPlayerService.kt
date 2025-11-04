package com.example.umc_android_mission2

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.util.Timer
import kotlin.concurrent.timerTask

class MusicPlayerService : Service() {

    private val binder = MusicPlayerBinder()
    private var song: Song = Song()
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper()) // UI 업데이트를 위한 핸들러

    // 각 화면(Activity)이 상태 변화를 감지할 수 있도록 콜백 함수들을 정의
    var onSongChanged: ((Song) -> Unit)? = null
    var onSecondChanged: ((Int) -> Unit)? = null
    var onStateChanged: ((Boolean) -> Unit)? = null

    // Activity가 Service에 연결될 때 호출됨
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    // Activity가 Service와 통신할 수 있는 통로 역할
    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    fun setSong(newSong: Song) {
        timer?.cancel()
        this.song = newSong
        handler.post {
            onSongChanged?.invoke(this.song)
            onStateChanged?.invoke(this.song.isPlaying)
            onSecondChanged?.invoke(this.song.second)
        }
        if (this.song.isPlaying) {
            startTimer()
        }
    }

    fun play() {
        if (!song.isPlaying) {
            song.isPlaying = true
            startTimer()
            handler.post { onStateChanged?.invoke(true) }
        }
    }

    fun pause() {
        if (song.isPlaying) {
            song.isPlaying = false
            timer?.cancel()
            handler.post { onStateChanged?.invoke(false) }
        }
    }

    fun getCurrentSong(): Song {
        return song
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(timerTask {
            if (song.second >= song.playtime) {
                song.isPlaying = false
                timer?.cancel()
                handler.post { onStateChanged?.invoke(false) }
                return@timerTask
            }
            song.second++
            handler.post {
                onSecondChanged?.invoke(song.second)
            }
        }, 1000, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}


