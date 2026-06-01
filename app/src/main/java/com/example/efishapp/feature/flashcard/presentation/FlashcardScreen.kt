package com.example.efishapp.feature.flashcard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardActionButtons
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardBottomNavigation
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardContentCard
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardHeader
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel

data class Vocabulary(
    val word: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val description: String = "",
    val example: String = "",
    val collocation: String = "",
    val relatedWords: String = "",
    val note: String = "",

    val repetitions: Int = 0,
    val interval: Int = 0,
    val easinessFactor: Float = 2.5f
)

data class FlashcardScreenConfig(
    val backgroundColor: Color = Color(0xFFF7F9FA),
    val spaceHeight: Dp = 24.dp,
    val horizontalPadding: Dp = 16.dp
)

@Composable
fun FlashcardScreen(viewModel: FlashcardViewModel = hiltViewModel(),
                    onNavigateToCongratulation: (totalRemember: Int, totalForget: Int) -> Unit,
                    modifier: Modifier = Modifier,
                    config: FlashcardScreenConfig = FlashcardScreenConfig()) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            onNavigateToCongratulation(state.countRemember, state.countForget)

            viewModel.resetNavigationFlag()
        }
    }

    Scaffold(
        bottomBar = {
            FlashcardBottomNavigation(onEvent = {event -> viewModel.onEvent(event)}) }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(config.backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = config.horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(config.spaceHeight))

            FlashcardHeader(
                state
            )

            Spacer(modifier = Modifier.height(config.spaceHeight))

            FlashcardContentCard(
                state,
                onEvent = {event -> viewModel.onEvent(event)},
                modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(config.spaceHeight))
            FlashcardActionButtons(onEvent = {event -> viewModel.onEvent(event)})
            Spacer(modifier = Modifier.height(config.spaceHeight))
        }
    }
}





