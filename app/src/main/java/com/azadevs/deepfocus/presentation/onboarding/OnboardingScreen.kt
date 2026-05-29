package com.azadevs.deepfocus.presentation.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azadevs.deepfocus.R
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Azamat on 29/05/2026.
 */
@Composable
fun OnboardingScreen(
    onFinishClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val themeColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(page = page)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onSkipClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f),
                    contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_skip),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                        label = "indicatorWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) themeColor else MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.2f
                                )
                            )
                    )
                }
            }

            val isLastPage = pagerState.currentPage == 2
            Button(
                onClick = {
                    if (isLastPage) {
                        onFinishClick()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isLastPage) stringResource(id = R.string.onboarding_finish) else stringResource(
                            id = R.string.onboarding_next
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (!isLastPage) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: Int) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 80.dp, bottom = 160.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (page) {
                0 -> MinimalPomodoroIllustration(modifier = Modifier.size(180.dp))
                1 -> MinimalStreakIllustration(modifier = Modifier.size(180.dp))
                2 -> MinimalAnalyticsIllustration(modifier = Modifier.size(180.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            val title = when (page) {
                0 -> stringResource(id = R.string.onboarding_title_1)
                1 -> stringResource(id = R.string.onboarding_title_2)
                else -> stringResource(id = R.string.onboarding_title_3)
            }

            val subtitle = when (page) {
                0 -> stringResource(id = R.string.onboarding_desc_1)
                1 -> stringResource(id = R.string.onboarding_desc_2)
                else -> stringResource(id = R.string.onboarding_desc_3)
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun MinimalPomodoroIllustration(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "minimalTimer")
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 270f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweepAngle"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onBackground

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width * 0.35f

        drawCircle(
            color = primaryColor.copy(alpha = 0.2f),
            center = center,
            radius = radius,
            style = Stroke(width = 6.dp.toPx())
        )

        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
        )

        drawCircle(
            color = onSurfaceColor,
            center = center,
            radius = 6.dp.toPx()
        )

        drawLine(
            color = onSurfaceColor,
            start = center,
            end = Offset(
                center.x + (radius * 0.6f * cos(Math.toRadians(-60.0))).toFloat(),
                center.y + (radius * 0.6f * sin(Math.toRadians(-60.0))).toFloat()
            ),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun MinimalStreakIllustration(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "minimalStreak")
    val pulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.scale(pulse)) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2, height / 2)
        val wUnit = width / 100f
        val hUnit = height / 100f

        val flamePath = Path().apply {
            moveTo(center.x, center.y - (30 * hUnit))
            cubicTo(
                center.x - (25 * wUnit), center.y - (10 * hUnit),
                center.x - (30 * wUnit), center.y + (15 * hUnit),
                center.x, center.y + (30 * hUnit)
            )
            cubicTo(
                center.x + (30 * wUnit), center.y + (15 * hUnit),
                center.x + (25 * wUnit), center.y - (10 * hUnit),
                center.x, center.y - (30 * hUnit)
            )
            close()
        }

        drawPath(
            path = flamePath,
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor, primaryColor.copy(alpha = 0.3f))
            )
        )

        val innerFlamePath = Path().apply {
            moveTo(center.x, center.y - (10 * hUnit))
            cubicTo(
                center.x - (15 * wUnit), center.y,
                center.x - (15 * wUnit), center.y + (10 * hUnit),
                center.x, center.y + (20 * hUnit)
            )
            cubicTo(
                center.x + (15 * wUnit), center.y + (10 * hUnit),
                center.x + (15 * wUnit), center.y,
                center.x, center.y - (10 * hUnit)
            )
            close()
        }

        drawPath(
            path = innerFlamePath,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun MinimalAnalyticsIllustration(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "minimalAnalytics")
    val heightFactor by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heightFactor"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onBackground

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2, height / 2)

        val chartWidth = width * 0.7f
        val chartHeight = height * 0.5f
        val startX = center.x - (chartWidth / 2)
        val bottomY = center.y + (chartHeight / 2)

        drawLine(
            color = onSurfaceColor.copy(alpha = 0.2f),
            start = Offset(startX - 10.dp.toPx(), bottomY),
            end = Offset(startX + chartWidth + 10.dp.toPx(), bottomY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        val barWidth = 24.dp.toPx()
        val spacing = (chartWidth - (barWidth * 3)) / 2
        val heights = listOf(0.4f, 0.9f, 0.6f)

        for (i in 0 until 3) {
            val ratio = heights[i] * heightFactor
            val barH = chartHeight * ratio
            val x = startX + (i * (barWidth + spacing))
            val y = bottomY - barH

            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}
