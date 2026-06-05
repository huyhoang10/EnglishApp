package com.example.efishapp.core.util

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