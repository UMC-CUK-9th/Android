package com.example.umc_android_mission2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.umc_android_mission2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var musicPlayerService: MusicPlayerService? = null
    private var isServiceBound = false

    // 서비스와의 연결을 관리하는 객체
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            isServiceBound = true
            // 서비스가 연결되는 시점에 UI를 한번 업데이트하고 리스너를 설정
            setupUI()
            setupListeners()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 서비스 시작 및 바인딩
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_frm) as NavHostFragment
        val navController = navHostFragment.navController

        binding.mainPlayer.setOnClickListener {
            val currentSong = musicPlayerService?.getCurrentSong() ?: return@setOnClickListener
            val intent = Intent(this, SongActivity::class.java).apply {
                putExtra("album_title", currentSong.title)
                putExtra("artist_name", currentSong.singer)
                currentSong.coverImg?.let { putExtra("album_coverImg", it) }
            }
            startActivity(intent)
        }

        binding.mainMiniplayerBtn.setOnClickListener { musicPlayerService?.play() }
        binding.mainPauseBtn.setOnClickListener { musicPlayerService?.pause() }

        binding.mainBnv.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        // 화면에 복귀할 때마다, 서비스가 연결되어 있다면 UI와 리스너를 다시 설정
        // SongActivity가 훔쳐갔던 리스너를 다시 되찾아오는 과정
        if (isServiceBound) {
            setupUI()
            setupListeners()
        }
    }

    override fun onPause() {
        super.onPause()
        // 다른 화면으로 이동하기 직전에 리스너를 해제
        // 다른 Activity가 리스너 역할을 할 수 있도록 자리를 비워줌
        if (isServiceBound) {
            musicPlayerService?.onSongChanged = null
            musicPlayerService?.onSecondChanged = null
            musicPlayerService?.onStateChanged = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    // HomeFragment에서 호출되어 서비스의 곡을 업데이트하는 함수
    fun updateMiniPlayer(album: AlbumData) {
        if (!isServiceBound) return
        val newSong = Song(
            title = album.title ?: "제목 없음",
            singer = album.artist ?: "가수 없음",
            playtime = 60,
            isPlaying = true,
            coverImg = album.coverImg
        )
        musicPlayerService?.setSong(newSong)
    }

    private fun setupUI() {
        if (!isServiceBound) return
        val song = musicPlayerService!!.getCurrentSong()
        binding.mainPlayerTitle.text = song.title
        binding.mainPlayerArtist.text = song.singer
        binding.mainPlayerSeekbar.max = song.playtime
        binding.mainPlayerSeekbar.progress = song.second
        setPlayerStatus(song.isPlaying)
    }

    private fun setupListeners() {
        if (!isServiceBound) return
        musicPlayerService?.onSongChanged = { song ->
            binding.mainPlayerTitle.text = song.title
            binding.mainPlayerArtist.text = song.singer
            binding.mainPlayerSeekbar.max = song.playtime
        }
        musicPlayerService?.onSecondChanged = { second ->
            binding.mainPlayerSeekbar.progress = second
        }
        musicPlayerService?.onStateChanged = { isPlaying ->
            setPlayerStatus(isPlaying)
        }
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
        }
    }
}

