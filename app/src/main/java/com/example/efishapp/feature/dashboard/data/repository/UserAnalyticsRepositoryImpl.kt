package com.example.efishapp.feature.dashboard.data.repository

import android.util.Log
import com.example.efishapp.feature.dashboard.domain.UserAnalytics
import com.example.efishapp.feature.dashboard.domain.UserAnalyticsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAnalyticsRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UserAnalyticsRepository {

    private val analyticsCollection = db.collection("user_analytics")
    private val usersCollection = db.collection("users")

    override suspend fun getUserAnalytics(userId: String): UserAnalytics? {
        return try {
            val snapshot = analyticsCollection.document(userId).get().await()
            if (snapshot.exists()) {
                UserAnalytics(
                    userId = userId,
                    streak = (snapshot.getLong("streak") ?: 0L).toLong(),
                    highestStreak = (snapshot.getLong("highestStreak") ?: 0L).toLong(),
                    totalVocabLearned = snapshot.getLong("totalWordsLearned") ?: 0L,
                    lastActiveDate = snapshot.getString("lastActiveDate") ?: ""
                )
            } else null
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error getting user analytics: ${e.message}")
            null
        }
    }

    override suspend fun initializeUserAnalytics(userId: String): UserAnalytics {
        val initialData = mapOf(
            "streak" to 0,
            "highestStreak" to 0,
            "totalWordsLearned" to 0L,
            "lastActiveDate" to ""
        )
        try {
            analyticsCollection.document(userId).set(initialData).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error initializing user analytics: ${e.message}")
        }
        return UserAnalytics(userId = userId)
    }

    override suspend fun updateStreakAndActivity(userId: String, streak: Long, highestStreak: Long, lastActiveDate: String) {
        val updates = mapOf(
            "streak" to streak,
            "highestStreak" to highestStreak,
            "lastActiveDate" to lastActiveDate
        )
        try {
            analyticsCollection.document(userId).update(updates).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error updating streak and activity: ${e.message}")
        }
    }

    override suspend fun updateTotalWords(userId: String, totalWords: Long) {
        try {
            analyticsCollection.document(userId).update("totalWordsLearned", totalWords).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error updating total words: ${e.message}")
        }
    }

    override suspend fun getUserName(userId: String): String {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            snapshot.getString("fullName") ?: "User"
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error getting user name: ${e.message}")
            "User"
        }
    }
}