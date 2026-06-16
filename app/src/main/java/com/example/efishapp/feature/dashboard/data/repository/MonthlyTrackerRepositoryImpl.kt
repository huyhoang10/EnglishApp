package com.example.efishapp.feature.dashboard.data.repository

import android.util.Log
import com.example.efishapp.feature.dashboard.domain.MonthTrackerRepository
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker
import com.example.efishapp.feature.flashcard.domain.model.VocabularyReview
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonthlyTrackerRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : MonthTrackerRepository {

    private val collectionRef = db.collection("monthly_study_tracker")

    override suspend fun getMonthStats(userId: String, month: Int): MonthlyStudyTracker? {
        return try {
            Log.e("Firestore_Debug", "Error fetching monthly stats: ${month}")
            val snapshot = collectionRef.document(userId).get().await()
            if (snapshot.exists()) {
                val monthlyStatsMap = snapshot.get("monthly_stats") as? Map<String, Any>
                val currentMonthMap = monthlyStatsMap?.get(month.toString()) as? Map<String, Any>
                Log.e("Firestore_Debug", "Error fetching monthly stats: ${currentMonthMap.toString()}")
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

    override suspend fun updateMonthlyAccuracy(userId: String, month: Int, correctCount: Long, wrongCount: Long) {
        val updates = mapOf(
            "monthly_stats.$month.correctVocabCount" to correctCount,
            "monthly_stats.$month.wrongVocabCount" to wrongCount
        )
        try {
            collectionRef.document(userId).update(updates).await()
        } catch (e: Exception) {
            Log.e("MonthlyTrackerRepositoryImpl", "Error updating monthly accuracy: ${e.message}")
        }
    }

    override suspend fun updateStreak(userId: String, streak: Int) {
        try {
            collectionRef.document(userId).update("streak", streak).await()
        } catch (e: Exception) {
            Log.e("Firestore_Debug", "Error updating streak: ${e.message}")
        }
    }

    override suspend fun getVocabularyInCurrentMonthString(userId: String): List<VocabularyReview> {
        val db = FirebaseFirestore.getInstance()

        val calendar = Calendar.getInstance()
        val yearAndMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        val startPrefix = "$yearAndMonth-01"
        val endPrefix = "$yearAndMonth-31"

        return try {
            val querySnapshot = db.collection("user_review")
                .document(userId)
                .collection("vocab_review")
                .orderBy("learnAt")
                .startAt(startPrefix)
                .endAt(endPrefix)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(VocabularyReview::class.java)
            }
        } catch (e: Exception) {
            Log.d("Exception_Monthly_Repo","${e.message}" )
            return emptyList()
        }
    }

}