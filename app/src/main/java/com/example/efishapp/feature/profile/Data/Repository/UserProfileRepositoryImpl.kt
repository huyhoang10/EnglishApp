package com.example.efishapp.feature.profile.data.repository

import com.example.efishapp.feature.profile.domain.model.UserProfile
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserProfileRepository {

    override suspend fun getProfile(): UserProfile {
        val user = auth.currentUser ?: return UserProfile()

        return try {
            val doc = withTimeout(5000) {
                firestore.collection("users").document(user.uid).get().await()
            }
            if (doc.exists()) {
                UserProfile(
                    userId = user.uid,
                    email = user.email ?: "",
                    fullName = doc.getString("fullName") ?: user.displayName ?: "",
                    dateOfBirth = doc.getString("dateOfBirth") ?: "",
                    gender = doc.getString("gender") ?: "",
                    goal = doc.getString("goal") ?: "",
                    level = doc.getString("level") ?: "",
                    isCompleted = doc.getBoolean("isCompleted") ?: false
                )
            } else {
                UserProfile(
                    userId = user.uid,
                    email = user.email ?: "",
                    fullName = user.displayName ?: "",
                )
            }
        } catch (e: Exception) {
            UserProfile(
                userId = user.uid,
                email = user.email ?: "",
                fullName = user.displayName ?: "",
            )
        }
    }

    override suspend fun updateProfile(
        fullName: String?,
        dateOfBirth: String?,
        gender: String?,
        goal: String?,
        level: String?
    ): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))

            // Đồng bộ fullName vào displayName của Auth luôn
            if (fullName != null) {
                val request = userProfileChangeRequest {
                    this.displayName = fullName
                }
                user.updateProfile(request).await()
            }

            val updates = mutableMapOf<String, Any>()
            fullName?.let { updates["fullName"] = it }
            dateOfBirth?.let { updates["dateOfBirth"] = it }
            gender?.let { updates["gender"] = it }
            goal?.let { updates["goal"] = it }
            level?.let { updates["level"] = it }
            updates["isCompleted"] = true

            withTimeout(8000) {
                firestore.collection("users").document(user.uid)
                    .set(updates, SetOptions.merge())
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isProfileCompleted(): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            val doc = withTimeout(5000) {
                firestore.collection("users").document(user.uid).get().await()
            }
            doc.getBoolean("isCompleted") ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteUserProfile(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))
        return try {
            withTimeout(5000) {
                firestore.collection("users").document(user.uid).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFirebaseAuth(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))
        return try {
            // Kiểm tra xem phiên đăng nhập có quá cũ không (ví dụ > 5 phút)
            val lastSignIn = user.metadata?.lastSignInTimestamp ?: 0
            val fiveMinutesInMillis = 5 * 60 * 1000
            if (System.currentTimeMillis() - lastSignIn > fiveMinutesInMillis) {
                return Result.failure(Exception("RECENT_AUTH_REQUIRED"))
            }

            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Nếu Firebase trả về lỗi "requires-recent-login"
            if (e.message?.contains("recent authentication", ignoreCase = true) == true) {
                Result.failure(Exception("RECENT_AUTH_REQUIRED"))
            } else {
                Result.failure(e)
            }
        }
    }
}