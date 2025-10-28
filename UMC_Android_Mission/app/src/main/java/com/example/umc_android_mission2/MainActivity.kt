package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.umc_android_mission2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentAlbum: AlbumData? = null // SongActivity로 전달해야 할 coverImg라는 보이지 않는 정보를 잠시 기억해두는 메모장 역할

    private val songActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val albumTitle = data?.getStringExtra("album_title")
            albumTitle?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // 스플래시 화면을 유지하는 시간을 설정
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_frm) as NavHostFragment
        val navController = navHostFragment.navController

        binding.mainPlayer.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java).apply {
                // currentAlbum이 있으면 그 정보를, 없으면 TextView의 현재 텍스트를 전달
                if (currentAlbum != null) {
                    putExtra("album_title", currentAlbum!!.title)
                    putExtra("artist_name", currentAlbum!!.artist)
                    currentAlbum!!.coverImg?.let { putExtra("album_coverImg", it) }
                } else {
                    putExtra("album_title", binding.mainPlayerTitle.text.toString())
                    putExtra("artist_name", binding.mainPlayerArtist.text.toString())
                    // album_coverImg는 전달하지 않음
                }
            }
            songActivityLauncher.launch(intent)
        }

        binding.mainBnv.setupWithNavController(navController)
    }

    fun updateMiniPlayer(album: AlbumData) {
        // 현재 앨범 정보를 저장하고 UI 업데이트
        currentAlbum = album
        binding.mainPlayerTitle.text = album.title
        binding.mainPlayerArtist.text = album.artist
    }
}
