package com.example.efishapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.efishapp.feature.flashcard.data.FlashcardConverters
import com.example.efishapp.feature.flashcard.data.FlashcardSessionDao
import com.example.efishapp.feature.flashcard.data.FlashcardSessionEntity

@Database(
    entities = [FlashcardSessionEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(FlashcardConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardSessionDao(): FlashcardSessionDao
}