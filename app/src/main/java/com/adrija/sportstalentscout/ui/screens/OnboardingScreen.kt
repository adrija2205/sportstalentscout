package com.adrija.sportstalentscout.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adrija.sportstalentscout.ui.theme.AccentGold
import com.adrija.sportstalentscout.ui.theme.SportBlue
import com.adrija.sportstalentscout.ui.theme.SportGreen
import com.adrija.sportstalentscout.ui.theme.SportOrange
import com.adrija.sportstalentscout.ui.theme.SportPurple
import com.adrija.sportstalentscout.ui.theme.SportsTalentScoutTheme
import kotlinx.coroutines.launch

// =========================================================================
// DATA MODELS & SPECIFICATION
// =========================================================================

/**
 * Types of onboarding animations prepared for future Lottie integration.
 *
 * @property assetName The future JSON asset filename (without .json extension).
 */
enum class OnboardingAnimationType(val assetName: String) {
    /**
     * Screen 1: Child athlete, rural/grassroots sports opportunities, ambition and hope.
     */
    TALENT_OPPORTUNITY("talent_opportunity"),

    /**
     * Screen 2: Pose estimation landmark tracking, joint angles, rep counting, AI vision.
     */
    AI_ASSESSMENT("ai_assessment"),

    /**
     * Screen 3: Performance insights, scout cards, growth trajectory, athletic excellence.
     */
    SPORTING_POTENTIAL("sporting_potential");

    /**
     * Helper to obtain relative asset path for future Lottie composition:
     * e.g., "animations/talent_opportunity.json"
     */
    @Suppress("unused")
    val assetPath: String get() = "animations/$assetName.json"
}

/**
 * Encapsulates the content and styling for each onboarding slide.
 */
data class OnboardingPageData(
    val id: Int,
    val categoryTag: String,
    val headline: String,
    val description: String,
    val animationType: OnboardingAnimationType,
    val accentColor: Color,
    val icon: ImageVector,
)

/**
 * Provides the three data-driven onboarding screens for KRIDA.
 */
fun getKridaOnboardingPages(): List<OnboardingPageData> = listOf(
    OnboardingPageData(
        id = 0,
        categoryTag = "OPPORTUNITY FOR ALL",
        headline = "Talent Has No Boundaries.",
        description = "Great sporting talent exists everywhere. KRIDA helps children from every corner of India showcase their abilities, regardless of their location or resources.",
        animationType = OnboardingAnimationType.TALENT_OPPORTUNITY,
        accentColor = SportOrange,
        icon = Icons.AutoMirrored.Filled.DirectionsRun,
    ),
    OnboardingPageData(
        id = 1,
        categoryTag = "AI VISION ASSESSMENT",
        headline = "Your Movement. Our Intelligence.",
        description = "Using AI and computer vision, KRIDA tracks your movements, counts repetitions, analyses exercise techniques, and evaluates your performance.",
        animationType = OnboardingAnimationType.AI_ASSESSMENT,
        accentColor = SportBlue,
        icon = Icons.Default.FitnessCenter,
    ),
    OnboardingPageData(
        id = 2,
        categoryTag = "SCOUT & ACADEMY CONNECT",
        headline = "Your Journey to Greatness Starts Here.",
        description = "Get meaningful performance insights and share your assessment reports with sports academies and talent identification organisations. Your next opportunity could be closer than you think.",
        animationType = OnboardingAnimationType.SPORTING_POTENTIAL,
        accentColor = SportGreen,
        icon = Icons.Default.EmojiEvents,
    ),
)

// =========================================================================
// MAIN ONBOARDING SCREEN
// =========================================================================

