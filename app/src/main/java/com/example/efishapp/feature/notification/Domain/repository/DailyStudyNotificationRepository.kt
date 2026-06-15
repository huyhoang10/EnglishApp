package com.example.efishapp.feature.notification.domain.repository

import com.example.efishapp.feature.notification.domain.model.DailyStudyNotification

interface DailyStudyNotificationRepository {
    suspend fun get(uid: String): DailyStudyNotification?
    suspend fun upsert(uid: String, data: DailyStudyNotification): Boolean
    suspend fun delete(uid: String): Boolean
}