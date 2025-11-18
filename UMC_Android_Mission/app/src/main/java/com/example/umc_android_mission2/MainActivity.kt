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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.umc_android_mission2.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var musicPlayerService: MusicPlayerService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? MusicPlayerService.MusicPlayerBinder

            if (binder != null) {
                musicPlayerService = binder.getService()
                isServiceBound = true
                updateUiAndListeners()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicPlayerService = null
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_frm) as NavHostFragment
        val navController = navHostFragment.navController
        binding.mainBnv.setupWithNavController(navController)

        binding.mainPlayer.setOnClickListener { val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        binding.mainMiniplayerPrevBtn.setOnClickListener { musicPlayerService?.playPrevious() }
        binding.mainMiniplayerNextBtn.setOnClickListener { musicPlayerService?.playNext() }
    }

    override fun onResume() {
        super.onResume()
        if (isServiceBound) {
            updateUiAndListeners()
        }
    }

    override fun onPause() {
        super.onPause()
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

    fun updateMiniPlayer(album: Album) {
        if (!isServiceBound) return

        lifecycleScope.launch {
            val songs = withContext(Dispatchers.IO) {
                AlbumDatabase.getInstance(this@MainActivity)!!.songDao().getSongsInAlbum(album.albumIdx)
            }

            if (songs.isNotEmpty()) {
                val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
                val savedSongId = sharedPreferences.getInt("songId", 0)
                val nowPos = songs.indexOfFirst { it.songIdx == savedSongId }.takeIf { it != -1 } ?: 0

                // 서비스에 전체 재생 목록과 시작 위치를 전달합니다.
                musicPlayerService?.setPlaylist(songs, nowPos)
            } else {
                // 재생할 노래가 없으면 플레이리스트를 비웁니다.
                musicPlayerService?.setPlaylist(emptyList(), 0)
            }
        }
    }

    private fun updateUiAndListeners() {
        if (!isServiceBound) return

        val service = musicPlayerService ?: return
        val currentSong = service.getCurrentSong()

        binding.mainPlayer.visibility = View.VISIBLE

        if (currentSong.title.isNotBlank()) {
            binding.mainPlayerTitle.text = currentSong.title
            binding.mainPlayerArtist.text = currentSong.artist
            binding.mainPlayerSeekbar.max = currentSong.playtime
            binding.mainPlayerSeekbar.progress = currentSong.second
            setPlayerStatus(currentSong.isPlaying)
        } else {
            // 노래가 없으면, UI의 내용을 비움
            binding.mainPlayerTitle.text = ""
            binding.mainPlayerArtist.text = ""
            binding.mainPlayerSeekbar.progress = 0
            binding.mainPlayerSeekbar.max = 0
            setPlayerStatus(false)
        }

        service.onSongChanged = { song ->
            updateUiAndListeners()
        }
        service.onSecondChanged = { second ->
            binding.mainPlayerSeekbar.progress = second
        }
        service.onStateChanged = { isPlaying ->
            setPlayerStatus(isPlaying)
        }

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
