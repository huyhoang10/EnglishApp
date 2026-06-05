package com.example.efishapp.feature.flashcard.domain

import com.example.efishapp.feature.flashcard.presentation.Vocabulary
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.max

enum class ActionType(val quality: Int) {
    AGAIN(0),
    HARD(1),
    GOOD(2),
    EASY(3)
}

class GetVocabularyReviewUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(userId: String): List<Vocabulary> {
        return repository.getVocabulariesReview(userId)
    }
}

class UpdateFlashcardProgressUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(
        userId: String,
        vocabularyId: String,
        actionType: ActionType
    ) {
        val quality = actionType.quality
        val currentProgress: VocabularyReview? = repository.getFlashcardProgress(userId, vocabularyId)

        // Nếu chưa từng học (null), lấy giá trị mặc định ban đầu
        val oldRepetitions = currentProgress?.repetitions ?: 0
        val oldInterval = currentProgress?.intervalDays ?: 0
        val oldEF = currentProgress?.easinessFactor ?: 2.5f

        // 1. Tính toán số lần lặp liên tiếp (repetitions)
        val nextRepetitions = if (quality >= 2) oldRepetitions + 1 else 0

        // 2. Tính toán khoảng thời gian ôn tập tiếp theo (intervalDays)
        val nextIntervalDays = when (nextRepetitions) {
            0 -> 0 // Học lại ngay trong ngày hoặc đưa vào hàng đợi gần
            1 -> 1 // 1 ngày sau
            2 -> 6 // 6 ngày sau
            else -> max(1, (oldInterval * oldEF).toInt())
        }

        // 3. Tính toán Hệ số dễ mới (Easiness Factor - EF) theo thuật toán SM-2
        // Thang 4 mức (0 đến 3) tương ứng chất lượng gốc (2 đến 5)
        val adjustedQuality = quality + 2
        val qFactor = 5 - adjustedQuality
        val newEF = oldEF + (0.1f - qFactor * (0.08f + qFactor * 0.02f))
        val finalEF = max(1.3f, newEF)

        // 4. Tính toán ngày cần ôn tập tiếp theo
        val calendar = Calendar.getInstance()
        calendar.time = Date() // Ngày hôm nay
        calendar.add(Calendar.DAY_OF_YEAR, nextIntervalDays)
        val nextReviewDate = calendar.time

        // Tạo object tiến độ mới để cập nhật xuống DB
        val updatedProgress = VocabularyReview(
            vocabularyId = vocabularyId,
            repetitions = nextRepetitions,
            easinessFactor = finalEF,
            intervalDays = nextIntervalDays,
            nextReviewDate = nextReviewDate
        )

        // Cập nhật thông qua Repository
        repository.updateFlashcardProgress(userId,updatedProgress)
    }
}