package com.example.efishapp.feature.dashboard.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.efishapp.R
import com.example.efishapp.feature.dashboard.presentation.DashboardUiState

// 1. Quản lý diện mạo, kích thước hệ thống (Style Tokens)
data class StatCardStyleConfig(
    val cardPadding: Dp = 16.dp,
    val contentPadding: Dp = 16.dp,
    val cornerRadius: Dp = 16.dp,
    val elevation: Dp = 4.dp,
    val titleFontSize: TextUnit = 20.sp,
    val valueFontSize: TextUnit = 44.sp,
    val iconSize: Dp = 40.dp,
    val backgroundColor: Color = Color.White
)

// 2. Định nghĩa nội dung thay đổi linh hoạt cho từng loại dữ liệu khác nhau
data class StatCardData(
    val title: String,
    val value: Long,
    @DrawableRes val iconRes: Int,
    val contentDescription: String? = null
)

@Composable
fun StatCard(
    data: StatCardData,
    modifier: Modifier = Modifier,
    styleConfig: StatCardStyleConfig = StatCardStyleConfig()
) {
    Card(
        modifier = modifier
            .padding(styleConfig.cardPadding),
        shape = RoundedCornerShape(styleConfig.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = styleConfig.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = styleConfig.elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(styleConfig.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.title,
                fontSize = styleConfig.titleFontSize,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.value.toString(),
                    fontSize = styleConfig.valueFontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(32.dp))
                Image(
                    painter = painterResource(id = data.iconRes),
                    contentDescription = data.contentDescription,
                    modifier = Modifier.size(styleConfig.iconSize)
                )
            }
        }
    }
}
@Composable
fun StreakCard(
    streak: Long = 0,
    modifier: Modifier = Modifier
){
    StatCard(
        data = StatCardData(title = stringResource(R.string.dashboard_streakTitle), value = streak, iconRes = R.drawable.streakicon),
        modifier = modifier
    )
}

@Composable
fun ReviewCard(
    numVocabularyReview: Long = 0,
    modifier: Modifier = Modifier
){
    StatCard(
        data = StatCardData(title = stringResource(R.string.dashboard_reviewTitle), value = numVocabularyReview, iconRes = R.drawable.reviewicon), // Bạn nhớ đổi R.drawable.review_icon tương ứng nhé
        modifier = modifier
    )
}