package com.example.efishapp.feature.flashcard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardActionButtons
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardBottomNavigation
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardContentCard
import com.example.efishapp.feature.flashcard.presentation.component.FlashcardHeader


data class Vocabulary(
    val word: String,
    val pronunciation: String,
    val meaning: String,
    val description: String, // English description
    val example: String,
    val collocation: String,
    val relatedWords: String,
    val note: String
)

@Composable
fun FlashcardScreen(innerPadding: PaddingValues, vocabularies: List<Vocabulary>?) {
    var isShowDetail by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = { FlashcardBottomNavigation({isShowDetail = !isShowDetail}) }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FA))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            FlashcardHeader(
                progressText = "10/20",
                leftCount = "10",
                rightCount = "10"
            )

            Spacer(modifier = Modifier.height(24.dp))

            FlashcardContentCard(
                modifier = Modifier.weight(1f),
                vocabulary = Vocabulary(
                    word = "Book",
                    pronunciation = "/bʊk/",
                    meaning = "Quyển sách",
                    description = "A set of printed pages fastened together inside a cover.",
                    example = "I am reading a book.",
                    collocation = "write a book",
                    relatedWords = "magazine",
                    note = "Có thể dùng như một động từ với nghĩa là 'đặt chỗ'."),
                isShowDetail = isShowDetail)

            Spacer(modifier = Modifier.height(24.dp))

            FlashcardActionButtons()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}





