package com.example.efishapp.feature.dashboard.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.core.ui.typography.ChartTypography
import com.example.efishapp.feature.dashboard.domain.DailyVocabTracker
import kotlin.math.ceil



data class BarChartConfig(
    val chartHeight: Dp = 220.dp,
    val paddingLeft: Dp = 15.dp,
    val paddingBottom: Dp = 30.dp,
    val paddingTop: Dp = 10.dp,
    val barWidthRatio: Float = 1.6f,
    val yStepCount: Int = 4,
    val reviewColor: Color = Color(0xFFFFA726),
    val newColor: Color = Color(0xFFFFF176),
    val axisColor: Color = Color.LightGray
)

@Composable
fun WeeklyVocabularyChart(
    weeklyLearningStats: List<DailyVocabTracker>,
    modifier: Modifier = Modifier,
    config: BarChartConfig = BarChartConfig() // Nhận cấu hình từ ngoài vào thông qua data class
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

        val rawMax = weeklyLearningStats.maxOfOrNull { it.total }?.toFloat() ?: 1f
        val maxAxisValue = (ceil(rawMax / 10f) * 10f).coerceAtLeast(10f)

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Thống kê từ vựng trong tuần",
                style = ChartTypography.title,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.chartHeight)
            ) {
                // Đổi đổi đơn vị các thông số từ Dp sang Px thông qua config
                val paddingLeftPx = config.paddingLeft.toPx()
                val paddingBottomPx = config.paddingBottom.toPx()
                val paddingTopPx = config.paddingTop.toPx()

                val chartWidth = size.width - paddingLeftPx
                val chartHeight = size.height - paddingBottomPx - paddingTopPx

                // --- VẼ TRỤC Y & CÁC ĐƯỜNG LƯỚI NGANG ---
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

                // --- VẼ TRỤC TOẠ ĐỘ CHÍNH ---
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

                // --- VẼ CÁC CỘT XẾP CHỒNG & NHÃN TRỤC X ---
                val barCount = weeklyLearningStats.size
                val barWidth = chartWidth / (barCount * config.barWidthRatio)
                val spacing = (chartWidth - (barWidth * barCount)) / (barCount + 1)

                weeklyLearningStats.forEachIndexed { index, data ->
                    val xOffset = paddingLeftPx + spacing + index * (barWidth + spacing)

                    val totalHeight = (data.total.toFloat() / maxAxisValue) * chartHeight
                    val reviewHeight = (data.reviewVocabCount.toFloat() / maxAxisValue) * chartHeight
                    val newHeight = (data.newVocabCount.toFloat() / maxAxisValue) * chartHeight

                    val baseLineY = paddingTopPx + chartHeight

                    // Tầng 1 (Review)
                    if (data.reviewVocabCount > 0) {
                        val reviewTop = baseLineY - reviewHeight
                        drawRoundRect(
                            color = config.reviewColor,
                            topLeft = Offset(xOffset, reviewTop),
                            size = Size(barWidth, reviewHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    // Tầng 2 (New)
                    if (data.newVocabCount > 0) {
                        val newTop = baseLineY - totalHeight
                        drawRoundRect(
                            color = config.newColor,
                            topLeft = Offset(xOffset, newTop),
                            size = Size(barWidth, newHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }

                    // Nhãn trục X
                    val xLabelResult = textMeasurer.measure(
                        text = data.dayOfWeek.toString(),
                        style = ChartTypography.axisLable
                    )
                    val labelX = xOffset + (barWidth - xLabelResult.size.width) / 2f
                    val labelY = baseLineY + 6.dp.toPx()

                    drawText(
                        textMeasurer = textMeasurer,
                        text = data.dayOfWeek.toString(),
                        topLeft = Offset(labelX, labelY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LegendItem(color = config.reviewColor, label = "Từ ôn tập")
                Spacer(modifier = Modifier.width(20.dp))
                LegendItem(color = config.newColor, label = "Từ học mới")
            }
        }
    }
}

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