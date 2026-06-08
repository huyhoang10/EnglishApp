package com.example.efishapp.feature.flashcard.domain.usecase

import android.util.Log
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardLocalRepository
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardRepository
import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import javax.inject.Inject

class GetVocabularyReviewUseCase @Inject constructor(
    private val remoteRepository: FlashcardRepository,
    private val localRepository: FlashcardLocalRepository
) {
    suspend operator fun invoke(userId: String): FlashcardSessionResult {
//        val localCache = localRepository.getCurrentSession(userId, "")
//
//        if (localCache != null) {
//            // Nếu có tiến trình dở dang -> Trả về data khôi phục ngay lập tức
//            return FlashcardSessionResult.ContinueSession(
//                currentIndex = localCache.currentIndex,
//                countForget = localCache.countForget,
//                countRemember = localCache.countRemember,
//                vocabularies = localCache.vocabularies,
//                savedAnswers = localCache.userAnswers
//            )
//        }

        // Learn from remote
        val remoteVocabs = remoteRepository.getVocabulariesReview(userId)
        Log.d("DEBUG_TAG","${remoteVocabs.toString()}")
        return FlashcardSessionResult.NewSession(vocabularies = remoteVocabs)
    }


}

sealed interface FlashcardSessionResult {
    data class ContinueSession(
        val currentIndex: Int,
        val countForget: Int,
        val countRemember: Int,
        val vocabularies: List<Vocabulary>,
        val savedAnswers: Map<String, ActionType>
    ) : FlashcardSessionResult

    data class NewSession(val vocabularies: List<Vocabulary>) : FlashcardSessionResult
}