package com.example.efishapp.feature.game.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.feature.game.domain.model.GameLevel
import com.example.efishapp.feature.game.domain.model.GameType

private val PrimaryBlue = Color(0xFF1E3A8A)
private val SuccessGreen = Color(0xFF10B981)
private val WarningAmber = Color(0xFFF59E0B)
private val ErrorRed = Color(0xFFEF4444)
private val LightGray = Color(0xFFE2E8F0)

@Composable
fun GameResultScreen(
    correctAnswers: Int,
    wrongAnswers: Int,
    gameType: GameType,
    gameLevel: GameLevel,
    onPlayAgain: () -> Unit,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = correctAnswers + wrongAnswers
    val score = if (total > 0) (correctAnswers * 100) / total else 0
    val scoreColor = when {
        score >= 80 -> SuccessGreen
        score >= 50 -> WarningAmber
        else -> ErrorRed
    }

    val rank = when {
        score >= 90 -> Rank.PERFECT
        score >= 70 -> Rank.EXCELLENT
        score >= 50 -> Rank.GOOD
        score >= 30 -> Rank.OKAY
        else -> Rank.TRY_AGAIN
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = rank.icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = rank.color
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(rank.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = rank.color)
        Spacer(modifier = Modifier.height(8.dp))
        Text(rank.subtitle, fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))

        ScoreCircle(score = score, color = scoreColor, modifier = Modifier.size(160.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Chi tiết", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Spacer(modifier = Modifier.height(16.dp))
                DetailRow("Trò chơi", gameType.displayName, PrimaryBlue)
                DetailRow("Cấp độ", gameLevel.displayName, PrimaryBlue)
                DetailRow("Câu đúng", "$correctAnswers / $total", SuccessGreen)
                DetailRow("Câu sai", "$wrongAnswers / $total", ErrorRed)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onGoBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Về trang chủ", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Replay, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chơi lại", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ScoreCircle(score: Int, color: Color, modifier: Modifier = Modifier) {
    var animatedScore by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(score) { animatedScore = score.toFloat() }

    val animatedValue by animateFloatAsState(
        targetValue = animatedScore,
        animationSpec = tween(durationMillis = 1500),
        label = "score_animation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            drawCircle(color = LightGray, radius = radius, style = Stroke(width = strokeWidth))
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = (animatedValue / 100f) * 360f,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${animatedValue.toInt()}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = color)
            Text("diem", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

private enum class Rank(
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    PERFECT("Xuất sắc!", "Bạn là thiên tài!", Color(0xFFFFD700), Icons.Default.EmojiEvents),
    EXCELLENT("Tuyet voi!", "Kết quả rất ấn tượng!", SuccessGreen, Icons.Default.EmojiEvents),
    GOOD("Tốt lắm!", "Cố gắng hơn nữa nhé!", Color(0xFF3B82F6), Icons.Default.EmojiEvents),
    OKAY("Cũng cũng!", "Hãy luyện tập nhiều hơn nhé!", WarningAmber, Icons.Default.EmojiEvents),
    TRY_AGAIN("Cần cố gắng!", "Don't give up, thử lại nào!", ErrorRed, Icons.Default.Replay)
}
