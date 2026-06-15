package com.example.efishapp.feature.vocabulary.data.remote

import com.example.efishapp.feature.vocabulary.data.remote.model.DictionaryApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {

    @GET("api/v2/entries/en/{word}")
    suspend fun getWordInfo(@Path("word") word: String): List<DictionaryApiResponse>
}
