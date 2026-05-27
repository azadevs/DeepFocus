package com.azadevs.deepfocus.presentation.cosmic.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.CosmicModule

private val UnlockedGradient = listOf(
    Color(0xFF1A1033),
    Color(0xFF2D1B69),
)
private val LockedGradient = listOf(
    Color(0xFF1A1C22),
    Color(0xFF22252C),
)
private val GlowColor = Color(0xFF7C4DFF)

@Composable
fun CosmicModuleCard(
    module: CosmicModule,
    currentStardust: Int,
    onUnlockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canAfford = currentStardust >= module.stardustCost

    val scaleAnim = remember { Animatable(0.92f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            1f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
        )
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (module.isUnlocked) 0.35f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "glow_alpha"
    )

    val gradient = if (module.isUnlocked) UnlockedGradient else LockedGradient
    val cardAlpha = if (!module.isUnlocked && !canAfford) 0.65f else 1f

    Box(
        modifier = modifier
            .scale(scaleAnim.value)
            .alpha(cardAlpha)
    ) {
        // Outer glow shadow for unlocked modules
        if (module.isUnlocked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = GlowColor.copy(alpha = glowAlpha),
                        ambientColor = GlowColor.copy(alpha = glowAlpha * 0.5f)
                    )
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(
                width = 1.dp,
                brush = if (module.isUnlocked)
                    Brush.linearGradient(listOf(Color(0xFF7C4DFF), Color(0xFF40C4FF)))
                else
                    Brush.linearGradient(listOf(Color(0xFF3A3F4B), Color(0xFF2A2D35)))
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(gradient))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Emoji badge
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                if (module.isUnlocked)
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFF7C4DFF).copy(0.6f),
                                            Color(0xFF3D1F9E).copy(0.3f)
                                        )
                                    )
                                else
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFF2A2D35),
                                            Color(0xFF1E2028)
                                        )
                                    )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = module.emoji,
                            fontSize = 26.sp
                        )
                    }

                    // Name + description
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(module.nameResId),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (module.isUnlocked) Color.White else Color(0xFFB0B8C8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(module.descriptionResId),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (module.isUnlocked) Color(0xFFBBB3E0) else Color(0xFF6B7280),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                    }

                    // Action button
                    AnimatedContent(
                        targetState = module.isUnlocked,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                        },
                        label = "unlock_btn"
                    ) { isUnlocked ->
                        if (isUnlocked) {
                            // "Built" badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF00C853),
                                                Color(0xFF69F0AE)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.cosmic_base_unlocked),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0A2E1A)
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(
                                    onClick = onUnlockClick,
                                    enabled = canAfford,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF7C4DFF),
                                        disabledContainerColor = Color(0xFF2A2D35)
                                    ),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 12.dp, vertical = 6.dp
                                    )
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.cosmic_base_unlock,
                                            module.stardustCost
                                        ),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canAfford) Color.White else Color(0xFF6B7280)
                                    )
                                }
                                if (!canAfford) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "Need ${module.stardustCost - currentStardust} more",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF6B7280),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 0.dp, end = 0.dp)
                ) {
                }
            }
        }
    }
}
