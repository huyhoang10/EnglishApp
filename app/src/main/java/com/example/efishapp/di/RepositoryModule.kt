package com.example.efishapp.di

import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository
import com.example.efishapp.feature.dashboard.data.repository.MonthlyTrackerRepositoryImpl
import com.example.efishapp.feature.dashboard.data.repository.WeeklyTrackerRepositoryImpl
import com.example.efishapp.feature.dashboard.domain.MonthTrackerReposity
import com.example.efishapp.feature.dashboard.domain.WeeklyTrackerRepository

import com.example.efishapp.feature.notification.Data.repository.DailyStudyNotificationRepositoryImpl
import com.example.efishapp.feature.notification.Domain.repository.DailyStudyNotificationRepository
import com.example.efishapp.feature.profile.data.repository.UserProfileRepositoryImpl
import com.example.efishapp.feature.profile.domain.repository.UserProfileRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

//    @Binds
//    @Singleton
//    abstract fun bindFolderRepository(impl: FolderRepositoryImpl): FolderRepository
//
//    @Binds
//    @Singleton
//    abstract fun bindVocabularyRepository(impl: VocabularyRepositoryImpl): VocabularyRepository

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
    abstract fun bindMonthlyTrackerRepository(impl: MonthlyTrackerRepositoryImpl): MonthTrackerReposity
}
