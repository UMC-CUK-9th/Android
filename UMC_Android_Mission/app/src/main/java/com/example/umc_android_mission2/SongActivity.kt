package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_android_mission2.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MainActivity에서 전달한 데이터 받기
        val albumTitle = intent.getStringExtra("album_title")
        val artistName = intent.getStringExtra("artist_name")
        val albumCoverImg = intent.getIntExtra("album_coverImg", 0)

        // 전달받은 데이터로 UI 업데이트
        binding.songTitleTv.text = albumTitle ?: "제목 없음"
        binding.songArtistTv.text = artistName ?: "가수 없음"
        if (albumCoverImg != 0) {
            binding.songAlbumIv.setImageResource(albumCoverImg)
        }

        // 버튼 클릭 시 MainActivity로 결과값 전달하며 종료
        binding.songBtnExpandIv.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("album_title", albumTitle)
                putExtra("artist_name", artistName)
                putExtra("album_coverImg", albumCoverImg)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
