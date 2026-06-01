package com.example.efishapp.feature.flashcard.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.feature.flashcard.presentation.FlashcardUiEvent

data class FlashcardButtonsStyleConfig(
    val buttonHeight: Dp = 44.dp,
    val spacing: Dp = 8.dp,
    val cornerRadius: Dp = 16.dp,
    val textStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.Black)
)

data class FlashcardButtonData(
    val text: String,
    val containerColor: Color,
    val onClick: () -> Unit
)



@Composable
fun FlashcardActionButtons(
    onEvent: (FlashcardUiEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val actionButtons = listOf(
            FlashcardButtonData(text = "Again", containerColor = Color(0xFFE6153C),
                onClick = {onEvent(FlashcardUiEvent.OnClickAgainAction)}),
            FlashcardButtonData(text = "Hard", containerColor = Color(0xFFFFA726),
                onClick = {onEvent(FlashcardUiEvent.OnClickHardAction)}),
            FlashcardButtonData(text = "Good", containerColor = Color(0xFF29B6F6),
                onClick = {onEvent(FlashcardUiEvent.OnClickGoodAction)}),
            FlashcardButtonData(text = "Easy", containerColor = Color(0xFF9CCC65),
                onClick = {onEvent(FlashcardUiEvent.OnClickEasyAction)})
        )
        actionButtons.forEach {dataAction ->
            ActionButton(dataAction, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ActionButton(dataAction: FlashcardButtonData,
                 config: FlashcardButtonsStyleConfig = FlashcardButtonsStyleConfig(),
                 modifier: Modifier = Modifier) {
    Button(
        onClick = dataAction.onClick,
        modifier = modifier.height(config.buttonHeight),
        shape = RoundedCornerShape(config.cornerRadius),
        colors = ButtonDefaults.buttonColors(containerColor = dataAction.containerColor),
    ) {
        Text(
            text = dataAction.text,
            style = config.textStyle ,
            fontWeight = FontWeight.Bold,
        )
    }
}