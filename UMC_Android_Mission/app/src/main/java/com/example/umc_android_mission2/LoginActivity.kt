package com.example.umc_android_mission2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.umc_android_mission2.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val repository by lazy { AuthRepository(ApiClient.authService) }
    private val viewModelFactory by lazy { AuthViewModelFactory(repository) }
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeLoginResult()

        binding.loginCloseIv.setOnClickListener {
            finish()
        }

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginSignInBtn.setOnClickListener {
            handleLogin()
        }
        
        //카카오 로그인 기능
        binding.loginKakakoLoginIv.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("LOGIN/KAKAO", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("LOGIN/KAKAO", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    // 사용자 정보 요청 함수 호출
                    getKakaoUserInfo()
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("LOGIN/KAKAO", "카카오톡으로 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i("LOGIN/KAKAO", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        // 사용자 정보 요청 함수 호출
                        getKakaoUserInfo()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    // 카카오 사용자 정보 가져오기 함수
    private fun getKakaoUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KAKAO_API", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("KAKAO_API", "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")

                //  카카오 로그인 상태를 SharedPreferences에 저장
                setKakaoLoginState(true)
                // 사용자 정보 요청 성공 후 MainActivity로 이동
                startMainActivity()
            }
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { loginResult ->
                saveAuthInfo(loginResult.accessToken, loginResult.memberId)
                Toast.makeText(this, "${loginResult.name}님 환영합니다.", Toast.LENGTH_SHORT).show()
                startMainActivity()

            }.onFailure { exception ->
                Toast.makeText(this, "로그인 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLogin() {
        val emailId = binding.loginIdEt.text.toString()
        val emailDomain = binding.loginDirectInputEt.text.toString()
        val password = binding.loginPasswordEt.text.toString()

        if (emailId.isEmpty() || emailDomain.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val email = "$emailId@$emailDomain"
        viewModel.login(email, password)
    }

    private fun saveAuthInfo(accessToken: String, memberId: Int) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("accessToken", accessToken)
        editor.putInt("memberId", memberId)
        editor.apply()
    }

    // 카카오 로그인 상태 저장을 위한 함수
    private fun setKakaoLoginState(isLoggedIn: Boolean) {
        val spf = getSharedPreferences("auth_kakao", MODE_PRIVATE)
        spf.edit().putBoolean("isKakaoLoggedIn", isLoggedIn).apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
