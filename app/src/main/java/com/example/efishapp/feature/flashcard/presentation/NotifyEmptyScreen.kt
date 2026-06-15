package com.example.efishapp.feature.flashcard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class EmptyReviewConfig(
    val messageText: String = "Hiện không có từ vựng nào cần ôn tập",
    val buttonText: String = "Home",
    val textStyle : TextStyle = TextStyle(fontSize = 18.sp),
    val spacerHeight: Int = 24
)


@Composable
fun EmptyReviewScreen(
     // Khởi tạo mặc định ở đây
    onBackToHomeClick: () -> Unit,
    config: EmptyReviewConfig = EmptyReviewConfig()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dòng thông báo lấy từ config
        Text(
            text = config.messageText,
            style = config.textStyle,
            textAlign = TextAlign.Center
        )

        // Khoảng cách lấy từ config
        Spacer(modifier = Modifier.height(config.spacerHeight.dp))

        // Button lấy chữ từ config
        Button(onClick = onBackToHomeClick) {
            Text(text = config.buttonText)
        }
    }
}

@Preview
@Composable
fun EmptyScreenPreview(){
    EmptyReviewScreen({})
}