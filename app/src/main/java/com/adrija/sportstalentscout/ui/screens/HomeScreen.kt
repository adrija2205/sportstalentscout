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

    // Hardcoded past assessments
    val pastAssessments = mapOf(
        "Speed Test" to PastAssessment(
            testType = "Speed Test",
            date = "December 15, 2024",
            score = 85,
            result = "12.3",
            unit = "seconds",
            rank = 15,
            analysis = "Good speed performance! You're running faster than 70% of athletes in your age group.",
            recommendations = listOf(
                "Focus on explosive starts",
                "Improve running form",
                "Add interval training to your routine"
            )
        ),
        "Strength Test" to PastAssessment(
            testType = "Strength Test",
            date = "December 10, 2024",
            score = 92,
            result = "45",
            unit = "push-ups",
            rank = 8,
            analysis = "Excellent upper body strength! You're in the top 10% for your age category.",
            recommendations = listOf(
                "Maintain current routine",
                "Add progressive overload",
                "Include core strengthening exercises"
            )
        ),
        "Agility Test" to PastAssessment(
            testType = "Agility Test",
            date = "December 8, 2024",
            score = 78,
            result = "15.8",
            unit = "seconds",
            rank = 23,
            analysis = "Good agility performance with room for improvement in lateral movements.",
            recommendations = listOf(
                "Practice cone drills",
                "Work on change of direction speed",
                "Strengthen hip flexors and glutes"
            )
        ),
        "Endurance Test" to PastAssessment(
            testType = "Endurance Test",
            date = "December 5, 2024",
            score = 88,
            result = "18:45",
            unit = "minutes",
            rank = 12,
            analysis = "Strong cardiovascular endurance! Your stamina is well above average.",
            recommendations = listOf(
                "Increase training volume gradually",
                "Add tempo runs",
                "Include cross-training activities"
            )
        )
    )

    // Default assessment to show when Tests list is clicked (most recent one)
    val defaultAssessment = pastAssessments["Speed Test"]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        when {
            // Show specific assessment from test card click
            selectedAssessment != null -> {
                PastAssessmentScreen(
                    assessment = selectedAssessment!!,
                    onBack = {
                        selectedAssessment = null
                        showTestsList = false
                    }
                )
            }
            // Show default assessment from tests list button click
            showTestsList && defaultAssessment != null -> {
                PastAssessmentScreen(
                    assessment = defaultAssessment,
                    onBack = {
                        showTestsList = false
                        selectedAssessment = null
                    }
                )
            }
            // Show Home Screen
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header Section with proper top padding
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
                            color = Color(0xFFC21E56) // Red color
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "It's time to challenge your limits.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    // Main content area
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // General Athlete Test Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFCDDC39) // Lime green color
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp)
                                ) {
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

                        // Four Test Cards in 2x2 Grid
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TestCard(
                                        title = "Speed",
                                        iconRes = R.drawable.file_icons_powerbuilder,
                                        onClick = { onTestSelected("Speed Test") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    TestCard(
                                        title = "Strength",
                                        iconRes = R.drawable.iconoir_gym,
                                        onClick = { onTestSelected("Strength Test") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TestCard(
                                        title = "Agility",
                                        iconRes = R.drawable.vector,
                                        onClick = { onTestSelected("Agility Test") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    TestCard(
                                        title = "Endurance",
                                        iconRes = R.drawable.picon_jump,
                                        onClick = { onTestSelected("Endurance Test") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Bottom spacing for navigation
                        item {
                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                }

                // Bottom Navigation Bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFC21E56) // Red bottom nav
                    )
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

                        // Updated Tests list button - now shows past assessment
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
