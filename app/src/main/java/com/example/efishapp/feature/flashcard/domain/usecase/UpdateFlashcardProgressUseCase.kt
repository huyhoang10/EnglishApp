package com.example.efishapp.feature.flashcard.domain.usecase

import android.util.Log
import com.example.efishapp.core.util.getTodayStr
import com.example.efishapp.feature.dashboard.domain.UserAnalyticsRepository
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardRepository
import com.example.efishapp.feature.flashcard.domain.model.VocabularyReview
import com.example.efishapp.feature.flashcard.presentation.UserActionSnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

class UpdateFlashcardProgressUseCase @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val weeklyTrackerRepository: WeeklyTrackerRepository,
    private val userAnalyticsRepository: UserAnalyticsRepository
) {
    suspend operator fun invoke(
        userId: String,
        actionSnapshots: List<UserActionSnapshot>
    ) {
        if (actionSnapshots.isEmpty()) return

        val todayStr = getTodayStr()
        val vocabIds = actionSnapshots.map { it.vocabId }

        // 1. Lấy toàn bộ tiến trình học của danh sách từ trong 1 lần gọi duy nhất
        val progressMap = flashcardRepository.getFlashcardProgressList(userId, vocabIds)
            .associateBy { it.vocabularyId }

        Log.d("Debug_UpdateWeekly", "${progressMap.size}")

        var newVocabCount = 0
        var reviewVocabCount = 0
        val updatedProgressList = mutableListOf<VocabularyReview>()

        // 2. Duyệt qua danh sách để tính toán logic trên Bộ nhớ (In-memory)
        for (actionSnapshot in actionSnapshots) {
            val currentProgress = progressMap[actionSnapshot.vocabId] ?: continue
            val isActionAgain = (actionSnapshot.actionType == ActionType.AGAIN)

            val isNewVocab = currentProgress.learnAt.isBlank()

            // CHỈ chặn review nếu đó là từ cũ, sai ngày hẹn VÀ action truoc không phải bấm "AGAIN"
            if (!isNewVocab && currentProgress.nextReviewDate != todayStr && !isActionAgain) {
                continue
            }

            if (isNewVocab) {
                newVocabCount++
            } else if (currentProgress.learnAt != todayStr) {
                reviewVocabCount++
            }

            // Tính toán SM2 và thêm vào danh sách cập nhật
            val updatedProgress = calculateSM2(currentProgress, actionSnapshot.actionType, todayStr)
            updatedProgressList.add(updatedProgress)
        }

        // 3. Cập nhật hàng loạt tiến trình học xuống DB nếu có sự thay đổi
        if (updatedProgressList.isNotEmpty()) {
            Log.d("Debug_UpdateWeekly", "${updatedProgressList.size}")
            flashcardRepository.updateFlashcardProgressList(userId, updatedProgressList)
        }

        // 4. Cập nhật cộng dồn thống kê tuần (Weekly Stats)
        if (newVocabCount > 0 || reviewVocabCount > 0) {
            Log.d("Debug_UpdateWeekly", "$newVocabCount, $reviewVocabCount")
            weeklyTrackerRepository.updateWeeklyStats(userId, newVocabCount, reviewVocabCount)
        }

        // 5. Cập nhat totalWord
        if(newVocabCount > 0){
            userAnalyticsRepository.updateTotalWords(userId,newVocabCount)
        }
    }

    private fun calculateSM2(
        currentProgress: VocabularyReview,
        actionType: ActionType,
        todayStr: String
    ): VocabularyReview {
        val oldRepetitions = currentProgress.repetitions
        val oldInterval = currentProgress.intervalDays
        val oldEF = currentProgress.easinessFactor

        val quality = actionType.quality
        val nextRepetitions = if (quality >= 1) oldRepetitions + 1 else 0

        val nextIntervalDays = when (nextRepetitions) {
            0 -> 0
            1 -> 1
            2 -> 6
            else -> max(1, (oldInterval * oldEF).toInt())
        }

        val adjustedQuality = (quality + 2).coerceIn(0, 5)
        val qFactor = 5 - adjustedQuality
        val newEF = oldEF + (0.1f - qFactor * (0.08f + qFactor * 0.02f))
        val finalEF = max(1.3f, newEF)

        val calendar = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DAY_OF_YEAR, nextIntervalDays)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val nextReviewDate = sdf.format(calendar.time)

        return currentProgress.copy(
            learnAt = todayStr,
            repetitions = nextRepetitions,
            easinessFactor = finalEF,
            intervalDays = nextIntervalDays,
            nextReviewDate = nextReviewDate
        )
    }
}