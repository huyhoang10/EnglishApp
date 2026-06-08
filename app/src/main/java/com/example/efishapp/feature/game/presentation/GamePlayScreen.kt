package com.example.efishapp.feature.game.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.core.util.OnDeviceTTSHelper
import com.example.efishapp.feature.game.domain.model.GameType
import androidx.compose.runtime.getValue

private val PrimaryBlue = Color(0xFF1E3A8A)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)
private val WarningAmber = Color(0xFFF59E0B)
private val LightGray = Color(0xFFE2E8F0)
private val LightBlue = Color(0xFFEFF6FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePlayScreen(
    uiState: GameUiState,
    gameType: GameType,
    onEvent: (GameEvent) -> Unit,
    ttsHelper: OnDeviceTTSHelper,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (correct: Int, wrong: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = uiState.currentQuestion
    val totalQuestions = uiState.gameSession?.questions?.size ?: 1
    val progress by androidx.compose.animation.core.animateFloatAsState(
        targetValue = (uiState.currentQuestionIndex + 1).toFloat() / totalQuestions,
        label = "progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gameType.displayName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Câu ${uiState.currentQuestionIndex + 1} / $totalQuestions", fontSize = 14.sp, color = Color.Gray)
                Text(uiState.selectedLevel.displayName, fontSize = 14.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = LightGray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (question == null) {
                Text("Không có câu hỏi", color = Color.Gray)
                return@Column
            }

            AnimatedVisibility(
                visible = uiState.lastAnswerCorrect == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (gameType) {
                        GameType.SPELLING -> SpellingGameContent(
                            question = question,
                            currentInput = uiState.currentInputLetters,
                            inputSlotToShuffledIndex = uiState.inputSlotToShuffledIndex,
                            usedIndices = uiState.usedLetterIndices,
                            onLetterSelected = { letter, idx -> onEvent(GameEvent.LetterSelected(letter, idx)) },
                            onLetterDeselected = { idx ->
                                uiState.inputSlotToShuffledIndex.getOrNull(idx)?.let { shuffledIdx ->
                                    uiState.currentInputLetters.getOrNull(idx)?.let { l ->
                                        onEvent(GameEvent.LetterDeselected(l, shuffledIdx))
                                    }
                                }
                            },
                            onPlayTTS = { ttsHelper.speak(question.vocabulary.word) }
                        )
                        GameType.FILL_BLANK -> FillBlankGameContent(
                            question = question,
                            userAnswer = uiState.userAnswer,
                            onAnswerChanged = { onEvent(GameEvent.UpdateAnswer(it)) },
                            onSubmit = {
                                onEvent(GameEvent.SubmitAnswer(uiState.userAnswer))
                            },
                            onPlayTTS = { ttsHelper.speak(question.vocabulary.word) }
                        )
                        GameType.HANGMAN -> HangmanGameContent(
                            question = question,
                            usedLetters = uiState.hangmanUsedLetters,
                            wrongCount = uiState.hangmanWrongCount,
                            onLetterGuessed = { onEvent(GameEvent.LetterSelected(it, -1)) },
                            onPlayTTS = { ttsHelper.speak(question.vocabulary.word) }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.lastAnswerCorrect != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                AnswerFeedbackCard(
                    isCorrect = uiState.lastAnswerCorrect ?: false,
                    correctWord = question.originalWord,
                    meaning = question.vocabulary.meaning,
                    onNext = {
                        if (uiState.currentQuestionIndex + 1 >= totalQuestions) {
                            onNavigateToResult(uiState.correctAnswers, uiState.wrongAnswers)
                        } else {
                            onEvent(GameEvent.NextQuestion)
                        }
                    },
                    onRetry = { onEvent(GameEvent.RetryQuestion) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Đúng", fontSize = 12.sp, color = SuccessGreen)
                    Text(uiState.correctAnswers.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sai", fontSize = 12.sp, color = ErrorRed)
                    Text(uiState.wrongAnswers.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                }
            }
        }
    }
}

@Composable
private fun CenteredSoundButton(onPlayTTS: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onPlayTTS,
            modifier = Modifier
                .size(72.dp)
                .background(PrimaryBlue, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Phát âm",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun SpellingGameContent(
    question: com.example.efishapp.feature.game.domain.model.GameQuestion,
    currentInput: List<Char?>,
    inputSlotToShuffledIndex: List<Int?>,
    usedIndices: Set<Int>,
    onLetterSelected: (Char, Int) -> Unit,
    onLetterDeselected: (Int) -> Unit,
    onPlayTTS: () -> Unit
) {
    val shuffledLetters = question.shuffledLetters

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nghe và sắp xếp chữ cái", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))
        CenteredSoundButton(onPlayTTS)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Dat cac chu cai vao o trong:", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            question.originalWord.forEachIndexed { index, _ ->
                val letter = currentInput.getOrNull(index)
                LetterSlot(
                    letter = letter,
                    onClick = { onLetterDeselected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text("Chọn chữ cái:", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(140.dp)
        ) {
            shuffledLetters.forEachIndexed { index, letter ->
                item {
                    val isUsed = index in usedIndices
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isUsed) LightGray else PrimaryBlue)
                            .clickable(enabled = !isUsed) { onLetterSelected(letter, index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUsed) Color.Gray else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FillBlankGameContent(
    question: com.example.efishapp.feature.game.domain.model.GameQuestion,
    userAnswer: String,
    onAnswerChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onPlayTTS: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hoàn thành câu", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))
        CenteredSoundButton(onPlayTTS)
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
            Text(
                text = question.vocabulary.example.replace(question.vocabulary.word, "______", ignoreCase = true),
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryBlue,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = userAnswer,
            onValueChange = onAnswerChanged,
            label = { Text("Nhập từ") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                cursorColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("KIỂM TRA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HangmanGameContent(
    question: com.example.efishapp.feature.game.domain.model.GameQuestion,
    usedLetters: Set<Char>,
    wrongCount: Int,
    onLetterGuessed: (Char) -> Unit,
    onPlayTTS: () -> Unit
) {
    val allAlphabet = ('A'..'Z').toList()
    val wordLetters = question.originalWord.toSet()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(question.vocabulary.meaning, fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(12.dp))
        CenteredSoundButton(onPlayTTS)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Sai: $wrongCount / 6", fontSize = 14.sp, color = if (wrongCount >= 4) ErrorRed else Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            question.originalWord.forEach { char ->
                val isRevealed = char in usedLetters
                LetterSlot(
                    letter = if (isRevealed) char else null,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Chọn chữ cái:", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(200.dp)
        ) {
            items(allAlphabet) { letter ->
                val isUsed = letter in usedLetters
                val isCorrectLetter = letter in wordLetters
                val bgColor = when {
                    !isUsed -> PrimaryBlue
                    isCorrectLetter -> SuccessGreen
                    else -> ErrorRed
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable(enabled = !isUsed, onClick = { onLetterGuessed(letter) }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = letter.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LetterSlot(letter: Char?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .widthIn(min = 36.dp)
            .height(56.dp)
            .border(2.dp, if (letter != null) PrimaryBlue else LightGray, RoundedCornerShape(8.dp))
            .background(if (letter != null) LightBlue else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (letter != null) {
            Text(text = letter.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
    }
}

@Composable
private fun AnswerFeedbackCard(
    isCorrect: Boolean,
    correctWord: String,
    meaning: String,
    onNext: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = if (isCorrect) SuccessGreen else ErrorRed
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isCorrect) "Chính xác!" else "Sai rồi!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) SuccessGreen else ErrorRed
            )

            if (!isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Đáp án: $correctWord", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                Spacer(modifier = Modifier.height(4.dp))
                Text(meaning, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!isCorrect) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("THỬ LẠI", fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("TIẾP TỤC", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
