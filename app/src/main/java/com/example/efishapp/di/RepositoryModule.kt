package com.example.efishapp.di

import com.example.efishapp.database.AppDatabase
import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository
import com.example.efishapp.feature.dashboard.data.repository.MonthlyTrackerRepositoryImpl
import com.example.efishapp.feature.dashboard.data.repository.UserAnalyticsRepositoryImpl
import com.example.efishapp.feature.dashboard.data.repository.WeeklyTrackerRepositoryImpl
import com.example.efishapp.feature.dashboard.domain.MonthTrackerRepository
import com.example.efishapp.feature.dashboard.domain.UserAnalyticsRepository
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository
import com.example.efishapp.feature.flashcard.data.FlashcardSessionDao
import com.example.efishapp.feature.flashcard.data.repository.FlashcardLocalRepositoryImpl
import com.example.efishapp.feature.flashcard.data.repository.FlashcardRepositoryImpl
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardLocalRepository
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardRepository
import com.example.efishapp.feature.notification.data.repository.DailyStudyNotificationRepositoryImpl
import com.example.efishapp.feature.notification.domain.repository.DailyStudyNotificationRepository
import com.example.efishapp.feature.profile.data.repository.UserProfileRepositoryImpl
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository

import com.example.efishapp.feature.folder.data.repository.FolderRepositoryImpl
import com.example.efishapp.feature.folder.domain.FolderRepository
import com.example.efishapp.feature.setting.data.repository.SettingRepositoryImpl
import com.example.efishapp.feature.setting.domain.repository.SettingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFolderRepository(impl: FolderRepositoryImpl): FolderRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindDailyStudyNotificationRepository(impl: DailyStudyNotificationRepositoryImpl): DailyStudyNotificationRepository

    @Binds
    @Singleton
    abstract fun bindWeeklyTrackerRepository(impl: WeeklyTrackerRepositoryImpl): WeeklyTrackerRepository

    @Binds
    @Singleton
    abstract fun bindMonthlyTrackerRepository(impl: MonthlyTrackerRepositoryImpl): MonthTrackerRepository

    @Binds
    @Singleton
    abstract fun bindUserAnalyticsRepository(impl: UserAnalyticsRepositoryImpl): UserAnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindFlashcardRepository(impl: FlashcardRepositoryImpl): FlashcardRepository

    @Binds
    @Singleton
    abstract fun bindFlashcardLocalRepository(impl: FlashcardLocalRepositoryImpl): FlashcardLocalRepository

    @Binds
    @Singleton
    abstract fun bindSettingRepository(impl: SettingRepositoryImpl): SettingRepository
}
