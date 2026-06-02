package com.example.efishapp.feature.notification.Data.repository

import com.example.efishapp.feature.notification.Domain.model.DailyStudyNotification
import com.example.efishapp.feature.notification.Domain.repository.DailyStudyNotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DailyStudyNotificationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : DailyStudyNotificationRepository {

    private val collection = firestore.collection("notifications")

    override suspend fun get(uid: String): DailyStudyNotification? {
        return try {
            val document = collection.document(uid).get().await()
            if (document.exists()) {
                DailyStudyNotification(
                    title = document.getString("title") ?: "EnglishApp",
                    message = document.getString("message") ?: "Time to study English!",
                    hour = document.getLong("hour")?.toInt() ?: 20,
                    minute = document.getLong("minute")?.toInt() ?: 0,
                    isActive = document.getBoolean("isActive") ?: false
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun upsert(uid: String, data: DailyStudyNotification): Boolean {
        return try {
            val map = mapOf(
                "title" to data.title,
                "message" to data.message,
                "hour" to data.hour,
                "minute" to data.minute,
                "isActive" to data.isActive
            )
            collection.document(uid).set(map).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun delete(uid: String): Boolean {
        return try {
            collection.document(uid).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
