package com.example.efishapp.feature.vocabulary.data.repository

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class VocabularyRepositoryImpl(
    private val firestore: FirebaseFirestore
) : VocabularyRepository {

    private val vocabulariesCollection = firestore.collection("vocabularies")

    override fun getVocabulariesByFolder(
        folderId: String
    ): Flow<List<Vocabulary>> = callbackFlow {

        val listener = vocabulariesCollection
            .whereEqualTo("folderId", folderId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val vocabularies = snapshot?.documents
                    ?.mapNotNull { it.toVocabulary() }
                    ?: emptyList()

                trySend(vocabularies)
            }

        awaitClose { listener.remove() }
    }

    override fun getVocabularyById(
        vocabularyId: String
    ): Flow<Vocabulary?> = callbackFlow {

        val listener = vocabulariesCollection
            .document(vocabularyId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                trySend(snapshot?.toVocabulary())
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createVocabulary(
        vocabulary: Vocabulary
    ): Result<String> {

        return try {
            val vocabularyId = UUID.randomUUID().toString()

            val vocabularyWithId = vocabulary.copy(
                id = vocabularyId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            vocabulariesCollection
                .document(vocabularyId)
                .set(vocabularyWithId)
                .await()

            Result.success(vocabularyId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateVocabulary(
        vocabulary: Vocabulary
    ): Result<Unit> {

        return try {
            val updatedVocabulary = vocabulary.copy(
                updatedAt = System.currentTimeMillis()
            )

            vocabulariesCollection
                .document(vocabulary.id)
                .set(updatedVocabulary)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteVocabulary(
        vocabularyId: String
    ): Result<Unit> {

        return try {
            vocabulariesCollection
                .document(vocabularyId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllVocabulariesInFolder(
        folderId: String
    ): Result<Unit> {

        return try {
            val snapshot = vocabulariesCollection
                .whereEqualTo("folderId", folderId)
                .get()
                .await()

            val batch = firestore.batch()

            snapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }

            batch.commit().await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot
            .toVocabulary(): Vocabulary? {

        return toObject(Vocabulary::class.java)
            ?.copy(id = id)
    }
}
