package com.example.efishapp.di

import android.content.Context
import androidx.room.Room
import com.example.efishapp.database.AppDatabase
import com.example.efishapp.feature.flashcard.data.FlashcardSessionDao
import com.example.efishapp.feature.flashcard.data.repository.FlashcardRepositoryImpl
import com.example.efishapp.feature.flashcard.domain.repository.FlashcardRepository
import com.example.efishapp.feature.setting.data.datastore.SettingPreferences
import com.example.efishapp.feature.setting.data.repository.SettingRepositoryImpl
import com.example.efishapp.feature.setting.domain.repository.SettingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class FlashcardBindingModule {
//    @Binds
//    @Singleton
//    abstract fun bindFlashcardRepository(impl: FlashcardRepositoryImpl): FlashcardRepository
//
//    @Binds
//    @Singleton
//    abstract fun bindSettingRepository(impl: SettingRepositoryImpl): SettingRepository
//}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideSettingPreferences(@ApplicationContext context: Context): SettingPreferences {
        return SettingPreferences(context)
    }

}
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase { // Đảm bảo import chính xác class AppDatabase chuẩn
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "efish_database"
        )
            .fallbackToDestructiveMigration() // Giúp tự clear DB cũ khi nâng lên version 2
            .build()
    }

    @Provides
    @Singleton
    fun provideFlashcardSessionDao(database: AppDatabase): FlashcardSessionDao {
        return database.flashcardSessionDao() // Hoặc tên hàm lấy Dao trong file AppDatabase của bạn
    }
}