package com.example.efishapp.feature.vocabulary.presentation

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.util.Locale

@Composable
fun VocabularyDetailScreen(
    folderColor: Long,
    onNavigateBack: () -> Unit,
    viewModel: VocabularyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val folderColorValue = Color(folderColor)
    val context = LocalContext.current

    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isSpeakingExample by remember { mutableStateOf(false) }
    var isTtsReady by remember { mutableStateOf(false) }
    var currentSpeakingWord by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                isTtsReady = true
            }
        }

        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    fun speakWord(word: String, isExample: Boolean = false) {
        if (isTtsReady) {
            if (isExample) {
                isSpeakingExample = true
                currentSpeakingWord = word
            } else {
                isSpeaking = true
                currentSpeakingWord = word
            }
            textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
        }
    }

    LaunchedEffect(isSpeaking, isSpeakingExample) {
        if (isSpeaking || isSpeakingExample) {
            kotlinx.coroutines.delay(1500)
            isSpeaking = false
            isSpeakingExample = false
            currentSpeakingWord = ""
        }
    }

    VocabularyDetailContent(
        uiState = uiState,
        folderColor = folderColorValue,
        isSpeaking = isSpeaking,
        isSpeakingExample = isSpeakingExample,
        onSpeakWord = { speakWord(it, false) },
        onSpeakExample = { speakWord(it, true) },
        onNavigateBack = onNavigateBack,
        onToggleLearned = viewModel::toggleLearned
    )
}
