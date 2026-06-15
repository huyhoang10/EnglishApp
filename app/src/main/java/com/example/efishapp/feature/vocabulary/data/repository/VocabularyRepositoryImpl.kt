package com.example.efishapp.feature.vocabulary.data.repository

import android.util.Log
import com.example.efishapp.feature.vocabulary.domain.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VocabularyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VocabularyRepository {

    private val vocabularyCollection = firestore.collection("vocabulary")

    override suspend fun getVocabulariesByFolder(folderId: String): List<Vocabulary> {
        return try {
            val snapshot = vocabularyCollection
                .whereEqualTo("folderId", folderId)
                .get()
                .await()
            snapshot.toObjects(Vocabulary::class.java)
        } catch (e: Exception) {
            Log.e("VocabularyRepo", "getVocabulariesByFolder: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getVocabulary(vocabularyId: String): Vocabulary? {
        return try {
            val doc = vocabularyCollection.document(vocabularyId).get().await()
            if (doc.exists()) doc.toObject(Vocabulary::class.java) else null
        } catch (e: Exception) {
            Log.e("VocabularyRepo", "getVocabulary: ${e.message}", e)
            null
        }
    }

    override suspend fun createVocabulary(vocabulary: Vocabulary): Result<Unit> {
        return try {
            val document = vocabularyCollection.document()
            val vocabWithId = vocabulary.copy(id = document.id)
            document.set(vocabWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VocabularyRepo", "createVocabulary: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateVocabulary(vocabulary: Vocabulary): Result<Unit> {
        return try {
            vocabularyCollection.document(vocabulary.id).set(vocabulary).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VocabularyRepo", "updateVocabulary: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteVocabulary(vocabularyId: String): Result<Unit> {
        return try {
            vocabularyCollection.document(vocabularyId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VocabularyRepo", "deleteVocabulary: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteVocabulariesByFolder(folderId: String): Result<Unit> {
        return try {
            val snapshot = vocabularyCollection
                .whereEqualTo("folderId", folderId)
                .get()
                .await()
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("VocabularyRepo", "deleteVocabulariesByFolder: ${e.message}", e)
            Result.failure(e)
        }
    }
}
