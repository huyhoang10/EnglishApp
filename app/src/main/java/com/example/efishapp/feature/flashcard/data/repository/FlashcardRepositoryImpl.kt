package com.example.efishapp.feature.flashcard.data.repository

import com.example.efishapp.feature.flashcard.domain.FlashcardProgress
import com.example.efishapp.feature.flashcard.domain.FlashcardRepository
import com.example.efishapp.feature.flashcard.presentation.Vocabulary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FlashcardRepository {

    override suspend fun getVocabulariesReview(userId: String): List<Vocabulary> {
        val today = Date()
        val reviewVocabularies = mutableListOf<Vocabulary>()

        try {
            // 1. Truy cập vào sub-collection tiến độ học của User cụ thể
            // Lọc điều kiện: nextReviewDate <= ngày hôm nay
            val progressSnapshots = firestore.collection("flashcards")
                .document(userId)
                .collection("progress")
                .whereLessThanOrEqualTo("nextReviewDate", today)
                .get()
                .await()

            // Lấy danh sách các vocabularyId cần phải ôn tập
            val vocabularyIds = progressSnapshots.documents.mapNotNull { doc ->
                doc.getString("vocabularyId")
            }

            if (vocabularyIds.isEmpty()) return emptyList()

            // 2. Lấy thông tin chi tiết của từng từ vựng từ collection "vocabularies" gốc
            // Firestore giới hạn toán tử 'whereIn' tối đa 30 phần tử mỗi query.
            // Để an toàn và tối ưu, ta chia nhỏ danh sách Id ra nếu số lượng từ cần ôn tập quá lớn.
            val chunks = vocabularyIds.chunked(30)
            for (chunk in chunks) {
                val vocabSnapshots = firestore.collection("vocabularies")
                    .whereIn("id", chunk)
                    .get()
                    .await()

                val vocabs = vocabSnapshots.toObjects(Vocabulary::class.java)
                reviewVocabularies.addAll(vocabs)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Có thể xử lý hoặc ném custom exception tùy kiến trúc dự án của bạn
        }

        return reviewVocabularies
    }

    override suspend fun getFlashcardProgress(userId: String, vocabularyId: String): FlashcardProgress {
        return try {
            val documentSnapshot = firestore.collection("flashcards")
                .document(userId)
                .collection("progress")
                .document(vocabularyId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                // Map từ document Firestore sang Object Kotlin
                documentSnapshot.toObject(FlashcardProgress::class.java)
                    ?: FlashcardProgress(userId = userId, vocabularyId = vocabularyId)
            } else {
                // Nếu chưa từng học từ này, trả về object mặc định ban đầu
                FlashcardProgress(userId = userId, vocabularyId = vocabularyId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            FlashcardProgress(userId = userId, vocabularyId = vocabularyId)
        }
    }

    override suspend fun updateFlashcardProgress(flashcardProgress: FlashcardProgress) {
        try {
            // Lưu dữ liệu tiến độ vào sub-collection theo cấu trúc rõ ràng:
            // flashcards -> {userId} -> progress -> {vocabularyId}
            firestore.collection("flashcards")
                .document(flashcardProgress.userId)
                .collection("progress")
                .document(flashcardProgress.vocabularyId)
                .set(flashcardProgress) // Ghi đè hoặc tạo mới nếu chưa tồn tại
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}