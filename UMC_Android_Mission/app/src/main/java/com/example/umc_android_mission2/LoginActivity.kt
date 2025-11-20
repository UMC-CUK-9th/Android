package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.umc_android_mission2.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity: AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginCloseIv.setOnClickListener {
            finish()
        }

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginSignInBtn.setOnClickListener {
            login()
        }

    }

    private fun login() {
        if ( binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if( binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }
        val email: String = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val pwd: String = binding.loginPasswordEt.text.toString()

        // DB 작업을 코루틴으로 실행
        lifecycleScope.launch(Dispatchers.IO) {
            val userDB = AlbumDatabase.getInstance(this@LoginActivity)!!
            val user = userDB.userDao().getUser(email, pwd)

            // UI 작업은 메인 스레드에서 처리
            withContext(Dispatchers.Main) {
                if (user != null) {
                    // 로그인 성공
                    Log.d("LOGIN_ACT/GET_USER", "userId: ${user.id}, $user")
                    saveJwt(user.id)
                    startMainActivity()
                } else {
                    // 로그인 실패
                    Toast.makeText(this@LoginActivity,"회원 정보가 존재하지 않습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // JWT를 SharedPreferences에 저장하는 함수
    private fun saveJwt(jwt: Int) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putInt("jwt", jwt)
        editor.apply()
    }

    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 로그인 성공 후에는 현재 액티비티를 닫음
    }
}