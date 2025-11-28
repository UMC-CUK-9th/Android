package com.example.umc_android_mission2

import com.google.gson.annotations.SerializedName

// 모든 API의 기본 응답 구조
data class AuthResponse<T>(
    @SerializedName("status") val status: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

// 로그인 성공 시 data 필드에 들어갈 데이터
data class LoginResult(
    @SerializedName("name") val name: String,
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("accessToken") val accessToken: String
)

// 회원가입, 유저 정보 수정 성공 시 data 필드에 들어갈 데이터
data class MemberIdResult(
    @SerializedName("memberId") val memberId: Int
)

// 토큰 테스트 성공 시 data 필드에 들어갈 데이터
data class TestResult(
    @SerializedName("result") val result: String
)

// --- 요청(Request) 데이터 클래스 ---

data class SignUpRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class UserUpdateRequest(
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("newName") val newName: String,
    @SerializedName("newPassword") val newPassword: String
)
