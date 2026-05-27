package com.azadevs.deepfocus.presentation.cosmic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.CosmicModule
import com.azadevs.deepfocus.presentation.cosmic.component.ConfirmPurchaseDialog
import com.azadevs.deepfocus.presentation.cosmic.component.CosmicModuleCard
import com.azadevs.deepfocus.presentation.cosmic.viewmodel.CosmicBaseViewModel
import com.azadevs.deepfocus.presentation.cosmic.viewmodel.CosmicUiEvent

@Composable
fun CosmicBaseScreen(
    viewModel: CosmicBaseViewModel = hiltViewModel()
) {
    val stardust by viewModel.stardust.collectAsStateWithLifecycle()
    val modules by viewModel.modules.collectAsStateWithLifecycle()

    var pendingModule by remember { mutableStateOf<CosmicModule?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is CosmicUiEvent.UnlockSuccess -> {
                    snackbarHostState.showSnackbar("${event.moduleEmoji} Module built successfully!")
                }

                is CosmicUiEvent.UnlockFailed -> {
                    snackbarHostState.showSnackbar("❌ ${event.reason}")
                }
            }
        }
    }

    val unlockedCount = modules.count { it.isUnlocked }
    val totalCount = modules.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C12)) // Deep space black
    ) {
        StarFieldBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CosmicHeader(
                    stardust = stardust,
                    unlockedCount = unlockedCount,
                    totalCount = totalCount,
                    onBalanceClick = { viewModel.addDebugStardust() }
                )
            }

            item {
                EarnHintCard()
            }

            itemsIndexed(
                items = modules,
                key = { _, module -> module.id }
            ) { _, module ->
                CosmicModuleCard(
                    module = module,
                    currentStardust = stardust,
                    onUnlockClick = { pendingModule = module }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 80.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF1E2028),
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp)
            )
        }

        pendingModule?.let { module ->
            ConfirmPurchaseDialog(
                module = module,
                currentStardust = stardust,
                onConfirm = {
                    viewModel.unlockModule(module)
                    pendingModule = null
                },
                onDismiss = { pendingModule = null }
            )
        }
    }
}

@Composable
private fun StarFieldBackground() {
    val stars = remember {
        listOf(
            Offset(0.1f, 0.05f),
            Offset(0.3f, 0.1f),
            Offset(0.7f, 0.07f),
            Offset(0.9f, 0.12f),
            Offset(0.5f, 0.03f),
            Offset(0.15f, 0.2f),
            Offset(0.82f, 0.25f),
            Offset(0.45f, 0.18f),
            Offset(0.6f, 0.35f),
            Offset(0.25f, 0.4f),
            Offset(0.88f, 0.5f),
            Offset(0.05f, 0.6f),
            Offset(0.72f, 0.65f),
            Offset(0.38f, 0.75f),
            Offset(0.95f, 0.85f),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3D1F9E).copy(0.15f), Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.3f),
                        radius = size.width * 0.7f
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0D47A1).copy(0.1f), Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.7f),
                        radius = size.width * 0.6f
                    )
                )

                stars.forEachIndexed { i, ratio ->
                    val alpha = if (i % 3 == 0) 0.8f else if (i % 2 == 0) 0.5f else 0.3f
                    val radius = if (i % 3 == 0) 2.5f else 1.5f
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = radius,
                        center = Offset(size.width * ratio.x, size.height * ratio.y)
                    )
                }
            }
    )
}

@Composable
private fun CosmicHeader(
    stardust: Int,
    unlockedCount: Int,
    totalCount: Int,
    onBalanceClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.cosmic_base_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            text = stringResource(R.string.cosmic_base_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8B8FA8)
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1A1033), Color(0xFF2D1B69), Color(0xFF1A1033))
                    )
                )
                .clickable { onBalanceClick() }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.cosmic_base_your_balance),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF8B8FA8)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 28.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stardust.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700),
                            fontSize = 36.sp
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Station Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF8B8FA8)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.cosmic_base_progress,
                            unlockedCount,
                            totalCount
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBBB3E0)
                    )
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f },
                        modifier = Modifier
                            .width(100.dp)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = Color(0xFF7C4DFF),
                        trackColor = Color(0xFF2A2D35),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun EarnHintCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1C22))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "💡  " + stringResource(R.string.cosmic_base_earn_hint),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF8B8FA8),
            lineHeight = 18.sp
        )
    }
}
