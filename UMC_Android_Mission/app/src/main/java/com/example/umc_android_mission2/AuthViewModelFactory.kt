package com.example.umc_android_mission2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 요청된 ViewModel 클래스가 AuthViewModel 클래스와 같거나 그 하위 클래스인지 확인
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // 외부에서 주입받은 AuthRepository를 사용하여 AuthViewModel 인스턴스를 생성
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        // 모르는 ViewModel 클래스가 요청되면 예외를 발생시킴
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
