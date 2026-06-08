package com.example.efishapp.di

import com.example.efishapp.feature.vocabulary.data.repository.VocabularyRepositoryImpl
import com.example.efishapp.feature.vocabulary.domain.VocabularyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VocabularyBindingModule {

    @Binds
    @Singleton
    abstract fun bindVocabularyRepository(impl: VocabularyRepositoryImpl): VocabularyRepository
}
