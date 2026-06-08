package com.example.efishapp.core.util
import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class OnDeviceTTSHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        // Khởi tạo TTS Engine của hệ thống
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Thiết lập ngôn ngữ mặc định là Tiếng Anh (Mỹ)
            val result = tts?.setLanguage(Locale.US)

            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isReady = true
            }
        }
    }

    fun speak(text: String) {
        val speed: Float = 1.0f
        if (isReady && text.isNotBlank()) {
            tts?.apply {
                setSpeechRate(speed) // Cấu hình tốc độ đọc (Ví dụ: 0.8 là chậm, 1.0 là bình thường)
                // Phát âm thanh (QUEUE_FLUSH: Dừng âm thanh cũ nếu đang phát để phát từ mới ngay)
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