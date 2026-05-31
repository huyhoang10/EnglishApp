package com.example.efishapp.feature.flashcard.presentation.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 1. Quản lý toàn bộ thông số diện mạo, kích thước và kiểu chữ của FlashcardHeader (Style Tokens).
 */
data class FlashcardHeaderConfig(
    val spacerHeight: Dp = 12.dp,
    val pillWidth: Dp = 100.dp,
    val pillHeight: Dp = 40.dp,
    val pillBorderWidth: Dp = 2.dp,
    val pillBackgroundColor: Color = Color(0xFFF0F4F8),

    // Tổ chức các thuộc tính văn bản thành TextStyle thống nhất
    val progressTextStyle: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    ),
    val pillTextStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    ),

    // Cấu hình mặc định cho viền của 2 nút Pill
    val leftPillBorderColor: Color = Color(0xFFFFE0B2),  // Viền cam nhạt
    val rightPillBorderColor: Color = Color(0xFFC8E6C9)  // Viền xanh nhạt
)

/**
 * 2. Thành phần Tiêu đề hiển thị số tiến độ học và các bộ đếm số lượng từ vựng.
 */
@Composable
fun FlashcardHeader(
    progressText: String,
    leftCount: String,
    rightCount: String,
    modifier: Modifier = Modifier,
    config: FlashcardHeaderConfig = FlashcardHeaderConfig() // Nhận cấu hình tập trung mặc định
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Số chỉ tiến độ (ví dụ: 10/20)
        Text(
            text = progressText,
            style = config.progressTextStyle,
        )

        Spacer(modifier = Modifier.height(config.spacerHeight))

        // Hàng chứa 2 nút Pill kéo giãn ra 2 bên mép
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Pill bên trái (Bộ đếm 1)
            PillIndicator(
                text = leftCount,
                borderColor = config.leftPillBorderColor,
                config = config
            )

            // Nút Pill bên phải (Bộ đếm 2)
            PillIndicator(
                text = rightCount,
                borderColor = config.rightPillBorderColor,
                config = config
            )
        }
    }
}

/**
 * 3. Thành phần Composable con biểu thị nút hình viên thuốc chứa số lượng từ.
 */
@Composable
fun PillIndicator(
    text: String,
    borderColor: Color,
    config: FlashcardHeaderConfig,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(config.pillWidth).height(config.pillHeight),
        shape = CircleShape,
        color = config.pillBackgroundColor,
        border = BorderStroke(config.pillBorderWidth, borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // CĂN GIỮA HOÀN TOÀN TẠI ĐÂY
        ) {
            Text(
                text = "$text",
                style = config.pillTextStyle
            )
        }
    }
}