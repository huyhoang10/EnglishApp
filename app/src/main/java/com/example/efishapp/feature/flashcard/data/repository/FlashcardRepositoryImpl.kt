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

    override suspend fun getFlashcardProgressList(
        userId: String,
        vocabularyIds: List<String>
    ): List<VocabularyReview> {
        if (userId.isBlank() || vocabularyIds.isEmpty()) return emptyList()

        // Lọc bỏ các ID bị trống để tránh lỗi Firestore
        val validIds = vocabularyIds.filter { it.isNotBlank() }.distinct()
        if (validIds.isEmpty()) return emptyList()

        val resultList = mutableListOf<VocabularyReview>()

        try {
            val collectionRef = firestore.collection("user_review")
                .document(userId)
                .collection("vocab_review")

            // Firestore giới hạn `whereIn` tối đa 30 phần tử trong 1 Query.
            // Cần chia nhỏ danh sách thành từng cụm tối đa 30 ID.
            val chunks = validIds.chunked(30)

            for (chunk in chunks) {
                val querySnapshot = collectionRef
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                // Chuyển đổi các Document tìm thấy sang Object
                val reviews = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(VocabularyReview::class.java)
                }
                resultList.addAll(reviews)
            }

            // Bổ sung các VocabularyReview mặc định cho những ID chưa tồn tại trên Firestore
            val foundIds = resultList.map { it.vocabularyId }.toSet() // Giả định trường trong object là vocabularyId hoặc vocabId
            val missingReviews = validIds
                .filter { it !in foundIds }
                .map { VocabularyReview(vocabularyId = it) }

            resultList.addAll(missingReviews)

        } catch (e: Exception) {
            Log.e("DEBUG_REPO_GET_LIST", e.message.toString())
            throw e
        }

        return resultList
    }

    override suspend fun updateFlashcardProgressList(
        userId: String,
        vocabularyReviews: List<VocabularyReview>
    ) {
        if (userId.isBlank() || vocabularyReviews.isEmpty()) return

        try {
            val collectionRef = firestore.collection("user_review")
                .document(userId)
                .collection("vocab_review")

            // Firestore giới hạn tối đa 500 thao tác ghi trong 1 Batch.
            // Chia nhỏ danh sách thành các cụm tối đa 500 phần tử để xử lý an toàn.
            val chunks = vocabularyReviews.chunked(500)

            for (chunk in chunks) {
                val batch = firestore.batch()

                for (review in chunk) {
                    if (review.vocabularyId.isBlank()) continue

                    val docRef = collectionRef.document(review.vocabularyId)
                    batch.set(docRef, review) // Đưa lệnh ghi của từng document vào Batch
                }

                // Gửi toàn bộ cụm dữ liệu này lên Firestore trong 1 Request duy nhất
                batch.commit().await()
            }

            Log.d("DEBUG_REPO_UPDATE_LIST", "Updated ${vocabularyReviews.size} flashcards successfully.")
        } catch (e: Exception) {
            Log.e("DEBUG_REPO_UPDATE_LIST", e.message.toString())
            throw e
        }
    }
}