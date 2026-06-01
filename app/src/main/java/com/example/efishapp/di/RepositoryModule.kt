package com.example.efishapp.di

import com.example.efishapp.feature.Auth.Data.Repository.AuthRepositoryImpl
import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository
import com.example.efishapp.feature.folder.data.repository.FolderRepositoryImpl
import com.example.efishapp.feature.folder.domain.repository.FolderRepository
import com.example.efishapp.feature.vocabulary.data.repository.VocabularyRepositoryImpl
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFolderRepository(firestore: FirebaseFirestore): FolderRepository {
        return FolderRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideVocabularyRepository(firestore: FirebaseFirestore): VocabularyRepository {
        return VocabularyRepositoryImpl(firestore)
    }
}
