package com.example.efishapp.di

import com.example.efishapp.feature.Auth.Domain.Repository.AuthRepository
import com.example.efishapp.feature.Auth.Domain.UseCase.ForgotPasswordUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.LoginWithGoogleUseCase
import com.example.efishapp.feature.Auth.Domain.UseCase.RegisterUseCase
import com.example.efishapp.feature.folder.domain.repository.FolderRepository
import com.example.efishapp.feature.folder.domain.usecase.CreateFolder
import com.example.efishapp.feature.folder.domain.usecase.DeleteFolder
import com.example.efishapp.feature.folder.domain.usecase.GetFolder
import com.example.efishapp.feature.folder.domain.usecase.UpdateFolder
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.usecase.CreateVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.DeleteVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.GetVocabulariesUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.UpdateVocabularyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    // Auth UseCases
    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideForgotPasswordUseCase(authRepository: AuthRepository): ForgotPasswordUseCase {
        return ForgotPasswordUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideLoginWithGoogleUseCase(authRepository: AuthRepository): LoginWithGoogleUseCase {
        return LoginWithGoogleUseCase(authRepository)
    }

    // Folder UseCases
    @Provides
    @ViewModelScoped
    fun provideGetFolder(folderRepository: FolderRepository): GetFolder {
        return GetFolder(folderRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateFolder(folderRepository: FolderRepository): CreateFolder {
        return CreateFolder(folderRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateFolder(folderRepository: FolderRepository): UpdateFolder {
        return UpdateFolder(folderRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteFolder(
        folderRepository: FolderRepository,
        vocabularyRepository: VocabularyRepository
    ): DeleteFolder {
        return DeleteFolder(folderRepository, vocabularyRepository)
    }

    // Vocabulary UseCases
    @Provides
    @ViewModelScoped
    fun provideGetVocabulariesUseCase(vocabularyRepository: VocabularyRepository): GetVocabulariesUseCase {
        return GetVocabulariesUseCase(vocabularyRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateVocabularyUseCase(vocabularyRepository: VocabularyRepository): CreateVocabularyUseCase {
        return CreateVocabularyUseCase(vocabularyRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateVocabularyUseCase(vocabularyRepository: VocabularyRepository): UpdateVocabularyUseCase {
        return UpdateVocabularyUseCase(vocabularyRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteVocabularyUseCase(vocabularyRepository: VocabularyRepository): DeleteVocabularyUseCase {
        return DeleteVocabularyUseCase(vocabularyRepository)
    }
}
