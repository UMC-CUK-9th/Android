package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_android_mission2.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // 1. SignUpActivity와 동일하게 ViewModel과 부품들을 선언합니다.
    private val repository by lazy { AuthRepository(ApiClient.authService) }
    private val viewModelFactory by lazy { AuthViewModelFactory(repository) }
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. 로그인 API의 호출 결과를 관찰(observe)하도록 설정합니다.
        observeLoginResult()

        binding.loginCloseIv.setOnClickListener {
            finish()
        }

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 3. 로그인 버튼 클릭 시, 새로운 handleLogin() 함수를 호출합니다.
        binding.loginSignInBtn.setOnClickListener {
            handleLogin()
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { loginResult ->
                // 4. 로그인 성공 시:
                //    - 서버에서 받은 AccessToken과 memberId를 저장합니다.
                //    - 환영 토스트 메시지를 보여줍니다.
                //    - MainActivity로 이동합니다.
                saveAuthInfo(loginResult.accessToken, loginResult.memberId)
                Toast.makeText(this, "${loginResult.name}님 환영합니다.", Toast.LENGTH_SHORT).show()
                startMainActivity()

            }.onFailure { exception ->
                // 5. 로그인 실패 시: 에러 메시지를 보여줍니다.
                Toast.makeText(this, "로그인 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLogin() {
        // 개발자님의 기존 코드와 동일하게 두 EditText에서 값을 가져옵니다.
        val emailId = binding.loginIdEt.text.toString()
        val emailDomain = binding.loginDirectInputEt.text.toString()
        val password = binding.loginPasswordEt.text.toString()

        // 유효성 검사도 기존 로직을 그대로 사용합니다.
        if (emailId.isEmpty() || emailDomain.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // 두 부분을 합쳐서 완전한 이메일 주소를 만듭니다.
        val email = "$emailId@$emailDomain"

        // 6. 모든 검사를 통과하면, ViewModel에게 로그인을 하라고 명령합니다.
        viewModel.login(email, password)
    }

    // 7. 서버에서 받은 인증 정보(AccessToken, memberId)를 SharedPreferences에 저장하는 함수
    private fun saveAuthInfo(accessToken: String, memberId: Int) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("accessToken", accessToken)
        editor.putInt("memberId", memberId)
        editor.apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // MainActivity로 이동하면서 이전의 모든 액티비티 스택을 지웁니다.
        // 이렇게 하면 MainActivity에서 뒤로가기 버튼을 눌렀을 때 로그인 화면으로 돌아오지 않습니다.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
