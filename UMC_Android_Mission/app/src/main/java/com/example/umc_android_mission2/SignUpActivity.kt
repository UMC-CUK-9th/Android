package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_android_mission2.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    // 1. ViewModel과 그에 필요한 부품들을 선언합니다.
    private val repository by lazy { AuthRepository(ApiClient.authService) }
    private val viewModelFactory by lazy { AuthViewModelFactory(repository) }
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. API 호출 결과를 관찰하고, 그에 따라 UI 처리를 설정합니다.
        observeSignUpResult()

        // 3. 회원가입 버튼 클릭 리스너를 설정합니다.
        binding.signUpSignUpBtn.setOnClickListener {
            handleSignUp()
        }
    }

    private fun observeSignUpResult() {
        viewModel.signUpResult.observe(this) { result ->
            result.onSuccess { memberIdResult ->
                // 성공 시: 토스트 메시지를 보여주고 로그인 화면으로 이동
                Toast.makeText(this, "회원가입 성공! 회원 ID: ${memberIdResult.memberId}", Toast.LENGTH_LONG).show()
                // 회원가입 성공 시 LoginActivity로 이동하도록 수정 (MainActivity가 아닌)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            }.onFailure { exception ->
                // 실패 시: 에러 메시지를 토스트로 보여줌
                Toast.makeText(this, "회원가입 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignUp() {
        val name = binding.signUpNicknameEt.text.toString()
        val emailId = binding.signUpIdEt.text.toString()
        val emailDomain = binding.signUpDirectInputEt.text.toString()
        val password = binding.signUpPasswordEt.text.toString()
        val passwordCheck = binding.signUpPasswordCheckEt.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (emailId.isEmpty() || emailDomain.isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못 되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != passwordCheck) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val email = "$emailId@$emailDomain"

        // 모든 검사를 통과하면, ViewModel에게 회원가입을 명령합니다.
        viewModel.signUp(name, email, password)
    }
}
