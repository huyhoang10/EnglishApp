package com.example.efishapp.feature.dashboard.domain

import java.util.Calendar
import java.util.Date


enum class DayOfWeek{
    Mon,
    Tue,
    Wed,
    Thu,
    Fri,
    Sat,
    Sun
}

data class DailyVocabTracker(
    val userId: String = "",
    val date: Date = Calendar.getInstance().time,
    val dayOfWeek: DayOfWeek = DayOfWeek.Mon,
    val reviewVocabCount: Int,
    val newVocabCount: Int
) {
    val total: Int get() = reviewVocabCount + newVocabCount
}

data class MonthlyStudyTracker(
    val userId: String = "",
    val year: Int = 2026,
    val correctVocabCount: Long = 0,
    val wrongVocabCount: Long = 0,
    val month: Int = 6,
) {
    val totalWords: Long = correctVocabCount + wrongVocabCount
}

