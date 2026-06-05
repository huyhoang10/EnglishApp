package com.example.efishapp.feature.dashboard.data.repository

import com.example.efishapp.core.util.getCurrentMonthKey
import com.example.efishapp.feature.dashboard.domain.MonthTrackerReposity
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MonthlyTrackerRepositoryImpl(): MonthTrackerReposity{
    override suspend fun getMonthStats(userId: String): MonthlyStudyTracker {
        val db = FirebaseFirestore.getInstance()

        // 1. Lấy tháng hiện tại (Ví dụ đang là tháng 6 -> "jun")
        val currentMonth = getCurrentMonthKey()

        try {
            // 2. Trỏ trực tiếp đến document của User
            val docRef = db.collection("monthly_study_tracker").document(userId)

            // 3. Thực hiện get dữ liệu từ Firestore
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                val monthlyStatsMap = snapshot.get("monthly_stats") as? Map<String, Any>

                val currentMonthMap = monthlyStatsMap?.get(currentMonth) as? Map<String, Any>

                if (currentMonthMap != null) {
                    return MonthlyStudyTracker(
                        correctVocabCount = (currentMonthMap["correctVocabCount"] as? Long) ?: 0L,
                        wrongVocabCount = (currentMonthMap["wrongVocabCount"] as? Long) ?: 0L
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Xử lý lỗi (ví dụ: mất mạng, sai userId...) và trả về object rỗng hoặc throw exception tùy bạn
        }

        // Trả về object mặc định (0, 0) nếu không tìm thấy dữ liệu của tháng đó hoặc user đó
        return MonthlyStudyTracker()
    }
    override suspend fun updateMonthlyStats(userId: String, monthlyStudyTracker: MonthlyStudyTracker){

    }
    override suspend fun undateStreak(userId: String, streak:Int){

    }
}