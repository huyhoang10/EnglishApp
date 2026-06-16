package com.example.efishapp.feature.dashboard.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.R
import com.example.efishapp.core.ui.typography.ChartTypography
import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import com.example.efishapp.feature.dashboard.domain.DayOfWeek
import kotlin.math.ceil

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawRoundRect(color = color, cornerRadius = CornerRadius(4f, 4f))
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = ChartTypography.legend)
    }
}
data class BarChartConfig(
    val chartHeight: Dp = 220.dp,
    val paddingLeft: Dp = 25.dp,
    val paddingBottom: Dp = 30.dp,
    val paddingTop: Dp = 10.dp,
    val barGroupWidthRatio: Float = 0.7f, // Tỷ lệ chiều ngang của cả NHÓM cột (Review + New) so với khoảng cách
    val barGap: Dp = 2.dp, // Khoảng cách nhỏ giữa 2 cột TRONG CÙNG 1 NHÓM (Ôn tập và Mới)
    val yStepCount: Int = 4,
    val reviewColor: Color = Color(0xFFFFA726),
    val newColor: Color = Color(0xFFFFF176),
    val axisColor: Color = Color.LightGray
)

@Composable
fun WeeklyVocabularyChart(
    weeklyLearningStats: List<DailyVocabTracker>,
    modifier: Modifier = Modifier,
    config: BarChartConfig = BarChartConfig()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val textMeasurer = rememberTextMeasurer()

        val rawMax = weeklyLearningStats.maxOfOrNull { maxOf(it.reviewVocabCount, it.newVocabCount) }?.toFloat() ?: 10f
        // maxOfOrNull nên lấy giá trị cao nhất trong 2 loại cột, không phải total
        val maxAxisValue = (ceil(rawMax / 10f) * 10f).coerceAtLeast(10f)

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.dashboard_titleWeeklyChart),
                style = ChartTypography.title,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.chartHeight)
            ) {
                val paddingLeftPx = config.paddingLeft.toPx()
                val paddingBottomPx = config.paddingBottom.toPx()
                val paddingTopPx = config.paddingTop.toPx()

                val chartWidth = size.width - paddingLeftPx
                val chartHeight = size.height - paddingBottomPx - paddingTopPx

                // Vẽ trục Y và lưới (Giữ nguyên)
                for (i in 0..config.yStepCount) {
                    val ratio = i.toFloat() / config.yStepCount
                    val yPos = paddingTopPx + chartHeight * (1 - ratio)
                    val labelValue = (maxAxisValue * ratio).toInt()

                    val yLabelResult = textMeasurer.measure(
                        text = labelValue.toString(),
                        style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                    )

                    val textX = paddingLeftPx - yLabelResult.size.width - 8.dp.toPx()
                    val textY = yPos - yLabelResult.size.height / 2f

                    drawText(
                        textMeasurer = textMeasurer,
                        text = labelValue.toString(),
                        topLeft = Offset(textX, textY)
                    )

                    if (i > 0) {
                        drawLine(
                            color = config.axisColor.copy(alpha = 0.4f),
                            start = Offset(paddingLeftPx, yPos),
                            end = Offset(size.width, yPos),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                // Vẽ trục tọa độ (Giữ nguyên)
                drawLine(
                    color = config.axisColor,
                    start = Offset(paddingLeftPx, paddingTopPx + chartHeight),
                    end = Offset(size.width, paddingTopPx + chartHeight),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = config.axisColor,
                    start = Offset(paddingLeftPx, paddingTopPx),
                    end = Offset(paddingLeftPx, paddingTopPx + chartHeight),
                    strokeWidth = 1.5.dp.toPx()
                )

                val dayCount = weeklyLearningStats.size
                if (dayCount > 0) {
                    // PHẦN SỬA ĐỔI CHÍNH Ở ĐÂY: Tính toán cột đôi

                    // Chiều ngang của NHÓM (2 cột + khoảng cách nhỏ ở giữa)
                    val groupWidth = chartWidth / (dayCount * (1f / config.barGroupWidthRatio))
                    // Khoảng cách lớn giữa CÁC NHÓM ngày
                    val spacingBetweenGroups = (chartWidth - (groupWidth * dayCount)) / (dayCount + 1)
                    val barGapPx = config.barGap.toPx()
                    // Chiều ngang thực tế của MỖI CỘT (Ôn tập và Mới bằng nhau)
                    val actualBarWidth = (groupWidth - barGapPx) / 2f

                    weeklyLearningStats.forEachIndexed { index, data ->
                        // Offset bắt đầu của cả NHÓM ngày
                        val groupXOffset = paddingLeftPx + spacingBetweenGroups + index * (groupWidth + spacingBetweenGroups)

                        // Chiều cao tính riêng cho từng cột
                        val reviewHeight = (data.reviewVocabCount.toFloat() / maxAxisValue) * chartHeight
                        val newHeight = (data.newVocabCount.toFloat() / maxAxisValue) * chartHeight

                        val baseLineY = paddingTopPx + chartHeight

                        // CỘT 1: Từ ôn tập (Vẽ bên trái nhóm)
                        val reviewXOffset = groupXOffset
                        val reviewTop = baseLineY - reviewHeight
                        if (data.reviewVocabCount >= 0) { // Vẽ cả khi = 0 để giữ chỗ
                            drawRoundRect(
                                color = config.reviewColor,
                                topLeft = Offset(reviewXOffset, reviewTop),
                                size = Size(actualBarWidth, reviewHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }

                        // CỘT 2: Từ học mới (Vẽ bên phải nhóm)
                        val newXOffset = groupXOffset + actualBarWidth + barGapPx
                        val newTop = baseLineY - newHeight
                        if (data.newVocabCount >= 0) {
                            drawRoundRect(
                                color = config.newColor,
                                topLeft = Offset(newXOffset, newTop),
                                size = Size(actualBarWidth, newHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }

                        // Nhãn trục X: Đặt ở giữa CẢ NHÓM
                        val dayLabel = data.dayOfWeek.name
                        val xLabelResult = textMeasurer.measure(
                            text = dayLabel,
                            style = ChartTypography.axisLable
                        )
                        val labelX = groupXOffset + (groupWidth - xLabelResult.size.width) / 2f
                        val labelY = baseLineY + 6.dp.toPx()

                        drawText(
                            textMeasurer = textMeasurer,
                            text = dayLabel,
                            topLeft = Offset(labelX, labelY)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phần chú thích (Giữ nguyên)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LegendItem(color = config.reviewColor, label = stringResource(R.string.dashboard_reviewVocabLabel))
                Spacer(modifier = Modifier.width(20.dp))
                LegendItem(color = config.newColor, label = stringResource(R.string.dashboard_newVocabLabel))
            }
        }
    }
}

@Preview(showBackground = true, name = "Weekly Chart Preview")
@Composable
fun WeeklyVocabularyChartPreview() {
    // 1. Tạo dữ liệu giả lập cho 7 ngày trong tuần
    val mockWeeklyStats = listOf(
        DailyVocabTracker(dayOfWeek = DayOfWeek.Mon, reviewVocabCount = 15, newVocabCount = 5),
        DailyVocabTracker(dayOfWeek = DayOfWeek.Tue, reviewVocabCount = 8, newVocabCount = 12),
        DailyVocabTracker(dayOfWeek = DayOfWeek.Wed, reviewVocabCount = 22, newVocabCount = 0), // Test trường hợp = 0
        DailyVocabTracker(dayOfWeek = DayOfWeek.Thu, reviewVocabCount = 0, newVocabCount = 18),  // Test trường hợp = 0
        DailyVocabTracker(dayOfWeek = DayOfWeek.Fri, reviewVocabCount = 14, newVocabCount = 9),
        DailyVocabTracker(dayOfWeek = DayOfWeek.Sat, reviewVocabCount = 5, newVocabCount = 4),
        DailyVocabTracker(dayOfWeek = DayOfWeek.Sun, reviewVocabCount = 25, newVocabCount = 15)
    )

    // 2. Đặt trong một Box nền xám nhẹ để nổi bật Card màu trắng của biểu đồ
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        WeeklyVocabularyChart(
            weeklyLearningStats = mockWeeklyStats
        )
    }
}