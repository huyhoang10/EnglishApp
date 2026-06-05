package com.example.efishapp.feature.dashboard.data.repository

import android.util.Log
import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.DayOfWeek
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyTrackerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WeeklyTrackerRepository {

    private val collectionRef = firestore.collection("weekly_study_tracker")

    override suspend fun getWeeklyStats(userId: String): List<DailyVocabTracker>? {
        return try {
            val documentSnapshot = collectionRef.document(userId).get().await()
            if (!documentSnapshot.exists()) return null

            val rawWeeklyStats = documentSnapshot.get("weekly_stats") as? Map<String, Any?>
            if (rawWeeklyStats != null) {
                DayOfWeek.entries.mapNotNull { day ->
                    val dayKey = day.name
                    val dayData = rawWeeklyStats[dayKey] as? Map<String, Any?>
                    if (dayData != null) {
                        val newCount = (dayData["newVocabCount"] as? Long)?.toInt() ?: 0
                        val reviewCount = (dayData["reviewVocabCount"] as? Long)?.toInt() ?: 0
                        DailyVocabTracker(dayOfWeek = day, newVocabCount = newCount, reviewVocabCount = reviewCount)
                    } else null
                }
            } else null
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error fetching weekly stats: ${e.message}")
            null
        }
    }

    override suspend fun initializeWeeklyStats(userId: String): List<DailyVocabTracker> {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        val initialMap = DayOfWeek.entries.associate { day ->
            day.name to mapOf("newVocabCount" to 0, "reviewVocabCount" to 0)
        }

        val initialData = mapOf(
            "weekly_stats" to initialMap,
            "lastResetWeek" to currentWeek,
            "lastResetYear" to currentYear
        )

        try {
            collectionRef.document(userId).set(initialData).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error initializing data: ${e.message}")
        }

        return DayOfWeek.entries.map { DailyVocabTracker(dayOfWeek = it, newVocabCount = 0, reviewVocabCount = 0) }
    }

    override suspend fun updateWeeklyStats(userId: String, dailyVocabTracker: DailyVocabTracker) {
        val dayKey = dailyVocabTracker.dayOfWeek.name

        val updates = mapOf(
            "weekly_stats.$dayKey.newVocabCount" to dailyVocabTracker.newVocabCount,
            "weekly_stats.$dayKey.reviewVocabCount" to dailyVocabTracker.reviewVocabCount
        )
        try {
            collectionRef.document(userId).update(updates).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error updating daily stats: ${e.message}")
        }
    }

    override suspend fun resetAndBackupWeeklyStats(userId: String, currentStats: List<DailyVocabTracker>) {
        val lastWeeklyMap = currentStats.associate { tracker ->
            tracker.dayOfWeek.name to mapOf(
                "newVocabCount" to tracker.newVocabCount,
                "reviewVocabCount" to tracker.reviewVocabCount
            )
        }
        try {
            collectionRef.document(userId).update("last_weekly_stats", lastWeeklyMap).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error backing up weekly stats: ${e.message}")
        }
    }
}