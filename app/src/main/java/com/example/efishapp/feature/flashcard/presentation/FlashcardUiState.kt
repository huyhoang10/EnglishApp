package com.example.efishapp.feature.flashcard.presentation

import com.example.efishapp.feature.flashcard.domain.model.ActionType
import com.example.efishapp.feature.flashcard.domain.model.Vocabulary

val sampleVocabularies = listOf(
    Vocabulary(
        word = "Book",
        pronunciation = "/bʊk/",
        meaning = "Quyển sách",
        description = "A set of printed pages fastened together inside a cover.",
        example = "I am reading a book.",
        collocation = "write a book",
        relatedWords = "magazine",
        note = "Có thể dùng như một động từ với nghĩa là 'đặt chỗ'.",

        ),
    Vocabulary(
        word = "Apparent",
        pronunciation = "/əˈpær.ənt/",
        meaning = "Rõ ràng, hiển nhiên",
        description = "Able to be seen or understood easily.",
        example = "It was apparent to everyone that he was dead.",
        collocation = "become apparent",
        relatedWords = "obvious, clear",
        note = "Trạng từ thường dùng là 'apparently' (nghe nói là, hình như).",

    ),
    Vocabulary(
        word = "Collaborate",
        pronunciation = "/kəˈlæb.ə.reɪt/",
        meaning = "Hợp tác, cộng tác",
        description = "To work together with someone for a special purpose.",
        example = "Researchers are collaborating to develop the software.",
        collocation = "collaborate with someone",
        relatedWords = "cooperate, team up",
        note = "Danh từ của nó là 'collaboration'.",

    ),
    Vocabulary(
        word = "Diligent",
        pronunciation = "/ˈdɪl.ɪ.dʒənt/",
        meaning = "Chăm chỉ, cần cù",
        description = "Careful and using a lot of effort.",
        example = "Leo is a diligent student who always completes his homework.",
        collocation = "diligent effort",
        relatedWords = "hard-working, studious",
        note = "Trái nghĩa với 'lazy'.",

    ),
    Vocabulary(
        word = "Evaluate",
        pronunciation = "/ɪˈvæl.ju.eɪt/",
        meaning = "Đánh giá, định giá",
        description = "To judge or calculate the quality, importance, amount, or value of something.",
        example = "We need to evaluate the success of the new strategy.",
        collocation = "carefully evaluate",
        relatedWords = "assess, appraise",
        note = "Danh từ thường gặp là 'evaluation'.",

    )
)

data class FlashcardUiState(
    val vocabularies: List<Vocabulary> = emptyList(),
    val indexWord: Int = 0,
    val countForget: Int = 0,
    val countRemember: Int = 0,
    val isFlipped: Boolean = false,
    val isShowDetail: Boolean = false,
    val isLoading: Boolean = false,
    val isFinished: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false
)

//sealed interface FlashcardUiEvent{
//    object OnFlipCard : FlashcardUiEvent
//    object OnClickDetail: FlashcardUiEvent
//    object OnClickAgainAction: FlashcardUiEvent
//    object OnClickHardAction: FlashcardUiEvent
//    object OnClickGoodAction: FlashcardUiEvent
//    object OnClickEasyAction: FlashcardUiEvent
//    object OnClickBack: FlashcardUiEvent
//    data class LoadVocabularies(val userId: String) : FlashcardUiEvent
//
//}

sealed interface FlashcardUiEvent {
    object OnFlipCard : FlashcardUiEvent
    object OnClickDetail : FlashcardUiEvent
    object OnClickBack: FlashcardUiEvent
    data class OnAnswer(val actionType: ActionType) : FlashcardUiEvent
    data class LoadVocabularies(val userId: String) : FlashcardUiEvent
}