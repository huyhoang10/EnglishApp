package com.example.efishapp.feature.notification.Domain.repository

import com.example.efishapp.feature.notification.Domain.model.DailyStudyNotification

interface DailyStudyNotificationRepository {
    suspend fun get(uid: String): DailyStudyNotification?
    suspend fun upsert(uid: String, data: DailyStudyNotification): Boolean
    suspend fun delete(uid: String): Boolean
}