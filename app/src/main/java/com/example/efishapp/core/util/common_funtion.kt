package com.example.efishapp.core.util

import com.example.efishapp.feature.dashboard.domain.DayOfWeek
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


fun getCurrentMonthKey(): String {
    val calendar = Calendar.getInstance()
    // "MMM" với Locale.US sẽ trả về "Jan", "Feb", "Mar", "Apr"...
    val monthFormat = SimpleDateFormat("MMM", Locale.US)
    // Chuyển về chữ thường (lowercase) để khớp hoàn toàn với database của bạn ("jan", "feb"...)
    return monthFormat.format(calendar.time).lowercase()
}

fun getCurrentDayOfWeek(): DayOfWeek {
    val calendar = Calendar.getInstance()

    // Calendar.DAY_OF_WEEK trả về: SUNDAY = 1, MONDAY = 2, ..., SATURDAY = 7
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> DayOfWeek.Mon
        Calendar.TUESDAY -> DayOfWeek.Tue
        Calendar.WEDNESDAY -> DayOfWeek.Wed
        Calendar.THURSDAY -> DayOfWeek.Thu
        Calendar.FRIDAY -> DayOfWeek.Fri
        Calendar.SATURDAY -> DayOfWeek.Sat
        Calendar.SUNDAY -> DayOfWeek.Sun
        else -> DayOfWeek.Mon // Giá trị phòng hờ (Fallback)
    }
}