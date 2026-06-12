package com.example.efishapp.feature.flashcard.presentation.component

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.core.util.OnDeviceTTSHelper
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary

data class FlashcardContentCardConfig(
    val cornerRadius: Dp = 24.dp,
    val elevation: Dp = 2.dp,
    val borderWidth: Dp = 1.dp,
    val borderColor: Color = Color(0xFFE0E0E0),
    val backgroundColor: Color = Color.White,
    val spacerHeight: Dp = 12.dp,
    val animationDurationMillis: Int = 300,
    val cameraDistanceDensity: Float = 12f,
    val iconSize: Dp = 35.dp,
    val largeTextStyle: TextStyle = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    ),
    val mediumTextStyle: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Black
    )
)


@Composable
fun FlashcardContentCard(
    vocabulary: Vocabulary = Vocabulary(),
    isFlipped: Boolean = false,
    isShowDetail: Boolean = false,
    onFlipCard:() -> Unit = {},
    isTtsReady: Boolean = true,
    onClickSpeech: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    config: FlashcardContentCardConfig = FlashcardContentCardConfig()
) {



    val cardRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = config.animationDurationMillis),
        label = "CardRotationAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onFlipCard() }
            .graphicsLayer {
                this.rotationY = cardRotation
                cameraDistance = config.cameraDistanceDensity * density
            },
        shape = RoundedCornerShape(config.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = config.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = config.elevation),
        border = BorderStroke(config.borderWidth, config.borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (cardRotation > 90f) {
                BackContent(vocabulary,isShowDetail)
            } else {
                FrontContent(vocabulary.word, isTtsReady)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardContentCardPreview() {
    FlashcardContentCard(
        vocabulary = Vocabulary(
            word = "Apple",
            meaning = "Táo"
        ),
        isFlipped = false,
        isShowDetail = false,
        onFlipCard = {},
        isTtsReady = true,
        onClickSpeech = {word->{}}
    )
}

@Composable
private fun FrontContent(word: String = "",
                         isTtsReady: Boolean = true,
                         config: FlashcardContentCardConfig = FlashcardContentCardConfig()
) {
    val context = LocalContext.current
    val ttsHelper = remember { OnDeviceTTSHelper(context) }

    // 2. Quản lý vòng đời: Tự động giải phóng khi rời khỏi Composable này
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                ttsHelper.speak(word)
            },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Phát âm",
                modifier = Modifier.size(config.iconSize),
                tint = if (isTtsReady) Color(0xFF2196F3)
                       else Color.LightGray
            )
        }

        Text(
            text = word,
            style = config.largeTextStyle,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}

@Composable
private fun BackContent(vocabulary: Vocabulary,
                        isShowDetail: Boolean = false,
                        config: FlashcardContentCardConfig = FlashcardContentCardConfig()
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = vocabulary.meaning,
            style = config.largeTextStyle,
            modifier = Modifier.graphicsLayer { rotationY = 180f }
        )
        Spacer(modifier = Modifier.height(config.spacerHeight))
        Text(
            text = vocabulary.example,
            style = config.mediumTextStyle,
            modifier = Modifier.graphicsLayer { rotationY = 180f }
        )
    }
    if (isShowDetail) {
        DetailCard(vocabulary)
    }

}