/**
 * KRIDA 3-Screen Onboarding experience.
 *
 * @param onOnboardingComplete Callback invoked when the user finishes or skips onboarding.
 * @param modifier Screen modifier.
 * @param pages List of onboarding pages (defaults to KRIDA 3-screen suite).
 */
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    pages: List<OnboardingPageData> = getKridaOnboardingPages(),
) {
    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == (pages.size - 1)
    val currentAccentColor = pages[pagerState.currentPage].accentColor

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Top App Bar / Header: KRIDA brand identifier & Skip Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // KRIDA Brand Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(SportOrange, SportPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "K",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Column {
                        Text(
                            text = "KRIDA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "SPORTS TALENT SCOUT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                // Skip Button (Visible on screens 0 & 1, hidden on final screen)
                AnimatedVisibility(
                    visible = !isLastPage,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TextButton(
                        onClick = onOnboardingComplete,
                        modifier = Modifier
                            .semantics {
                                role = Role.Button
                                contentDescription = "Skip onboarding and enter KRIDA"
                            }
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Swipeable Horizontal Pager for the 3 Onboarding Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                OnboardingPage(
                    pageData = pages[pageIndex],
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom Navigation Area: Dynamic Indicators + Contextual Navigation Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OnboardingNavigationButtons(
                    pagerState = pagerState,
                    pagesCount = pages.size,
                    currentAccentColor = currentAccentColor,
                    isLastPage = isLastPage,
                    onNextClick = {
                        if (pagerState.currentPage < (pages.size - 1)) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    onGetStartedClick = onOnboardingComplete,
                )
            }
        }
    }
}

// =========================================================================
// REUSABLE PAGE COMPONENT
// =========================================================================

/**
 * Modular Onboarding page composable with flexible layout that adapts gracefully
 * across varied screen dimensions.
 */
@Composable
fun OnboardingPage(
    pageData: OnboardingPageData,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Dedicated Visual / Lottie Animation Container
        OnboardingAnimationPlaceholder(
            animationType = pageData.animationType,
            accentColor = pageData.accentColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Content Area: Category Tag, Headline & Description
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Category Badge
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = pageData.accentColor.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    pageData.accentColor.copy(alpha = 0.35f)
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = pageData.icon,
                        contentDescription = null,
                        tint = pageData.accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = pageData.categoryTag,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = pageData.accentColor,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            // Headline
            Text(
                text = pageData.headline,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body Description
            Text(
                text = pageData.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 23.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// =========================================================================
// LOTTIE ANIMATION CONTAINER & PLACEHOLDER
// =========================================================================

/**
 * Dedicated, reusable animation container for KRIDA onboarding.
 *
 * =========================================================================
 * LOTTIE ANIMATION INTEGRATION GUIDE (Phase 2):
 * =========================================================================
 * When ready to integrate Lottie animations:
 *
 * 1. Add dependency to app/build.gradle.kts:
 *    implementation("com.airbnb.android:lottie-compose:6.4.0")
 *
 * 2. Place your Lottie JSON files in the assets directory:
 *    app/src/main/assets/animations/
 *      ├── talent_opportunity.json   (For Screen 1: Grassroots child athlete)
 *      ├── ai_assessment.json        (For Screen 2: Pose estimation & joints)
 *      └── sporting_potential.json   (For Screen 3: Performance score & badge)
 *
 * 3. Inside this composable, replace the placeholder illustration below with:
 *    val composition by rememberLottieComposition(
 *        LottieCompositionSpec.Asset(animationType.assetPath)
 *    )
 *    val progress by animateLottieCompositionAsState(
 *        composition = composition,
 *        iterations = LottieConstants.IterateForever
 *    )
 *    LottieAnimation(
 *        composition = composition,
 *        progress = { progress },
 *        modifier = Modifier.fillMaxSize()
 *    )
 * =========================================================================
 *
 * @param animationType The specific animation slot to display.
 * @param modifier Container modifier specifying dimensions and alignment.
 * @param accentColor Primary accent tint matching the slide's theme.
 */
@Composable
fun OnboardingAnimationPlaceholder(
    animationType: OnboardingAnimationType,
    modifier: Modifier = Modifier,
    accentColor: Color = SportBlue
) {
    Card(
        modifier = modifier
            .semantics {
                contentDescription = when (animationType) {
                    OnboardingAnimationType.TALENT_OPPORTUNITY ->
                        "Graphic representing a young Indian athlete running with determination"
                    OnboardingAnimationType.AI_ASSESSMENT ->
                        "Graphic representing computer vision body pose landmarks and repetition tracking"
                    OnboardingAnimationType.SPORTING_POTENTIAL ->
                        "Graphic representing sports performance assessment and talent scouting opportunities"
                }
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            accentColor.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (animationType) {
                OnboardingAnimationType.TALENT_OPPORTUNITY -> {
                    GrassrootsTalentIllustration(accentColor = accentColor)
                }
                OnboardingAnimationType.AI_ASSESSMENT -> {
                    AiPoseTrackerIllustration(accentColor = accentColor)
                }
                OnboardingAnimationType.SPORTING_POTENTIAL -> {
                    SportingPotentialIllustration(accentColor = accentColor)
                }
            }

            // Badge indicating placeholder status & future Lottie drop-in slot
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    accentColor.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "KRIDA MOTION",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }
    }
}

// =========================================================================
// STYLIZED COMPOSITE ARTWORKS (VISUAL CENTERPIECES)
// =========================================================================

/**
 * Screen 1 Illustration: Grassroots child athlete running with passion,
 * surrounded by an energetic sunrise arc and motion trails.
 */
@Composable
private fun GrassrootsTalentIllustration(accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "TalentPulsing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension * 0.34f

            // Background ambient radiant aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.28f),
                        SportPurple.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.5f * pulseScale
                ),
                radius = baseRadius * 1.5f * pulseScale,
                center = center
            )

            // Dynamic running track curves (India's grassroots ground to Olympic track)
            val trackPath = Path().apply {
                moveTo(size.width * 0.1f, size.height * 0.78f)
                cubicTo(
                    size.width * 0.35f, size.height * 0.72f,
                    size.width * 0.65f, size.height * 0.88f,
                    size.width * 0.9f, size.height * 0.75f
                )
            }
            drawPath(
                path = trackPath,
                color = accentColor.copy(alpha = 0.5f),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            val trackPath2 = Path().apply {
                moveTo(size.width * 0.15f, size.height * 0.84f)
                cubicTo(
                    size.width * 0.4f, size.height * 0.80f,
                    size.width * 0.7f, size.height * 0.94f,
                    size.width * 0.95f, size.height * 0.82f
                )
            }
            drawPath(
                path = trackPath2,
                color = SportOrange.copy(alpha = 0.3f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Motion dash lines
            drawLine(
                color = accentColor.copy(alpha = 0.6f),
                start = Offset(center.x - 70.dp.toPx(), center.y - 10.dp.toPx()),
                end = Offset(center.x - 40.dp.toPx(), center.y - 10.dp.toPx()),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = accentColor.copy(alpha = 0.4f),
                start = Offset(center.x - 85.dp.toPx(), center.y + 15.dp.toPx()),
                end = Offset(center.x - 50.dp.toPx(), center.y + 15.dp.toPx()),
                strokeWidth = 2.5f.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Center Athlete Emblem with floating ambition tags
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(SportOrange, AccentGold, SportPurple, SportOrange)
                        )
                    )
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grassroots Tag
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = "Grassroots India • Bharat Talent",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

/**
 * Screen 2 Illustration: AI Pose Estimation with landmark joints,
 * tracking scan-lines, and live repetition counting telemetry.
 */
@Composable
private fun AiPoseTrackerIllustration(accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "AiScanTransition")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanLineProgress"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Scanning AI Grid
            val scanY = size.height * scanProgress
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        accentColor.copy(alpha = 0.8f),
                        accentColor,
                        accentColor.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                ),
                start = Offset(size.width * 0.15f, scanY),
                end = Offset(size.width * 0.85f, scanY),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Pose Landmarks (Head, Shoulders, Elbows, Wrists, Hips, Knees, Ankles)
            val head = Offset(center.x, center.y - 70.dp.toPx())
            val leftShoulder = Offset(center.x - 38.dp.toPx(), center.y - 42.dp.toPx())
            val rightShoulder = Offset(center.x + 38.dp.toPx(), center.y - 42.dp.toPx())
            val leftElbow = Offset(center.x - 55.dp.toPx(), center.y - 12.dp.toPx())
            val rightElbow = Offset(center.x + 55.dp.toPx(), center.y - 12.dp.toPx())
            val leftWrist = Offset(center.x - 62.dp.toPx(), center.y + 18.dp.toPx())
            val rightWrist = Offset(center.x + 62.dp.toPx(), center.y + 18.dp.toPx())

            val leftHip = Offset(center.x - 24.dp.toPx(), center.y + 15.dp.toPx())
            val rightHip = Offset(center.x + 24.dp.toPx(), center.y + 15.dp.toPx())
            val leftKnee = Offset(center.x - 30.dp.toPx(), center.y + 55.dp.toPx())
            val rightKnee = Offset(center.x + 30.dp.toPx(), center.y + 55.dp.toPx())
            val leftAnkle = Offset(center.x - 35.dp.toPx(), center.y + 92.dp.toPx())
            val rightAnkle = Offset(center.x + 35.dp.toPx(), center.y + 92.dp.toPx())

            val joints = listOf(
                head, leftShoulder, rightShoulder, leftElbow, rightElbow,
                leftWrist, rightWrist, leftHip, rightHip, leftKnee,
                rightKnee, leftAnkle, rightAnkle
            )

            // Skeletal connecting lines
            val bones = listOf(
                head to leftShoulder, head to rightShoulder,
                leftShoulder to rightShoulder,
                leftShoulder to leftElbow, leftElbow to leftWrist,
                rightShoulder to rightElbow, rightElbow to rightWrist,
                leftShoulder to leftHip, rightShoulder to rightHip,
                leftHip to rightHip,
                leftHip to leftKnee, leftKnee to leftAnkle,
                rightHip to rightKnee, rightKnee to rightAnkle
            )

            val boneStroke = Stroke(width = 2.5f.dp.toPx(), cap = StrokeCap.Round)
            bones.forEach { (start, end) ->
                drawLine(
                    color = accentColor.copy(alpha = 0.65f),
                    start = start,
                    end = end,
                    strokeWidth = boneStroke.width,
                    cap = boneStroke.cap
                )
            }

            // Joint node points with glow
            joints.forEach { joint ->
                drawCircle(
                    color = accentColor.copy(alpha = 0.35f),
                    radius = 7.dp.toPx(),
                    center = joint
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.5f.dp.toPx(),
                    center = joint
                )
            }

            // Target Reticle Corner Brackets
            val bracketSize = 18.dp.toPx()
            val pad = 24.dp.toPx()
            val right = size.width - pad
            val bottom = size.height - pad

            // Top-left bracket
            drawLine(accentColor, Offset(pad, pad), Offset(pad + bracketSize, pad), 2.dp.toPx())
            drawLine(accentColor, Offset(pad, pad), Offset(pad, pad + bracketSize), 2.dp.toPx())
            // Top-right bracket
            drawLine(accentColor, Offset(right, pad), Offset(right - bracketSize, pad), 2.dp.toPx())
            drawLine(accentColor, Offset(right, pad), Offset(right, pad + bracketSize), 2.dp.toPx())
            // Bottom-left bracket
            drawLine(accentColor, Offset(pad, bottom), Offset(pad + bracketSize, bottom), 2.dp.toPx())
            drawLine(accentColor, Offset(pad, bottom), Offset(pad, bottom - bracketSize), 2.dp.toPx())
            // Bottom-right bracket
            drawLine(accentColor, Offset(right, bottom), Offset(right - bracketSize, bottom), 2.dp.toPx())
            drawLine(accentColor, Offset(right, bottom), Offset(right, bottom - bracketSize), 2.dp.toPx())
        }

        // Live AI Telemetry HUD Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Reps: 18",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SportGreen.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SportGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Posture: 98.4%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SportGreen
                    )
                }
            }
        }
    }
}

