package com.example.efishapp.feature.Auth.Data.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRepository {
    override suspend fun loginWithEmail(email: String, password: String): Result<Unit> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null && user.isEmailVerified) {
                Result.success(Unit)
            } else {
                firebaseAuth.signOut()
                Result.failure(Exception("Tài khoản chưa được kích hoạt. Vui lòng xác thực email trước khi đăng nhập."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            // 1. Tiền kiểm tra các phương thức đăng nhập của email này trên hệ thống
            val signInMethods = firebaseAuth.fetchSignInMethodsForEmail(email).await().signInMethods ?: emptyList()

            // 2. Phân tách và chặn trước khi Firebase kịp ném ngoại lệ loằng ngoằng
            if (signInMethods.contains("google.com")) {
                return Result.failure(Exception("GOOGLE_COLLISION"))
            }
            if (signInMethods.contains("password")) {
                return Result.failure(Exception("PASSWORD_COLLISION"))
            }

            // 3. Nếu kiểm tra sạch sẽ, tiến hành tạo tài khoản bình thường
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.sendEmailVerification()?.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Mọi loại lỗi (bao gồm cả Collision nếu có phát sinh ngầm)
            // sẽ được ném thẳng lên ViewModel để xử lý tập trung
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun clearSession(): Result<Unit> {
        return try {
            // Lệnh chuẩn của Firebase để xóa token, cookie và đăng xuất hoàn toàn
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun isUserLoggedIn(): Boolean {
        // Trả về true nếu Firebase đang giữ token của user hiện tại
        return firebaseAuth.currentUser != null
    }


}