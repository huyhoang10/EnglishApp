package com.example.efishapp.feature.dashboard.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GreetingCardConfig(
    val cardPadding: Dp = 16.dp,
    val contentPadding: Dp = 16.dp,
    val cornerRadius: Dp = 16.dp,
    val elevation: Dp = 4.dp,
    val backgroundColor: Color = Color.White,
    val iconSize: Dp = 24.dp,
    val iconColor: Color = Color.Gray,
    val welcomeFontSize: TextUnit = 20.sp,
    val subtitleFontSize: TextUnit = 14.sp
)

@Composable
fun GreetingCard(
    name: String,
    onUserProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    config: GreetingCardConfig = GreetingCardConfig(), // Nhận cấu hình tập trung
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(config.cardPadding),
        shape = RoundedCornerShape(config.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = config.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = config.elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(config.contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Đẩy text sang trái, icon sang phải
        ) {
            IconButton(onClick = onUserProfileClick) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle, // Sử dụng vector mẫu của Material Design
                    contentDescription = "Notifications",
                    modifier = Modifier.size(config.iconSize),
                    tint = config.iconColor
                )
            }
            // Phần 1: Nội dung chữ bên trái
            Column(

            ) {
                Text(
                    text = "Hi, $name!",
                    fontSize = config.welcomeFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,

                )
                Text(
                    text = "Have a good day",
                    fontSize = config.subtitleFontSize,
                    color = Color.Gray,
                )
            }

            // Phần 2: Icon chuông tương tác bên phải
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Filled.Notifications, // Sử dụng vector mẫu của Material Design
                    contentDescription = "Notifications",
                    modifier = Modifier.size(config.iconSize),
                    tint = config.iconColor
                )
            }
        }
    }
}

@Preview
@Composable
fun GreetingCardPreview(){
    GreetingCard("Hoàng",{},{} )
}