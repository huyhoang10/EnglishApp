package com.example.efishapp.feature.flashcard.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary

@Composable
fun DetailCard(vocabulary: Vocabulary){
    Card(modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DetailTextRow("Description",vocabulary.description)
            DetailTextRow("Pronunciation",vocabulary.pronunciation)
            DetailTextRow("Collocation",vocabulary.collocation)
            DetailTextRow("Relative words",vocabulary.relatedWords)
            DetailTextRow("Note",vocabulary.note)
        }
    }

}
data class DetailRowStyleConfig(
    val largeTextStyle: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal, color = Color.Red),
    val mediumTextStyle: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Black),
    val padding: Dp = 8.dp,
    val textAlign: TextAlign = TextAlign.Left

)

@Composable
fun DetailTextRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    config: DetailRowStyleConfig = DetailRowStyleConfig()
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "$label:",
            style = config.largeTextStyle,
            textAlign = config.textAlign,
            modifier = Modifier
                .fillMaxWidth()
                .padding(config.padding)
                .graphicsLayer(rotationY = 180f)
        )
        Text(
            text = "$value",
            style = config.mediumTextStyle,
            textAlign = config.textAlign,
            modifier = Modifier
                .fillMaxWidth()
                .padding(config.padding)
                .graphicsLayer(rotationY = 180f)
        )
    }
}