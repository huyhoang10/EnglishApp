package com.example.efishapp

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.efishapp.core.util.getCurrentMonthKey
import com.example.efishapp.feature.dashboard.data.repository.MonthlyTrackerRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MonthlyTrackerTest {

    @Test
    fun test_getCurrentMonthKey_isCorrect() {
        // Chạy thử hàm lấy tên tháng viết tắt
        val monthKey = getCurrentMonthKey()

        // Kiểm tra xem nó có viết thường và có 3 chữ cái không
        println("Tháng hiện tại là: $monthKey") // In ra: jun
        Assert.assertEquals(3, monthKey.length)
    }

    @Test
    fun test_getMonthlyStats() = runBlocking() {
        // Chạy thử hàm lấy tên tháng viết tắt
        val monthKey = getCurrentMonthKey()
        val monthlyTrackerRepositoryImpl = MonthlyTrackerRepositoryImpl()
        val stats = monthlyTrackerRepositoryImpl.getMonthStats("DuwZLdACmcWoYCqFPhbPdeKy7Mk1")
        // Kiểm tra xem nó có viết thường và có 3 chữ cái không
        // Sử dụng Log.d (Debug) với một cái TAG để dễ tìm kiếm
        Log.d("FIREBASE_TEST", "Tháng hiện tại là: $monthKey")
        Log.d("FIREBASE_TEST", "Dữ liệu stats: $stats")
        Assert.assertEquals(3, monthKey.length)
    }
}