package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_android_mission2.databinding.ActivitySongBinding
import java.util.Timer
import kotlin.concurrent.timerTask

class SongActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongBinding
    private lateinit var song: Song
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer()

        binding.songBtnExpandIv.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("song_title", song.title)
                putExtra("artist_name", song.singer)
                putExtra("second", song.second)
                putExtra("playtime", song.playtime)
                putExtra("isPlaying", song.isPlaying)
                intent.getIntExtra("album_coverImg", 0).let {
                    if (it != 0) putExtra("album_coverImg", it)
                }
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // 하나의 버튼으로 재생/일시정지 제어
        binding.songBtnPlayIv.setOnClickListener {
            song.isPlaying = !song.isPlaying // 상태 반전
            setPlayerStatus(song.isPlaying) // UI 및 타이머 업데이트
        }
        // 이전 곡 버튼 클릭 시 음악 초기화
        binding.songBtnSkipNextIv.setOnClickListener {
            restartSong()
        }
        // 다음 곡 버튼 클릭 시 음악 초기화
        binding.songBtnSkipPreviousIv.setOnClickListener {
            restartSong()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun initSong() {
        val title = intent.getStringExtra("album_title") ?: "제목 없음"
        val singer = intent.getStringExtra("artist_name") ?: "가수 없음"

        song = Song(
            title = title,
            singer = singer,
            second = 0,
            playtime = 60,
            isPlaying = true
        )
    }

    private fun setPlayer() {
        binding.songTitleTv.text = song.title
        binding.songArtistTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playtime / 60, song.playtime % 60)
        binding.songProgressSb.max = song.playtime
        binding.songProgressSb.progress = song.second

        val albumCoverImg = intent.getIntExtra("album_coverImg", 0)
        if (albumCoverImg != 0) {
            binding.songAlbumIv.setImageResource(albumCoverImg)
        }

        setPlayerStatus(song.isPlaying)
    }

    // 이미지 리소스를 교체하는 방식
    private fun setPlayerStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.songBtnPlayIv.setImageResource(R.drawable.nugu_btn_pause_32) // 일시정지 아이콘
            startTimer()
        } else {
            binding.songBtnPlayIv.setImageResource(R.drawable.nugu_btn_play_32) // 재생 아이콘
            timer?.cancel()
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(timerTask {
            if (song.second >= song.playtime) {
                runOnUiThread { setPlayerStatus(false) }
                return@timerTask
            }
            song.second++
            runOnUiThread {
                binding.songProgressSb.progress = song.second
                binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
            }
        }, 1000, 1000)
    }

    private fun restartSong() {
        timer?.cancel() // 기존 타이머 중지
        song.second = 0
        song.isPlaying = true
        runOnUiThread {
            binding.songProgressSb.progress = song.second
            binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
            setPlayerStatus(song.isPlaying) // UI 업데이트 및 타이머 다시 시작
        }
    }
}