package com.example.efishapp.feature.flashcard.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.flashcard.presentation.FlashcardUiEvent

data class FlashcardBottomNavConfig(
    val barHeight: Dp = 64.dp,
    val horizontalPadding: Dp = 24.dp,
    val iconSize: Dp = 28.dp,
    val containerColor: Color = Color(0xFFF7F9FA),
    val iconTint: Color = Color.Gray
)

@Composable
fun FlashcardBottomNavigation(
    onClickBack: ()-> Unit,
    onClickDetail: ()-> Unit,
    modifier: Modifier = Modifier,
    config: FlashcardBottomNavConfig = FlashcardBottomNavConfig()
) {
    NavigationBar(
        containerColor = config.containerColor,
        modifier = modifier.height(config.barHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = config.horizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onClickBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = config.iconTint,
                    modifier = Modifier.size(config.iconSize)
                )
            }

            IconButton(onClick = onClickDetail) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Detail",
                    tint = config.iconTint,
                    modifier = Modifier.size(config.iconSize)
                )
            }
        }
    }
}