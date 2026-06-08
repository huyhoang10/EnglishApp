package com.example.efishapp.feature.flashcard.data.repository

import android.util.Log
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardRepository
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.example.efishapp.feature.flashcard.domain.model.VocabularyReview
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FlashcardRepository {

override suspend fun getVocabulariesReview(userId: String): List<Vocabulary> {
    val DATE_FORMAT = "yyyy-MM-dd"
    val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val todayStr = sdf.format(Date())
    return try {
        val progressSnapshots = firestore.collection("user_review")
            .document(userId)
            .collection("vocab_review")
            .whereLessThanOrEqualTo("nextReviewDate", todayStr)
            .get()
            .await()

        val vocabIds = progressSnapshots.documents.map{it.id}
        Log.d("DEBUG_REPO",vocabIds.toString())
        if (vocabIds.isEmpty()) return emptyList()

        val vocabularySnapshots = firestore.collection("vocabulary")
            .whereIn(FieldPath.documentId(), vocabIds.take(30))
            .get()
            .await()

        // 3. Tự động parse sang list object (Sử dụng tính năng tự nhận ID của Cách 2 @DocumentId)
        val vocabularies = vocabularySnapshots.toObjects(Vocabulary::class.java)

        Log.d("DEBUG_REPO", "Đã lấy chi tiết thành công: ${vocabularies.size} từ vựng.")
        return vocabularies

    } catch (e: Exception) {
        // Luôn ghi log lỗi tại đây để dễ bắt bệnh trong Logcat nếu có trục trặc phát sinh
        Log.e("DEBUG_REPO", "getVocabulariesReview: ${e.message}", e)
        throw e
    }
}

    override suspend fun getVocabulariesFromFolder(folderId: String?): List<Vocabulary> {
        return try {
            val progressSnapshots = firestore.collection("vocabulary")
                .whereEqualTo("folderId", folderId)
                .get()
                .await()

            val vocabularies = progressSnapshots.toObjects(Vocabulary::class.java)
            return vocabularies

        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getFlashcardProgress(userId: String, vocabularyId: String): VocabularyReview {
        if (userId.isBlank() || vocabularyId.isBlank()) {
            //Log.e("DEBUG_REPO_GET", "Không thể lấy tiến độ: userId hoặc vocabularyId bị rỖNG!")
            return VocabularyReview(vocabularyId = vocabularyId)
        }
        return try {
            val documentSnapshot = firestore.collection("user_review")
                .document(userId)
                .collection("vocab_review")
                .document(vocabularyId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(VocabularyReview::class.java)
                    ?: VocabularyReview(vocabularyId = vocabularyId)
            } else {
                VocabularyReview(vocabularyId = vocabularyId)
            }
        } catch (e: Exception) {
            Log.d("DEBUG_REPO_GET",e.message.toString())
            throw e
        }
    }

    override suspend fun updateFlashcardProgress(userId: String, vocabularyReview: VocabularyReview) {
        if (userId.isBlank() || vocabularyReview == null) {
            return
        }
        try {
            firestore.collection("user_review")
                .document(userId)
                .collection("vocab_review")
                .document(vocabularyReview.vocabularyId)
                .set(vocabularyReview) // Ghi đè hoặc tạo mới nếu chưa tồn tại
                .await()
        } catch (e: Exception) {
            Log.d("DEBUG_REPO_UPDATE",e.message.toString())
            throw e
        }
    }
}