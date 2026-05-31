package com.example.efishapp.feature.flashcard.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.feature.flashcard.presentation.Vocabulary

/**
 * 1. Quản lý toàn bộ thông số diện mạo, kích thước và kiểu chữ của Flashcard (Style Tokens).
 * Các kiểu văn bản khác nhau được gom nhóm gọn gàng inside hai thuộc tính [largeTextStyle] và [mediumTextStyle].
 */
data class FlashcardContentCardConfig(
    val cornerRadius: Dp = 24.dp,
    val elevation: Dp = 2.dp,
    val borderWidth: Dp = 1.dp,
    val borderColor: Color = Color(0xFFE0E0E0),
    val backgroundColor: Color = Color.White,
    val spacerHeight: Dp = 12.dp,
    val animationDurationMillis: Int = 300,
    val cameraDistanceDensity: Float = 12f,

    // Gom cụm các tham số Text thành các đối tượng TextStyle
    val largeTextStyle: TextStyle = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    ),
    val mediumTextStyle: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal, // Mặc định từ example cũ của bạn
        color = Color.Black
    )
)

/**
 * 2. Thành phần Composable thẻ nội dung Flashcard hỗ trợ hiệu ứng lật 3D mặt trước/mặt sau.
 */
@Composable
fun FlashcardContentCard(
    vocabulary: Vocabulary,
    isShowDetail: Boolean,
    modifier: Modifier = Modifier,
    config: FlashcardContentCardConfig = FlashcardContentCardConfig() // Nhận cấu hình tập trung mặc định
) {
    var isFlipped by remember { mutableStateOf(false) }

    val cardRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = config.animationDurationMillis),
        label = "CardRotationAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isFlipped = !isFlipped }
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
                // --- MẶT SAU (Hiển thị Nghĩa và Ví dụ) ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = vocabulary.meaning,
                        style = config.largeTextStyle, // Áp dụng TextStyle kiểu lớn
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    )
                    Spacer(modifier = Modifier.height(config.spacerHeight))
                    Text(
                        text = vocabulary.example,
                        style = config.mediumTextStyle, // Áp dụng TextStyle kiểu trung bình
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    )
                }
                if (isShowDetail) {
                    DetailCard(vocabulary)
                }
            } else {
                // --- MẶT TRƯỚC (Hiển thị Từ gốc) ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = vocabulary.word,
                        style = config.largeTextStyle // Áp dụng TextStyle kiểu lớn
                    )
                }
            }
        }
    }
}