package com.example.umc_android_mission2

import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    // 회원가입
    @POST("signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse<MemberIdResult>>

    // 로그인
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse<LoginResult>>

    // 토큰 테스트 (JWT)
    @GET("test")
    suspend fun testToken(@Header("Authorization") token: String): Response<AuthResponse<TestResult>>

    // 유저 정보 수정
    @PATCH("users")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body request: UserUpdateRequest
    ): Response<AuthResponse<MemberIdResult>>

    // 회원 탈퇴
    @DELETE("users")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Header("memberId") memberId: Int,
        @Header("password") password: String
    ): Response<AuthResponse<Unit>>
}
