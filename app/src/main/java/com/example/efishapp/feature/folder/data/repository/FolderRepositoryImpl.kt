package com.example.efishapp.feature.folder.data.repository

import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import com.example.efishapp.feature.folder.domain.model.Folder
import com.example.efishapp.feature.folder.domain.model.Topic
import com.example.efishapp.feature.folder.domain.repository.FolderRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FolderRepositoryImpl (
    private val firestore: FirebaseFirestore
) : FolderRepository {

    private val foldersCollection = firestore.collection("folders")
    private val vocabulariesCollection = firestore.collection("vocabularies")

    override fun getFolders(): Flow<List<Folder>> = callbackFlow {
        val listener = foldersCollection
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val folders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toFolder()
                } ?: emptyList()
                trySend(folders)
            }
        awaitClose { listener.remove() }
    }

    override fun getFolderById(folderId: String): Flow<Folder?> = callbackFlow {
        val listener = foldersCollection.document(folderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val folder = snapshot?.toFolder()
                trySend(folder)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createFolder(folder: Folder): Result<String> {
        return try {
            val folderId = UUID.randomUUID().toString()
            val folderWithId = folder.copy(
                id = folderId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            foldersCollection.document(folderId).set(folderWithId).await()
            Result.success(folderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFolder(folder: Folder): Result<Unit> {
        return try {
            val updatedFolder = folder.copy(updatedAt = System.currentTimeMillis())
            foldersCollection.document(folder.id).set(updatedFolder).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFolder(folderId: String): Result<Unit> {
        return try {
            foldersCollection.document(folderId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateVocabularyCount(folderId: String) {
        try {
            val count = getVocabularyCount(folderId)
            foldersCollection.document(folderId).update("vocabularyCount", count)
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun getVocabularyCount(folderId: String): Int {
        return try {
            val snapshot = vocabulariesCollection
                .whereEqualTo("folderId", folderId)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun incrementVocabularyCount(folderId: String) {
        try {
            val folderDoc = foldersCollection.document(folderId)
            val snapshot = folderDoc.get().await()
            val currentCount = snapshot.getLong("vocabularyCount") ?: 0
            folderDoc.update("vocabularyCount", currentCount + 1)
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun decrementVocabularyCount(folderId: String) {
        try {
            val folderDoc = foldersCollection.document(folderId)
            val snapshot = folderDoc.get().await()
            val currentCount = snapshot.getLong("vocabularyCount") ?: 0
            if (currentCount > 0) {
                folderDoc.update("vocabularyCount", currentCount - 1)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun deleteAllVocabulariesInFolder(folderId: String) {
        try {
            val snapshot = vocabulariesCollection
                .whereEqualTo("folderId", folderId)
                .get()
                .await()
            
            val batch = firestore.batch()
            snapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toFolder(): Folder {
        return Folder(
            id = id,
            name = getString("name") ?: "",
            description = getString("description") ?: "",
            topicType = Topic.fromString(getString("topicType") ?: "CUSTOM"),
            vocabularyCount = getLong("vocabularyCount")?.toInt() ?: 0,
            colorHex = getLong("colorHex") ?: 0xFF4C58BA,
            createdAt = getLong("createdAt") ?: 0,
            updatedAt = getLong("updatedAt") ?: 0,
            ownerId = getString("ownerId") ?: ""
        )
    }
}
