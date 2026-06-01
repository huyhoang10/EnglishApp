package com.example.efishapp.feature.flashcard.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class CongratulationConfig(
    val backgroundColor: Color = Color(0xFFF7F9FA),
    val cardColor: Color = Color.White,
    val rememberColor: Color = Color(0xFF4CAF50),
    val forgetColor: Color = Color(0xFFF44336),
    val primaryButtonColor: Color = Color(0xFF2196F3),
    val paddingLarge: Dp = 24.dp,
    val paddingMedium: Dp = 16.dp,
    val spacerHeight: Dp = 16.dp,

    val titleStyle: TextStyle = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A237E) // Màu xanh đậm hoàng gia
    ),
    val subtitleStyle: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Gray
    ),
    val scoreNumberStyle: TextStyle = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    val scoreLabelStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = Color.DarkGray
    )
)


@Composable
fun CongratulationScreen(
    totalRemember: Int,
    totalForget: Int,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
    config: CongratulationConfig = CongratulationConfig()
) {
    val totalWords = totalRemember + totalForget
    val accuracy = if (totalWords > 0) (totalRemember * 100) / totalWords else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(config.backgroundColor)
            .padding(config.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🎉 Awesome!",
            style = config.titleStyle,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You have completed this flashcard set!",
            style = config.subtitleStyle,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(config.paddingLarge * 1.5f))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = config.cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(config.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalRemember",
                            style = config.scoreNumberStyle.copy(color = config.rememberColor)
                        )
                        Text(
                            text = "Remembered",
                            style = config.scoreLabelStyle
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalForget",
                            style = config.scoreNumberStyle.copy(color = config.forgetColor)
                        )
                        Text(
                            text = "Forgot",
                            style = config.scoreLabelStyle
                        )
                    }
                }

                Spacer(modifier = Modifier.height(config.spacerHeight * 1.5f))

                Text(
                    text = "Accuracy: $accuracy%",
                    style = config.subtitleStyle.copy(
                        color = when {
                            accuracy >= 80 -> config.rememberColor
                            accuracy >= 50 -> config.primaryButtonColor
                            else -> config.forgetColor
                        },
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(config.paddingLarge * 2f))

        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = config.primaryButtonColor)
        ) {
            Text(
                text = "Continue Learning",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CongratulationScreenPreview() {
    CongratulationScreen(
        totalRemember = 4,
        totalForget = 1,
        onBackToHome = {}
    )
}