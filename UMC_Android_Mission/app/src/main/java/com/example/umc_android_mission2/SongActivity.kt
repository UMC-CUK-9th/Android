package com.example.umc_android_mission2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_android_mission2.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongBinding
    private var musicPlayerService: MusicPlayerService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            isServiceBound = true
            updateUiAndListeners()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songBtnExpandIv.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        // 액티비티가 다시 화면에 나타날 때, 서비스가 연결된 상태라면 UI를 다시 동기화
        if (isServiceBound) {
            updateUiAndListeners()
        }
    }

    override fun onPause() {
        super.onPause()
        // 액티비티가 화면에서 사라질 때, 메모리 누수 방지를 위해 콜백을 반드시 제거
        musicPlayerService?.onSongChanged = null
        musicPlayerService?.onSecondChanged = null
        musicPlayerService?.onStateChanged = null
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    // 1. UI 업데이트와 리스너 설정을 하나의 함수로 통합
    private fun updateUiAndListeners() {
        if (!isServiceBound) return // 서비스가 바인딩되지 않았다면 아무것도 하지 않음

        val service = musicPlayerService ?: return
        val currentSong = service.getCurrentSong()

        // UI 업데이트
        binding.songTitleTv.text = currentSong.title
        binding.songArtistTv.text = currentSong.artist
        binding.songProgressSb.max = currentSong.playtime
        binding.songProgressSb.progress = currentSong.second
        binding.songStartTimeTv.text = formatTime(currentSong.second)
        binding.songEndTimeTv.text = formatTime(currentSong.playtime)
        currentSong.coverImg?.let { binding.songAlbumIv.setImageResource(it) }
        setPlayerStatus(currentSong.isPlaying)
        setLikeStatus(currentSong.isLike)

        // 서비스로부터의 콜백(리스너) 설정
        service.onSongChanged = { song ->
            updateUiAndListeners()
        }
        service.onSecondChanged = { second ->
            binding.songProgressSb.progress = second
            binding.songStartTimeTv.text = formatTime(second)
        }
        service.onStateChanged = { isPlaying ->
            setPlayerStatus(isPlaying)
        }

        // UI의 클릭 리스너 설정 (여기로 통합)
        binding.songBtnPlayIv.setOnClickListener {
            if (service.getCurrentSong().isPlaying) service.pause() else service.play()
        }
        binding.songBtnSkipNextIv.setOnClickListener { service.playNext() }
        binding.songBtnSkipPreviousIv.setOnClickListener { service.playPrevious() }
        binding.songLikeIv.setOnClickListener { service.toggleLike() }
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.songBtnPlayIv.setImageResource(R.drawable.nugu_btn_pause_32)
        } else {
            binding.songBtnPlayIv.setImageResource(R.drawable.nugu_btn_play_32)
        }
    }

    private fun setLikeStatus(isLike: Boolean) {
        if (isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun formatTime(seconds: Int): String {
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }
}
