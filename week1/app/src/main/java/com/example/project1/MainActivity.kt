package com.example.project1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var tvHappy: TextView
    private lateinit var tvExcited: TextView
    private lateinit var tvSoso: TextView
    private lateinit var tvNervous: TextView
    private lateinit var tvAngry: TextView

    private lateinit var imgHappy: ImageView
    private lateinit var imgExcited: ImageView
    private lateinit var imgSoso: ImageView
    private lateinit var imgNervous: ImageView
    private lateinit var imgAngry: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvHappy = findViewById(R.id.tvHappy)
        tvExcited = findViewById(R.id.tvExcited)
        tvSoso = findViewById(R.id.tvSoso)
        tvNervous = findViewById(R.id.tvNervous)
        tvAngry = findViewById(R.id.tvAngry)

        imgHappy = findViewById(R.id.imgHappy)
        imgExcited = findViewById(R.id.imgExcited)
        imgSoso = findViewById(R.id.imgSoso)
        imgNervous = findViewById(R.id.imgNervous)
        imgAngry = findViewById(R.id.imgAngry)

        // 클릭 이벤트
        imgHappy.setOnClickListener { onEmotionSelected(tvHappy, "행복한") }
        imgExcited.setOnClickListener { onEmotionSelected(tvExcited, "신나는") }
        imgSoso.setOnClickListener { onEmotionSelected(tvSoso, "평범한") }
        imgNervous.setOnClickListener { onEmotionSelected(tvNervous, "불안한") }
        imgAngry.setOnClickListener { onEmotionSelected(tvAngry, "화나는") }
    }

    private fun onEmotionSelected(selectedText: TextView, emotion: String) {
        // 모든 텍스트 색상 초기화
        tvHappy.setTextColor(Color.BLACK)
        tvExcited.setTextColor(Color.BLACK)
        tvSoso.setTextColor(Color.BLACK)
        tvNervous.setTextColor(Color.BLACK)
        tvAngry.setTextColor(Color.BLACK)

        // 선택된 텍스트만 강조
        //selectedText.setTextColor(Color.RED)

        when (selectedText) {
            tvHappy -> selectedText.setTextColor(Color.YELLOW)
            tvExcited -> selectedText.setTextColor(Color.BLUE)
            tvSoso -> selectedText.setTextColor(Color.GRAY)
            tvNervous -> selectedText.setTextColor(Color.GREEN)
            tvAngry -> selectedText.setTextColor(Color.RED)
        }

        // Toast 메시지
        Toast.makeText(this, "$emotion 하루입니다", Toast.LENGTH_SHORT).show()

    }
}