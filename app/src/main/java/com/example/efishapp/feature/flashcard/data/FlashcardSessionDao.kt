package com.example.efishapp.feature.flashcard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FlashcardSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: FlashcardSessionEntity)

    @Query("SELECT * FROM flashcard_sessions WHERE userId = :userId AND folderId = :folderId")
    suspend fun getSession(userId: String, folderId: String): FlashcardSessionEntity?

    @Query("DELETE FROM flashcard_sessions WHERE userId = :userId AND folderId = :folderId")
    suspend fun clearSession(userId: String, folderId: String): Int
}