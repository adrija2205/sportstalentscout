package com.adrija.sportstalentscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adrija.sportstalentscout.R
import com.adrija.sportstalentscout.ui.components.TestCard
import com.adrija.sportstalentscout.data.models.PastAssessment
import com.adrija.sportstalentscout.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onTestSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedAssessment by remember { mutableStateOf<PastAssessment?>(null) }
    var showTestsList by remember { mutableStateOf(false) }

    val pastAssessments = mapOf(
        "Push-ups" to PastAssessment(
            testType = "Push-ups",
            date = "December 15, 2024",
            score = 85,
            result = "32",
            unit = "reps",
            rank = 15,
            analysis = "Great upper body strength! You're above 70% of athletes in your age group.",
            recommendations = listOf(
                "Add progressive overload",
                "Focus on full range of motion",
                "Include core strengthening exercises"
            )
        ),
        "Squats" to PastAssessment(
            testType = "Squats",
            date = "December 12, 2024",
            score = 78,
            result = "28",
            unit = "reps",
            rank = 22,
            analysis = "Good lower body endurance with room to improve depth.",
            recommendations = listOf(
                "Focus on squat depth — thighs parallel to floor",
                "Strengthen hip flexors",
                "Add goblet squats for form practice"
            )
        ),
        "Bicep Curls" to PastAssessment(
            testType = "Bicep Curls",
            date = "December 10, 2024",
            score = 92,
            result = "22",
            unit = "reps",
            rank = 8,
            analysis = "Excellent bicep strength! You're in the top 10% for your age category.",
            recommendations = listOf(
                "Maintain current routine",
                "Try hammer curls for variation",
                "Increase weight gradually"
            )
        ),
        "Shoulder Press" to PastAssessment(
            testType = "Shoulder Press",
            date = "December 8, 2024",
            score = 80,
            result = "18",
            unit = "reps",
            rank = 18,
            analysis = "Good shoulder strength. Consistent training will push you to the next level.",
            recommendations = listOf(
                "Include lateral raises for shoulder width",
                "Strengthen rotator cuff muscles",
                "Practice overhead mobility drills"
            )
        )
    )

    val defaultAssessment = pastAssessments["Push-ups"]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        when {
            selectedAssessment != null -> {
                PastAssessmentScreen(
                    assessment = selectedAssessment!!,
                    onBack = {
                        selectedAssessment = null
                        showTestsList = false
                    }
                )
            }
            showTestsList && defaultAssessment != null -> {
                PastAssessmentScreen(
                    assessment = defaultAssessment,
                    onBack = {
                        showTestsList = false
                        selectedAssessment = null
                    }
                )
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {

                    // ── Header ────────────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                    ) {
                        Text(
                            text = "Hi, $currentUser",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color(0xFFC21E56)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "It's time to challenge your limits.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        // ── Banner card ───────────────────────────────────────
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFCDDC39)
                                )
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = "General Athlete Test",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "45 Mins",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 14.sp,
                                        color = Color.Black.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        // ── 4 Test Cards in 2×2 grid ──────────────────────────
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                                // Row 1: Push-ups | Squats
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TestCard(
                                        title    = "Push-ups",
                                        iconRes  = R.drawable.iconoir_gym,
                                        onClick  = { onTestSelected("Push-ups") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    TestCard(
                                        title    = "Squats",
                                        iconRes  = R.drawable.vector,
                                        onClick  = { onTestSelected("Squats") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Row 2: Bicep Curls | Shoulder Press
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TestCard(
                                        title    = "Bicep Curls",
                                        iconRes  = R.drawable.file_icons_powerbuilder,
                                        onClick  = { onTestSelected("Bicep Curls") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    TestCard(
                                        title    = "Shoulder Press",
                                        iconRes  = R.drawable.picon_jump,
                                        onClick  = { onTestSelected("Shoulder Press") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Bottom spacing for nav bar
                        item { Spacer(modifier = Modifier.height(120.dp)) }
                    }
                }

                // ── Bottom Navigation Bar (unchanged) ─────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC21E56))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { /* Already on home */ }) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = {
                            showTestsList = true
                            selectedAssessment = null
                        }) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "Tests",
                                tint = if (showTestsList) Color.White else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = onProfileClick) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val viewModel: MainViewModel = viewModel()
    HomeScreen(
        viewModel = viewModel,
        onTestSelected = { },
        onProfileClick = { }
    )
}