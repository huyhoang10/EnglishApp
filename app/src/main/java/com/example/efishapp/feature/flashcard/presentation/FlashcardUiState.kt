package com.example.efishapp.feature.flashcard.presentation

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
        repetitions = 0,
        interval = 0,
        easinessFactor = 2.5f
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
        repetitions = 0,
        interval = 0,
        easinessFactor = 2.5f
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
        repetitions = 0,
        interval = 0,
        easinessFactor = 2.5f
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
        repetitions = 0,
        interval = 0,
        easinessFactor = 2.5f
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
        repetitions = 0,
        interval = 0,
        easinessFactor = 2.5f
    )
)

data class FlashcardUiState(
    val indexWord: Int = 0,
    val countForget: Int = 0,
    val countRemember: Int = 0,
    val isFlipped: Boolean = false,
    val isShowDetail: Boolean = false,
    val vocabularies: List<Vocabulary> = sampleVocabularies,
    val isFinished: Boolean = false
)

sealed interface FlashcardUiEvent{
    object OnFlipCard : FlashcardUiEvent
    object OnClickAgainAction: FlashcardUiEvent
    object OnClickHardAction: FlashcardUiEvent
    object OnClickGoodAction: FlashcardUiEvent
    object OnClickEasyAction: FlashcardUiEvent
    object OnClickBack: FlashcardUiEvent
    object OnClickDetail: FlashcardUiEvent
}
