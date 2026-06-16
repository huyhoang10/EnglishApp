package com.example.efishapp.feature.dashboard.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.R

data class ReviewCardConfig(
    val cardPadding: Dp = 16.dp,
    val contentPadding: Dp = 16.dp,
    val cornerRadius: Dp = 16.dp,
    val elevation: Dp = 4.dp,
    val titleFontSize: TextUnit = 20.sp,
    val reviewFontSize: TextUnit = 44.sp,
    val iconSize: Dp = 40.dp,
    val backgroundColor: Color = Color.White
)

@Composable
fun LevelCard(
    numReview: Int,
    config: ReviewCardConfig = ReviewCardConfig()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(config.cardPadding),
        shape = RoundedCornerShape(config.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = config.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = config.elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(config.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Review",
                fontSize = config.titleFontSize,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = numReview.toString(),
                    fontSize = config.reviewFontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(32.dp))
                Image(
                    painter = painterResource(R.drawable.streakicon),
                    contentDescription = null,
                    modifier = Modifier.size(config.iconSize)
                )
            }
        }
    }
}