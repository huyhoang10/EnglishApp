package com.example.efishapp.feature.flashcard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.efishapp.core.designsystem.LoadingDialog
import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardActionButtons
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardBottomNavigation
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardContentCard
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardHeader
import com.example.efishapp.feature.flashcard.presentation.FlashcardViewModel

data class FlashcardScreenConfig(
    val backgroundColor: Color = Color(0xFFF7F9FA),
    val spaceHeight: Dp = 24.dp,
    val horizontalPadding: Dp = 16.dp
)

@Composable
fun FlashcardScreen(viewModel: FlashcardViewModel = hiltViewModel(),
                    isTtsReady: Boolean = true,
                    onClickSpeech: (String) -> Unit,
                    onNavigateToCongratulation: (totalRemember: Int, totalForget: Int) -> Unit,
                    onNavigateNotifyEmpty: () -> Unit) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isFinished, state.isEmpty) {
        if (state.isFinished) {
            viewModel.updateUserReview()
            viewModel.resetNavigationFlag()
            onNavigateToCongratulation(state.countRemember, state.countForget)
        }else if (state.isEmpty) {
            onNavigateNotifyEmpty()
            viewModel.resetNavigationFlag()
        }
    }

    Scaffold(
        bottomBar = {
            FlashcardBottomNavigation(onClickBack = { viewModel.onEvent(FlashcardUiEvent.OnClickBack) },
                onClickDetail = { viewModel.onEvent(FlashcardUiEvent.OnClickDetail) }) }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingDialog()
            }

            state.vocabularies.isEmpty() || state.isEmpty -> {
                onNavigateNotifyEmpty()
            }
            else -> {
                FlashcardContent(
                    indexWord = state.indexWord,
                    numVocabulary = state.vocabularies.size,
                    countForget = state.countForget,
                    countRemember = state.countRemember,
                    vocabulary = state.vocabularies[state.indexWord],
                    isFlipped = state.isFlipped,
                    isShowDetail = state.isShowDetail,
                    onClickFlipCard = { viewModel.onEvent(FlashcardUiEvent.OnFlipCard) },
                    isTtsReady = isTtsReady,
                    onClickSpeech = onClickSpeech,
                    onClickAgain = { viewModel.onEvent(FlashcardUiEvent.OnAnswer(ActionType.AGAIN)) },
                    onClickHard = { viewModel.onEvent(FlashcardUiEvent.OnAnswer(ActionType.HARD)) },
                    onClickGood = { viewModel.onEvent(FlashcardUiEvent.OnAnswer(ActionType.GOOD)) },
                    onClickEasy = { viewModel.onEvent(FlashcardUiEvent.OnAnswer(ActionType.EASY)) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
//            else {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(innerPadding),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            }
        }


    }
}

@Composable
fun FlashcardContent(
    indexWord: Int,
    numVocabulary: Int,
    countForget: Int,
    countRemember: Int,
    vocabulary: Vocabulary,
    isFlipped: Boolean,
    isShowDetail: Boolean,
    onClickFlipCard: () -> Unit,
    isTtsReady: Boolean,
    onClickSpeech: (String) -> Unit,
    onClickAgain: () -> Unit,
    onClickHard: () -> Unit,
    onClickGood: () -> Unit,
    onClickEasy: () -> Unit,
    modifier: Modifier = Modifier,
    config: FlashcardScreenConfig = FlashcardScreenConfig()
) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(config.backgroundColor)
                .padding()
                .padding(horizontal = config.horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(config.spaceHeight))

            FlashcardHeader(
                indexWord,
                numVocabulary,
                countForget,
                countRemember,
            )

            Spacer(modifier = Modifier.height(config.spaceHeight))

            FlashcardContentCard(
                vocabulary,
                isFlipped,
                isShowDetail,
                onClickFlipCard,
                isTtsReady,
                onClickSpeech,
                modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(config.spaceHeight))
            FlashcardActionButtons(
                onClickAgain,
                onClickHard,
                onClickGood,
                onClickEasy,
            )
            Spacer(modifier = Modifier.height(config.spaceHeight))
        }
}



@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun FlashcardContentPreview() {
    FlashcardContent(
        indexWord = 1,
        numVocabulary = 20,
        countForget = 3,
        countRemember = 5,
        vocabulary = Vocabulary(
            id = "1",
            word = "apple",
            pronunciation = "/ˈæp.əl/",
            meaning = "quả táo",
            description = "A round fruit with red, green, or yellow skin.",
            example = "I eat an apple every morning.",
            collocation = "apple pie",
            relatedWords = "fruit, orange, banana",
            note = "Common vocabulary for beginners."
        ),
        isFlipped = false,
        isShowDetail = true,
        isTtsReady = true,
        onClickFlipCard = {},
        onClickSpeech = {word ->{}},
        onClickAgain = {},
        onClickHard = {},
        onClickGood = {},
        onClickEasy = {}
    )
}