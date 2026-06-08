package com.example.efishapp.feature.flashcard.data

import androidx.room.TypeConverter
import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FlashcardConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromVocabularyList(value: List<Vocabulary>?): String? = gson.toJson(value)

    @TypeConverter
    fun toVocabularyList(value: String?): List<Vocabulary>? {
        val listType = object : TypeToken<List<Vocabulary>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromUserAnswersMap(value: Map<String, ActionType>?): String? = gson.toJson(value)

    @TypeConverter
    fun toUserAnswersMap(value: String?): Map<String, ActionType>? {
        val mapType = object : TypeToken<Map<String, ActionType>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
}