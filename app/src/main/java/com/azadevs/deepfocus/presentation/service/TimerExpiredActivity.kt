package com.azadevs.deepfocus.presentation.service

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azadevs.deepfocus.R
import com.azadevs.deepfocus.domain.model.PomodoroPhase
import com.azadevs.deepfocus.domain.pomodoro.PomodoroController
import com.azadevs.deepfocus.presentation.MainActivity
import com.azadevs.deepfocus.presentation.util.theme.DeepFocusTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Azamat Kalmurzaev
 * 20/05/2026
 * Full Screen Intent Activity triggered when the Pomodoro Timer finishes.
 */
@AndroidEntryPoint
class TimerExpiredActivity : ComponentActivity() {

    @Inject
    lateinit var controller: PomodoroController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        turnScreenOnAndShowOnLockScreen()

        setContent {
            DeepFocusTheme {
                val state by controller.state.collectAsState()

                // Intercept back key to dismiss the alarm properly
                BackHandler {
                    dismissAlarm()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val phase = state.phase
                    // We completed the focus phase if the next phase is SHORT_BREAK or LONG_BREAK
                    val isFocusFinished =
                        phase == PomodoroPhase.SHORT_BREAK || phase == PomodoroPhase.LONG_BREAK

                    val phaseColor = when (phase) {
                        PomodoroPhase.FOCUS -> MaterialTheme.colorScheme.primary
                        PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                        PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.background,
                                        phaseColor.copy(alpha = 0.15f),
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val scrollState = rememberScrollState()
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            // Pulsing Ring Animation
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 0.9f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.8f,
                                targetValue = 0.1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "alpha"
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(200.dp)
                            ) {
                                // Dynamic pulsing background rings
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .scale(scale)
                                        .clip(CircleShape)
                                        .background(phaseColor.copy(alpha = alpha))
                                        .border(2.dp, phaseColor.copy(alpha = alpha), CircleShape)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .scale(scale * 0.85f)
                                        .clip(CircleShape)
                                        .background(phaseColor.copy(alpha = alpha * 1.2f))
                                )

                                // Foreground Ring & Icon
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(phaseColor)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = "Alarm Icon",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(36.dp))

                            Text(
                                text = if (isFocusFinished) "Focus Finished!" else "Break is Over!",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (isFocusFinished) {
                                    "Outstanding job! Time to recharge."
                                } else {
                                    "Ready to dive back into deep focus?"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(64.dp))

                            Card(
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        RoundedCornerShape(28.dp)
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Start next session button
                                    Button(
                                        onClick = {
                                            startNextPhase()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = phaseColor,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = if (isFocusFinished) "Start Break" else "Start Focusing",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }

                                    // Turn off/Dismiss button
                                    OutlinedButton(
                                        onClick = {
                                            dismissAlarm()
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.dismiss_alarm),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun dismissAlarm() {
        controller.stopAlarm()
        finish()
    }

    private fun startNextPhase() {
        controller.stopAlarm()
        controller.start()
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(mainIntent)
        finish()
    }

    private fun turnScreenOnAndShowOnLockScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }
    }
}
