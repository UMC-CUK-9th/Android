package com.example.umc_android_mission2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // 로그인 성공 시 얻는 사용자 정보
    var memberId: Int? = null
    var accessToken: String? = null
    var name: String? = null

    // 회원가입 결과 추적
    private val _signUpResult = MutableLiveData<Result<MemberIdResult>>()
    val signUpResult: LiveData<Result<MemberIdResult>> = _signUpResult

    // 로그인 결과 추적
    private val _loginResult = MutableLiveData<Result<LoginResult>>()
    val loginResult: LiveData<Result<LoginResult>> = _loginResult

    // JWT 테스트 결과 추적
    private val _testTokenResult = MutableLiveData<Result<TestResult>>()
    val testTokenResult: LiveData<Result<TestResult>> = _testTokenResult

    // 유저 정보 수정 결과 추적
    private val _updateUserResult = MutableLiveData<Result<MemberIdResult>>()
    val updateUserResult: LiveData<Result<MemberIdResult>> = _updateUserResult

    // 유저 정보 삭제 결과 추적
    private val _deleteUserResult = MutableLiveData<Result<Unit>>()
    val deleteUserResult: LiveData<Result<Unit>> = _deleteUserResult

    /** API 호출 함수 **/
    
    // 회원가입
    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            val request = SignUpRequest(name, email, password)
            val result = repository.SignUp(request)
            _signUpResult.postValue(result)
        }
    }

    // 로그인
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val request = LoginRequest(email, password)
            val result = repository.Login(request)

            // 로그인 성공 시, ViewModel에 사용자 정보 저장
            result.onSuccess { loginData ->
                memberId = loginData.memberId
                accessToken = loginData.accessToken
                name = loginData.name
            }
            
            _loginResult.postValue(result)
        }
    }

    // 헤더 테스트
    fun testToken(token: String) {
        viewModelScope.launch {
            val result = repository.TestToken(token)
            _testTokenResult.postValue(result)
        }
    }

    // 유저 정보 수정
    fun updateUser(token: String, memberId: Int, newName: String, newPassword: String) {
        viewModelScope.launch {
            val request = UserUpdateRequest(memberId, newName, newPassword)
            val result = repository.UpdateUser(token, request)
            
            // 정보 수정 성공 시, ViewModel의 name도 업데이트
            result.onSuccess {
                if (newName.isNotBlank()) {
                    name = newName
                }
            }

            _updateUserResult.postValue(result)
        }
    }

    // 유저 정보 삭제 (회원탈퇴)
    fun deleteUser(token: String, memberId: Int, password: String) {
        viewModelScope.launch {
            val result = repository.DeleteUser(token, memberId, password)
            _deleteUserResult.postValue(result)
        }
    }
}
