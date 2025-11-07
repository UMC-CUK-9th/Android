package com.example.umc_android_mission2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.umc_android_mission2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var musicPlayerService: MusicPlayerService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            isServiceBound = true
            // 서비스가 연결되면, UI와 리스너를 즉시 설정
            updateUiAndListeners()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 서비스 시작 및 바인딩
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_frm) as NavHostFragment
        val navController = navHostFragment.navController
        binding.mainBnv.setupWithNavController(navController)

        // 미니플레이어 레이아웃 전체 클릭 시 SongActivity로 이동
        binding.mainPlayer.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            // 최신 곡 정보를 전달할 필요 없이, SongActivity가 서비스로부터 직접 받도록 함
            startActivity(intent)
        }
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
    
    // 1. UI 업데이트와 리스너 설정을 하나의 함수로 통합
    private fun updateUiAndListeners() {
        if (!isServiceBound) return // 서비스가 바인딩되지 않았다면 아무것도 하지 않음

        val service = musicPlayerService ?: return
        val currentSong = service.getCurrentSong()

        // --- UI 업데이트 로직 ---
        if (currentSong.title.isNotBlank()) {
            binding.mainPlayer.visibility = View.VISIBLE
            binding.mainPlayerTitle.text = currentSong.title
            binding.mainPlayerArtist.text = currentSong.singer
            binding.mainPlayerSeekbar.max = currentSong.playtime
            binding.mainPlayerSeekbar.progress = currentSong.second
            setPlayerStatus(currentSong.isPlaying)
        } else {
            binding.mainPlayer.visibility = View.GONE
        }

        // --- 서비스로부터의 콜백(리스너) 설정 ---
        service.onSongChanged = { song ->
            // 곡 정보가 바뀔 때 UI 전체를 다시 그림
            updateUiAndListeners()
        }
        service.onSecondChanged = { second ->
            binding.mainPlayerSeekbar.progress = second
        }
        service.onStateChanged = { isPlaying ->
            setPlayerStatus(isPlaying)
        }

        // --- UI의 클릭 리스너 설정 (여기로 통합) ---
        binding.mainMiniplayerBtn.setOnClickListener { service.play() }
        binding.mainPauseBtn.setOnClickListener { service.pause() }
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