/**
 * Screen 3 Illustration: Athletic Growth, Scout Assessment Card,
 * Trophy of Excellence, and Academy Discovery.
 */
@Composable
private fun SportingPotentialIllustration(accentColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Ascending Talent Growth Trajectory
            val growthPath = Path().apply {
                moveTo(size.width * 0.15f, size.height * 0.82f)
                cubicTo(
                    size.width * 0.40f, size.height * 0.80f,
                    size.width * 0.55f, size.height * 0.45f,
                    size.width * 0.85f, size.height * 0.22f
                )
            }

            drawPath(
                path = growthPath,
                brush = Brush.linearGradient(
                    listOf(SportBlue, accentColor, AccentGold)
                ),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Growth milestone dots
            val p1 = Offset(size.width * 0.22f, size.height * 0.81f)
            val p2 = Offset(size.width * 0.50f, size.height * 0.60f)
            val p3 = Offset(size.width * 0.85f, size.height * 0.22f)

            listOf(p1, p2, p3).forEach { point ->
                drawCircle(color = accentColor.copy(alpha = 0.3f), radius = 8.dp.toPx(), center = point)
                drawCircle(color = AccentGold, radius = 4.dp.toPx(), center = point)
            }
        }

        // Assessment Card & Trophy Stack
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(AccentGold, SportOrange, SportGreen)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mock Assessment Card preview
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "KRIDA Talent Passport",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "National Academy Benchmark: Top 5%",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = SportGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// =========================================================================
// REUSABLE INDICATORS & NAVIGATION BUTTONS
// =========================================================================

/**
 * Three smooth horizontal page indicators with active expansion and fluid color morphing.
 */
@Composable
fun OnboardingIndicators(
    pagerState: PagerState,
    pagesCount: Int,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.semantics {
            contentDescription = "Onboarding page ${pagerState.currentPage + 1} of $pagesCount"
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagesCount) { index ->
            val isSelected = pagerState.currentPage == index
            val targetWidth = if (isSelected) 28.dp else 8.dp
            val animatedWidth by animateDpAsState(
                targetValue = targetWidth,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "IndicatorWidth"
            )
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.22f),
                animationSpec = tween(durationMillis = 300),
                label = "IndicatorColor"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(animatedWidth)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
        }
    }
}

