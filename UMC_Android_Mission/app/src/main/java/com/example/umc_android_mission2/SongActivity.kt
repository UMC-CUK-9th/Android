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

        // MainActivity에서 전달한 앨범 제목,가수 받기
        val albumTitle = intent.getStringExtra("album_title")
        val artistName = intent.getStringExtra("artist_name")

        // 버튼 클릭 시 MainActivity로 결과값 전달하며 종료
        binding.songBtnExpandIv.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("album_title", albumTitle)
                putExtra("artist_name",artistName)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
