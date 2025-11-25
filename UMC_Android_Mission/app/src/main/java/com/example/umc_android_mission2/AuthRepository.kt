package com.example.umc_android_mission2

import android.util.Log

class AuthRepository(private val service: AuthService) {

    // 1. 로그인
    suspend fun Login(req: LoginRequest): Result<LoginResult> = try {
        val response = service.login(req)

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.data == null) {
                val errorMessage = "Response body or data is null"
                Log.d("AuthRepository", errorMessage)
                Result.failure(RuntimeException(errorMessage))
            } else {
                Log.d("AuthRepository", "Login successful")
                Result.success(body.data)
            }
        } else {
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("AuthRepository", "Login failed: $errMsg")
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "Login exception", e)
        Result.failure(e)
    }

    // 2. 회원가입
    suspend fun SignUp(req: SignUpRequest): Result<MemberIdResult> = try {
        val response = service.signUp(req)

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.data == null) {
                val errorMessage = "Response body or data is null"
                Log.d("AuthRepository", errorMessage)
                Result.failure(RuntimeException(errorMessage))
            } else {
                Log.d("AuthRepository", "SignUp successful")
                Result.success(body.data)
            }
        } else {
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("AuthRepository", "SignUp failed: $errMsg")
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "SignUp exception", e)
        Result.failure(e)
    }

    // 3. JWT 테스트
    suspend fun TestToken(accessToken: String): Result<TestResult> = try {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.testToken(token)

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.data == null) {
                val errorMessage = "Response body or data is null"
                Log.d("AuthRepository", errorMessage)
                Result.failure(RuntimeException(errorMessage))
            } else {
                Log.d("AuthRepository", "Token test successful")
                Result.success(body.data)
            }
        } else {
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("AuthRepository", "Token test failed: $errMsg")
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "Token test exception", e)
        Result.failure(e)
    }

    // 4. 유저 정보 수정
    suspend fun UpdateUser(accessToken: String, req: UserUpdateRequest): Result<MemberIdResult> = try {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.updateUser(token, req)

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.data == null) {
                val errorMessage = "Response body or data is null"
                Log.d("AuthRepository", errorMessage)
                Result.failure(RuntimeException(errorMessage))
            } else {
                Log.d("AuthRepository", "User update successful")
                Result.success(body.data)
            }
        } else {
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("AuthRepository", "User update failed: $errMsg")
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "User update exception", e)
        Result.failure(e)
    }

    // 5. 유저 정보 삭제
    suspend fun DeleteUser(accessToken: String, memberId: Int, password: String): Result<Unit> = try {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.deleteUser(token, memberId, password)

        if (response.isSuccessful) {
            Log.d("AuthRepository", "User deletion successful")
            Result.success(Unit)
        } else {
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("AuthRepository", "User deletion failed: $errMsg")
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "User deletion exception", e)
        Result.failure(e)
    }
}
