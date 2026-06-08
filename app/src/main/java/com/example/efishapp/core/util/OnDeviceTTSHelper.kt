package com.example.efishapp.core.util
import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

open class OnDeviceTTSHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var _isReady = false
    val isReady: Boolean get() = _isReady

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locales = listOf(Locale.US, Locale.ENGLISH, Locale.getDefault())
            _isReady = locales.any { locale ->
                val result = tts?.setLanguage(locale)
                result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    fun speak(text: String) {
        if (_isReady && text.isNotBlank()) {
            tts?.apply {
                setSpeechRate(1.0f)
                speak(text, TextToSpeech.QUEUE_FLUSH, null, "VocabularyTTSId")
            }
        }
    }

    // Giải phóng bộ nhớ khi không sử dụng (Tránh Memory Leak)
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}