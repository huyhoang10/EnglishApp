package com.example.efishapp.feature.flashcard.data.repository

import com.example.efishapp.feature.flashcard.data.FlashcardSessionDao
import com.example.efishapp.feature.flashcard.data.FlashcardSessionEntity
import com.example.efishapp.feature.flashcard.domain.model.FlashcardSession
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardLocalRepository
import javax.inject.Inject

class FlashcardLocalRepositoryImpl @Inject constructor(
    private val flashcardSessionDao: FlashcardSessionDao
) : FlashcardLocalRepository {

    override suspend fun saveCurrentSession(session: FlashcardSession) {
        // Chuyển đổi từ Model sạch sang Entity để Room lưu xuống
        val entity = session.toEntity()
        flashcardSessionDao.saveSession(entity)
    }

    override suspend fun getCurrentSession(userId: String, folderId: String): FlashcardSession? {
        // Lấy Entity từ Room lên và chuyển thành Model sạch cho tầng UI dùng
        val entity = flashcardSessionDao.getSession(userId, folderId)
        return entity?.toDomainModel()
    }

    override suspend fun clearSession(userId: String, folderId: String) {
        flashcardSessionDao.clearSession(userId, folderId)
    }
}

// --- BỘ CHUYỂN ĐỔI DỮ LIỆU (MAPPERS) EXTENSION FUNCTIONS ---
fun FlashcardSessionEntity.toDomainModel(): FlashcardSession {
    return FlashcardSession(
        userId = this.userId,
        folderId = this.folderId,
        currentIndex = this.currentIndex,
        countForget = this.countForget,
        countRemember = this.countRemember,
        vocabularies = this.vocabularies,
        userAnswers = this.userAnswers
    )
}

fun FlashcardSession.toEntity(): FlashcardSessionEntity {
    return FlashcardSessionEntity(
        userId = this.userId,
        folderId = this.folderId,
        currentIndex = this.currentIndex,
        countForget = this.countForget,
        countRemember = this.countRemember,
        vocabularies = this.vocabularies,
        userAnswers = this.userAnswers
    )
}