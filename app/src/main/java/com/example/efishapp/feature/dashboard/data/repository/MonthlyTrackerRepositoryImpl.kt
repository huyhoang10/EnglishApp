package com.example.efishapp.feature.dashboard.data.repository

import android.util.Log
import com.example.efishapp.feature.dashboard.domain.MonthTrackerRepository
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonthlyTrackerRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : MonthTrackerRepository {

    private val collectionRef = db.collection("monthly_study_tracker")

    override suspend fun getMonthStats(userId: String, month: Int): MonthlyStudyTracker? {
        return try {
            val snapshot = collectionRef.document(userId).get().await()
            if (snapshot.exists()) {
                val monthlyStatsMap = snapshot.get("monthly_stats") as? Map<String, Any>
                val currentMonthMap = monthlyStatsMap?.get(month.toString()) as? Map<String, Any>

                if (currentMonthMap != null) {
                    MonthlyStudyTracker(
                        userId = userId,
                        year = Calendar.getInstance().get(Calendar.YEAR),
                        month = month,
                        correctVocabCount = (currentMonthMap["correctVocabCount"] as? Long) ?: 0L,
                        wrongVocabCount = (currentMonthMap["wrongVocabCount"] as? Long) ?: 0L
                    )
                } else null
            } else null
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error fetching monthly stats: ${e.message}")
            null
        }
    }

    override suspend fun initializeMonthlyStats(userId: String, month: Int): MonthlyStudyTracker {
        val initialMonthData = mapOf(
            "correctVocabCount" to 0L,
            "wrongVocabCount" to 0L
        )
        try {
            val docRef = collectionRef.document(userId)
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                docRef.update("monthly_stats.$month", initialMonthData).await()
            } else {
                val initialData = mapOf("monthly_stats" to mapOf(month.toString() to initialMonthData))
                docRef.set(initialData).await()
            }
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error initializing monthly stats: ${e.message}")
        }
        return MonthlyStudyTracker(userId = userId, month = month)
    }

    override suspend fun incrementMonthlyAccuracy(userId: String, month: Int, correctCount: Long, wrongCount: Long) {
        val updates = mapOf(
            "monthly_stats.$month.correctVocabCount" to FieldValue.increment(correctCount),
            "monthly_stats.$month.wrongVocabCount" to FieldValue.increment(wrongCount)
        )
        try {
            collectionRef.document(userId).update(updates).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error incrementing monthly accuracy: ${e.message}")
        }
    }

    override suspend fun updateStreak(userId: String, streak: Int) {
        try {
            collectionRef.document(userId).update("streak", streak).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error updating streak: ${e.message}")
        }
    }
}