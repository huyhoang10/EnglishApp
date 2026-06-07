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

        } catch (e: FirebaseAuthUserCollisionException) {
            try {
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    currentUser.linkWithCredential(credential).await()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Email này đã được đăng ký bằng Mật khẩu. Vui lòng đăng nhập bằng Mật khẩu trước để liên kết tài khoản."))
                }
            } catch (linkException: Exception) {
                Result.failure(linkException)
            }
        } catch (e: Exception) {
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


}