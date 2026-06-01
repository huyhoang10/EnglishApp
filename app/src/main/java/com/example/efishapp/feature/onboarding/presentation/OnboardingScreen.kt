package com.example.efishapp.feature.onboarding.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.efishapp.R
import com.example.efishapp.feature.folder.presentation.theme.GlassBackground
import com.example.efishapp.feature.folder.presentation.theme.GlassBorder
import com.example.efishapp.feature.folder.presentation.theme.GradientEnd
import com.example.efishapp.feature.folder.presentation.theme.GradientStart
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_welcome_title),
            description = stringResource(R.string.onboarding_welcome_desc),
            imageRes = R.drawable.step1,
            backgroundColor = Color(0xFF4C58BA)
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_learn_title),
            description = stringResource(R.string.onboarding_learn_desc),
            imageRes = R.drawable.step2,
            backgroundColor = Color(0xFF4C58BA)
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_practice_title),
            description = stringResource(R.string.onboarding_practice_desc),
            imageRes = R.drawable.step3,
            backgroundColor = Color(0xFF4C58BA)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageItem(page = pages[page])
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingIndicator(
                pageCount = 3,
                currentPage = pagerState.currentPage
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_skip),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(80.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                if (pagerState.currentPage < 2) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_next),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Button(
                        onClick = onNavigateToAuth,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = GradientStart
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_get_started),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