/**
 * Bottom navigation controls providing smooth navigation between onboarding slides.
 * On slides 0 & 1: Shows pagination indicators and a prominent Next button.
 * On slide 2: Replaces Next with a full-width, high-emphasis "Get Started" CTA.
 */
@Composable
fun OnboardingNavigationButtons(
    pagerState: PagerState,
    pagesCount: Int,
    currentAccentColor: Color,
    isLastPage: Boolean,
    onNextClick: () -> Unit,
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLastPage) {
        // Prominent Full-Width "Get Started" Button on the final slide
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OnboardingIndicators(
                pagerState = pagerState,
                pagesCount = pagesCount,
                activeColor = currentAccentColor
            )

            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Get Started with KRIDA"
                    },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentAccentColor,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    } else {
        // Slide 0 & 1: Indicators on the left, "Next" button on the right
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnboardingIndicators(
                pagerState = pagerState,
                pagesCount = pagesCount,
                activeColor = currentAccentColor
            )

            Button(
                onClick = onNextClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentAccentColor,
                    contentColor = Color.White
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
                modifier = Modifier
                    .height(48.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Next onboarding screen"
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
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

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun OnboardingScreenLightPreview() {
    SportsTalentScoutTheme(darkTheme = false) {
        OnboardingScreen(onOnboardingComplete = {})
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
private fun OnboardingScreenDarkPreview() {
    SportsTalentScoutTheme(darkTheme = true) {
        OnboardingScreen(onOnboardingComplete = {})
    }
}
