package com.example.umc_android_mission2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.umc_android_mission2.databinding.ActivitySignupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity: AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpSignUpBtn.setOnClickListener {
            signUp()
        }
    }

    private fun getUser(): User {
        val email: String = binding.signUpIdEt.text.toString() + "@" + binding.signUpDirectInputEt.text.toString()
        val pwd: String = binding.signUpPasswordEt.text.toString()

        return User(email, pwd)
    }

    private fun signUp() {
        if ( binding.signUpIdEt.text.toString().isEmpty() || binding.signUpDirectInputEt.text.isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못 되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if ( binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }


        val user = getUser()

        // DB 작업 백그라운드 스레드에서 실행
        lifecycleScope.launch(Dispatchers.IO) {
            val userDB = AlbumDatabase.getInstance(this@SignUpActivity)!!
            userDB.userDao().insert(user)

            val users = userDB.userDao().getUsers()
            Log.d("SIGNUPACT", users.toString())

            withContext(Dispatchers.Main) {
                Toast.makeText(this@SignUpActivity, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
