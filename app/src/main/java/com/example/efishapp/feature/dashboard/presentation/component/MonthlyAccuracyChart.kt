package com.example.efishapp.feature.dashboard.presentation.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.efishapp.core.ui.typography.ChartTypography
import com.example.efishapp.feature.dashboard.domain.MonthlyStudyTracker


@Composable
fun MonthlyStatsScreen(monthlyLearningStat: MonthlyStudyTracker) {
    Column(modifier = Modifier.fillMaxSize()) {
        MonthlyAccuracyChart(monthlyStudyTracker = monthlyLearningStat)
    }
}

data class PieChartConfig(
    val chartSize: Dp = 150.dp,
    val canvasSize: Dp = 140.dp,
    val strokeWidth: Dp = 25.dp,
    val correctColor: Color = Color(0xFF2D62ED),
    val forgottenColor: Color = Color(0xFFFFD571)
)

@Composable
fun MonthlyAccuracyChart(
    monthlyStudyTracker: MonthlyStudyTracker,
    config: PieChartConfig = PieChartConfig() // Sử dụng cấu hình tập trung từ ngoài
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Độ chính xác từ vựng", style = ChartTypography.title)
            Text(text = "Tháng ${monthlyStudyTracker.month}", style = ChartTypography.legend)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatItem(
                        label = "Chính xác",
                        value = monthlyStudyTracker.correctVocabCount.toString(),
                        color = config.correctColor
                    )
                    StatItem(
                        label = "Quên/Sai",
                        value = monthlyStudyTracker.wrongVocabCount.toString(),
                        color = config.forgottenColor
                    )
                }

                Box(
                    modifier = Modifier.size(config.chartSize),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(config.canvasSize)) {
                        val strokeWidthPx = config.strokeWidth.toPx()
                        val total = monthlyStudyTracker.totalWords.toFloat()

                        val correctAngle = if (total > 0) (monthlyStudyTracker.correctVocabCount / total) * 360f else 0f
                        val forgottenAngle = 360f - correctAngle

                        drawArc(
                            color = config.correctColor,
                            startAngle = -90f,
                            sweepAngle = correctAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                        )

                        drawArc(
                            color = config.forgottenColor,
                            startAngle = -90f + correctAngle,
                            sweepAngle = forgottenAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tổng", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "${monthlyStudyTracker.totalWords}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

data class StatItemConfig(
    val dotSize: Dp = 10.dp,
    val dotCornerRadius: Dp = 2.dp,
    val valueFontSize: TextUnit = 18.sp,
    val valueColor: Color = Color.DarkGray
)
@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color,
    showDot: Boolean = true,
    config: StatItemConfig = StatItemConfig()
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showDot) {
                Surface(
                    modifier = Modifier.size(config.dotSize),
                    shape = RoundedCornerShape(config.dotCornerRadius),
                    color = color
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = label, style = ChartTypography.legend)
        }
        Text(
            text = value,
            fontSize = config.valueFontSize,
            fontWeight = FontWeight.Bold,
            color = config.valueColor,
            modifier = Modifier.padding(start = if (showDot) (config.dotSize + 8.dp) else 0.dp)
        )
    }
}

