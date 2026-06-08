package com.example.efishapp.feature.folder.data.repository

import com.example.efishapp.feature.folder.domain.FolderRepository
import com.example.efishapp.feature.folder.domain.model.Folder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FolderRepository {

    private val folderCollection = firestore.collection("folders")

    override suspend fun getFolders(userId: String): List<Folder> {
        return try {
            val snapshot = folderCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.toObjects(Folder::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun createFolder(folder: Folder): Result<Unit> {
        return try {
            val document = folderCollection.document()
            val folderWithId = folder.copy(id = document.id)
            document.set(folderWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFolder(folder: Folder): Result<Unit> {
        return try {
            folderCollection.document(folder.id).set(folder).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFolder(folderId: String): Result<Unit> {
        return try {
            folderCollection.document(folderId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
