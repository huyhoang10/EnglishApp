package com.example.efishapp.feature.game.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.efishapp.core.util.OnDeviceTTSHelper
import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameType

private val PrimaryBlue = Color(0xFF1E3A8A)
private val SuccessGreen = Color(0xFF10B981)

@Composable
fun GameScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Int, Int, GameType, GameLevel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val onDeviceTTSHelper = remember {
        object : OnDeviceTTSHelper(context) {
            override fun onInit(status: Int) {
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { onDeviceTTSHelper.shutdown() }
    }

    AnimatedContent(
        targetState = when {
            uiState.isGameStarted && !uiState.isGameCompleted -> GameScreenState.PLAYING
            uiState.isGameCompleted -> GameScreenState.RESULT
            else -> GameScreenState.SELECTION
        },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "game_screen_transition",
        modifier = modifier
    ) { state ->
        when (state) {
            GameScreenState.SELECTION -> {
                GameSelectionScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier.fillMaxSize()
                )
            }
            GameScreenState.PLAYING -> {
                val gameType = uiState.selectedGameType ?: return@AnimatedContent
                GamePlayScreen(
                    uiState = uiState,
                    gameType = gameType,
                    onEvent = viewModel::onEvent,
                    ttsHelper = onDeviceTTSHelper,
                    onNavigateBack = {
                        viewModel.onEvent(GameEvent.ResetGame)
                    },
                    onNavigateToResult = { correct, wrong ->
                        onNavigateToResult(correct, wrong, gameType, uiState.selectedLevel)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            GameScreenState.RESULT -> {
                val gameType = uiState.selectedGameType ?: return@AnimatedContent
                GameResultScreen(
                    correctAnswers = uiState.correctAnswers,
                    wrongAnswers = uiState.wrongAnswers,
                    gameType = gameType,
                    gameLevel = uiState.selectedLevel,
                    onPlayAgain = { viewModel.onEvent(GameEvent.StartGame) },
                    onGoBack = {
                        viewModel.onEvent(GameEvent.ResetGame)
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private enum class GameScreenState { SELECTION, PLAYING, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameSelectionScreen(
    uiState: GameUiState,
    onEvent: (GameEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trò chơi", fontWeight = FontWeight.Bold) },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Chọn trò chơi", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp), color = PrimaryBlue)
            } else {
                GameTypeCard(
                    gameType = GameType.SPELLING,
                    title = "Spelling Game",
                    description = "Nghe và sắp xếp trật tự từ",
                    icon = Icons.Default.Spellcheck,
                    isSelected = uiState.selectedGameType == GameType.SPELLING,
                    onClick = { onEvent(GameEvent.SelectGameType(GameType.SPELLING)) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                GameTypeCard(
                    gameType = GameType.FILL_BLANK,
                    title = "Fill in the Blank",
                    description = "Hoàn thành ô trống",
                    icon = Icons.Default.Keyboard,
                    isSelected = uiState.selectedGameType == GameType.FILL_BLANK,
                    onClick = { onEvent(GameEvent.SelectGameType(GameType.FILL_BLANK)) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                GameTypeCard(
                    gameType = GameType.HANGMAN,
                    title = "Hangman",
                    description = "Đoán từ gợi ý",
                    icon = Icons.Default.Checkroom,
                    isSelected = uiState.selectedGameType == GameType.HANGMAN,
                    onClick = { onEvent(GameEvent.SelectGameType(GameType.HANGMAN)) }
                )

                if (uiState.selectedGameType != null) {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text("Chọn cấp độ", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GameLevel.entries.forEach { level ->
                            LevelButton(
                                level = level,
                                isSelected = uiState.selectedLevel == level,
                                onClick = { onEvent(GameEvent.SelectLevel(level)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    GameStartButton(enabled = true, onClick = { onEvent(GameEvent.StartGame) })
                }
            }
        }
    }
}

@Composable
private fun GameTypeCard(
    gameType: GameType,
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryBlue else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = if (isSelected) Color.White else PrimaryBlue
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Black)
                Text(description, fontSize = 14.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray)
            }
        }
    }
}

@Composable
private fun LevelButton(level: GameLevel, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(level.displayName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else PrimaryBlue)
            Text(level.description, fontSize = 10.sp, textAlign = TextAlign.Center, color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray)
        }
    }
}

@Composable
private fun GameStartButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (enabled) SuccessGreen else Color.Gray)
    ) {
        Text(
            "BẮT ĐẦU CHƠI",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
