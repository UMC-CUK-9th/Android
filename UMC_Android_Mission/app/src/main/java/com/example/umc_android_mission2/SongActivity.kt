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
            initSong() 
            setupUI()
            setupListeners()
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

        binding.songBtnPlayIv.setOnClickListener {
            if (isServiceBound) {
                val song = musicPlayerService!!.getCurrentSong()
                if (song.isPlaying) {
                    musicPlayerService?.pause()
                } else {
                    musicPlayerService?.play()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        if (isServiceBound) {
            setupUI()
            setupListeners()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isServiceBound) {
            musicPlayerService?.onSongChanged = null
            musicPlayerService?.onSecondChanged = null
            musicPlayerService?.onStateChanged = null
        }
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    private fun initSong() {
        if (!isServiceBound) return
        val title = intent.getStringExtra("album_title") ?: ""
        val singer = intent.getStringExtra("artist_name") ?: ""
        val coverImg = intent.getIntExtra("album_coverImg", 0)

        val currentSong = musicPlayerService!!.getCurrentSong()

        if (currentSong.title != title || currentSong.singer != singer) {
            musicPlayerService?.setSong(
                Song(
                    title = title,
                    singer = singer,
                    playtime = 60,
                    isPlaying = true,
                    coverImg = coverImg.takeIf { it != 0 }
                )
            )
        }
    }

    private fun setupUI() {
        if (!isServiceBound) return
        val song = musicPlayerService!!.getCurrentSong()
        binding.songTitleTv.text = song.title
        binding.songArtistTv.text = song.singer
        binding.songProgressSb.max = song.playtime
        binding.songProgressSb.progress = song.second
        binding.songStartTimeTv.text = formatTime(song.second)
        binding.songEndTimeTv.text = formatTime(song.playtime)
        song.coverImg?.let { binding.songAlbumIv.setImageResource(it) }
        setPlayerStatus(song.isPlaying)
    }

    private fun setupListeners() {
        if (!isServiceBound) return
        musicPlayerService?.onSongChanged = { song ->
            binding.songTitleTv.text = song.title
            binding.songArtistTv.text = song.singer
            binding.songProgressSb.max = song.playtime
            binding.songEndTimeTv.text = formatTime(song.playtime)
            song.coverImg?.let { img -> binding.songAlbumIv.setImageResource(img) }
        }
        musicPlayerService?.onSecondChanged = { second ->
            binding.songProgressSb.progress = second
            binding.songStartTimeTv.text = formatTime(second)
        }
        musicPlayerService?.onStateChanged = { isPlaying ->
            setPlayerStatus(isPlaying)
        }
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.songBtnPlayIv.setImageResource(R.drawable.nugu_btn_pause_32)
        } else {
            binding.songBtnPlayIv.setImageResource(R.drawable.nugu_btn_play_32)
        }
    }

    private fun formatTime(seconds: Int): String {
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }
}