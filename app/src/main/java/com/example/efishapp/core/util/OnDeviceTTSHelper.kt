package com.example.efishapp.core.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

open class OnDeviceTTSHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var _isReady = false
    val isReady: Boolean get() = _isReady

    private var pendingText: String? = null
    private var pendingUtteranceId: String? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("TTS", "Started: $utteranceId")
                    }

                    override fun onDone(utteranceId: String?) {
                        Log.d("TTS", "Done: $utteranceId")
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        Log.e("TTS", "Error on utterance: $utteranceId")
                    }
                })

                val localesToTry = listOf(
                    Locale.US,
                    Locale.ENGLISH,
                    Locale.UK,
                    Locale("en", "GB")
                )

                var foundLocale = false
                for (locale in localesToTry) {
                    val result = engine.setLanguage(locale)
                    if (result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        _isReady = true
                        foundLocale = true
                        Log.d("TTS", "Language ready: ${locale.displayName}")
                        break
                    }
                }

                if (!foundLocale) {
                    val result = engine.setLanguage(Locale.getDefault())
                    if (result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        _isReady = true
                        Log.d("TTS", "Language ready (default): ${Locale.getDefault().displayName}")
                    } else {
                        Log.e("TTS", "No supported language found for TTS")
                    }
                }

                if (_isReady && pendingText != null) {
                    Log.d("TTS", "Speaking pending text: $pendingText")
                    val text = pendingText!!
                    val id = pendingUtteranceId ?: "PendingTTSId"
                    engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
                    pendingText = null
                    pendingUtteranceId = null
                }
            }
        } else {
            Log.e("TTS", "TTS init failed with status: $status")
        }
    }

    fun speak(text: String, utteranceId: String = "GameTTSId") {
        if (text.isBlank()) return

        if (_isReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            Log.d("TTS", "Speaking: $text")
        } else {
            Log.w("TTS", "TTS not ready yet (isReady=$_isReady), queuing: $text")
            pendingText = text
            pendingUtteranceId = utteranceId
            retrySetLanguage()
        }
    }

    fun speakBasic(text: String) {

    }
    private fun retrySetLanguage() {
        tts?.let { engine ->
            val locales = listOf(Locale.US, Locale.ENGLISH, Locale.UK)
            for (locale in locales) {
                val result = engine.setLanguage(locale)
                if (result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    _isReady = true
                    Log.d("TTS", "Language set on retry: ${locale.displayName}")
                    if (pendingText != null) {
                        val text = pendingText!!
                        val id = pendingUtteranceId ?: "GameTTSId"
                        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
                        Log.d("TTS", "Speaking pending after retry: $text")
                        pendingText = null
                        pendingUtteranceId = null
                    }
                    return
                }
            }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isReady = false
    }
}
