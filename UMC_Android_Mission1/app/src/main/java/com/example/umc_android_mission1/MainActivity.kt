package com.example.umc_android_mission1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.umc_android_mission1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val colorHappy = ContextCompat.getColor(this, R.color.happy)
        val colorExcited = ContextCompat.getColor(this, R.color.excited)
        val colorNormal = ContextCompat.getColor(this, R.color.normal)
        val colorAnxiety = ContextCompat.getColor(this, R.color.anxiety)
        val colorAngry = ContextCompat.getColor(this, R.color.angry)


        binding.btnHappy.setOnClickListener {
            binding.tvHappy.setTextColor(colorHappy)
            Toast.makeText(this, "무엇이 가장 행복했나요?", Toast.LENGTH_SHORT).show()
        }

        binding.btnExcited.setOnClickListener {
            binding.tvExcited.setTextColor(colorExcited)
            Toast.makeText(this, "무엇이 가장 들떴나요?", Toast.LENGTH_SHORT).show()
        }

        binding.btnNormal.setOnClickListener {
            binding.tvNormal.setTextColor(colorNormal)
            Toast.makeText(this, "그래도 기억에 남는 일은 없었나요?", Toast.LENGTH_SHORT).show()
        }

        binding.btnAnxiety.setOnClickListener {
            binding.tvAnxiety.setTextColor(colorAnxiety)
            Toast.makeText(this, "어떤 생각이 가장 많이 들었나요?", Toast.LENGTH_SHORT).show()
        }

        binding.btnAngry.setOnClickListener {
            binding.tvAngry.setTextColor(colorAngry)
            Toast.makeText(this, "무엇이 가장 화가 났나요?", Toast.LENGTH_SHORT).show()
        }
    }
}
