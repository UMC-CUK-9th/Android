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

        // 전달받은 앨범 제목과 가수 이름으로 TextView 업데이트
        // 앨범 제목이 없을 경우 "제목 없음"으로 표시
        // 가수 이름이 없을 경우 "가수 없음"으로 표시
        binding.songTitleTv.text = albumTitle ?: "제목 없음"
        binding.songArtistTv.text = artistName ?: "가수 없음"

        // 버튼 클릭 시 MainActivity로 결과값 전달하며 종료
        binding.songBtnExpandIv.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("album_title", albumTitle) // 처음 받은 값 기준
                putExtra("artist_name", artistName) // 처음 받은 값 기준
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}