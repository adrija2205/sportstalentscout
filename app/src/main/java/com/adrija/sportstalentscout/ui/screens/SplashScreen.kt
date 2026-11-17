package com.adrija.sportstalentscout.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adrija.sportstalentscout.ui.theme.SportOrange
import com.adrija.sportstalentscout.ui.theme.SportPurple
import com.adrija.sportstalentscout.ui.theme.SportsTalentScoutTheme

/**
 * Clean, athletic splash / loading screen shown while KRIDA initializes and resolves
 * persistent user preferences, preventing any UI flicker between onboarding and home.
 */
@Composable
fun KridaSplashScreen(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashPulsing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "PulseScale"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Loading KRIDA Sports Talent Scout"
            },
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // KRIDA Glowing Brand Emblem
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(88.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(SportOrange, SportPurple),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "K",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 50.sp,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "KRIDA",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = "SPORTS TALENT SCOUT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                )

                Spacer(modifier = Modifier.height(40.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = SportOrange,
                    strokeWidth = 3.dp,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Democratising Sports Talent in India",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun KridaSplashScreenLightPreview() {
    SportsTalentScoutTheme(darkTheme = false) {
        KridaSplashScreen()
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
private fun KridaSplashScreenDarkPreview() {
    SportsTalentScoutTheme(darkTheme = true) {
        KridaSplashScreen()
    }
}
