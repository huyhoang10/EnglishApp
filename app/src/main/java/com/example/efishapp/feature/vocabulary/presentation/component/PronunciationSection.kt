package com.example.efishapp.feature.vocabulary.presentation.component

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.efishapp.core.util.OnDeviceTTSHelper

@Composable
fun PronunciationSection(
    word: String,
    pronunciation: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isTtsReady by remember { mutableStateOf(false) }

    val ttsHelper = remember {
        object : OnDeviceTTSHelper(context) {
            override fun onInit(status: Int) {
                super.onInit(status)
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    isTtsReady = true
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (pronunciation.isNotBlank()) {
            Text(
                text = pronunciation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f, fill = false)
            )
        }

        IconButton(
            onClick = {
                if (!isTtsReady) {
                    Toast.makeText(context, "Đang khởi tạo giọng đọc...", Toast.LENGTH_SHORT).show()
                    return@IconButton
                }
                if (!ttsHelper.isReady) {
                    Toast.makeText(context, "Giọng đọc không khả dụng trên thiết bị này", Toast.LENGTH_SHORT).show()
                    return@IconButton
                }
                isPlaying = true
                ttsHelper.speak(word)
                isPlaying = false
            }
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Phát âm",
                tint = if (isTtsReady) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}
