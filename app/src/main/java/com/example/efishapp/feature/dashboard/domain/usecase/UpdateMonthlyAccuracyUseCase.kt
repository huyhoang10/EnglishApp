package com.example.efishapp.feature.dashboard.domain.usecase

import android.util.Log
import com.example.efishapp.feature.dashboard.domain.MonthTrackerRepository
import java.util.Calendar
import javax.inject.Inject

class UpdateMonthlyAccuracyUseCase @Inject constructor(
    private val repository: MonthTrackerRepository
) {
    suspend operator fun invoke(userId: String) {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val vocabulariesReview = repository.getVocabularyInCurrentMonthString(userId)
        Log.d("DEBUG_MonthTrackerRepository",vocabulariesReview.size.toString())

        if (vocabulariesReview.isNotEmpty()) {
            var correctCount = 0
            var wrongCount = 0
            vocabulariesReview.forEach { vocabReview ->
                if (vocabReview.easinessFactor >= 2.5) {
                    correctCount++
                } else {
                    wrongCount++
                }
            }
            Log.d("DEBUG_MonthTrackerRepository",correctCount.toString())
            repository.updateMonthlyAccuracy(userId, currentMonth, correctCount.toLong(), wrongCount.toLong())
        }
    }
}