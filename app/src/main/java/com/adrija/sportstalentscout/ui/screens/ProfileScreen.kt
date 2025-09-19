package com.adrija.sportstalentscout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adrija.sportstalentscout.ui.components.BadgeItem
import com.adrija.sportstalentscout.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val testResults by viewModel.testResults.collectAsState()
    val badges by viewModel.badges.collectAsState()

    val userWeight = 75f // kg
    val userHeight = 165f // cm
    val bmi = calculateBMI(userWeight, userHeight)
    val bmiStatus = getBMIStatus(bmi)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "My Profile",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile header card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFC21E56)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Surface(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape),
                                        color = Color.White.copy(alpha = 0.2f)
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(50.dp),
                                            tint = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = currentUser,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Text(
                                        text = "max@example.com",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )

                                    Text(
                                        text = "Birthday: June 28 Th",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Surface(
                                    modifier = Modifier.size(100.dp, 150.dp),
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatCard(
                                    value = "${userWeight.toInt()} Kg",
                                    label = "Weight"
                                )
                                StatCard(
                                    value = "21",
                                    label = "Years Old"
                                )
                                StatCard(
                                    value = "${userHeight.toInt()} CM",
                                    label = "Height"
                                )
                            }
                        }
                    }
                }

                // BMI Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFCDDC39)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Body Mass Index (BMI)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = String.format("%.1f", bmi),
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Surface(
                                    color = Color(0xFFC21E56),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = bmiStatus,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            BMIScale(currentBMI = bmi)
                        }
                    }
                }

                // Achievements/Badges Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Achievements",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (badges.isNotEmpty()) {
                                badges.forEach { badge ->
                                    BadgeItem(
                                        badge = badge,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            } else {

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    BadgePlaceholder(
                                        title = "First Test",
                                        description = "Complete your first fitness test",
                                        isEarned = testResults.isNotEmpty()
                                    )
                                    BadgePlaceholder(
                                        title = "Speed Demon",
                                        description = "Complete a speed test",
                                        isEarned = testResults.any { it.testType.contains("Speed") }
                                    )
                                    BadgePlaceholder(
                                        title = "Strong Performer",
                                        description = "Complete a strength test",
                                        isEarned = testResults.any { it.testType.contains("Strength") }
                                    )
                                    BadgePlaceholder(
                                        title = "Agility Master",
                                        description = "Complete an agility test",
                                        isEarned = testResults.any { it.testType.contains("Agility") }
                                    )
                                    BadgePlaceholder(
                                        title = "Endurance Champion",
                                        description = "Complete an endurance test",
                                        isEarned = testResults.any { it.testType.contains("Endurance") }
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

@Composable
fun BadgePlaceholder(
    title: String,
    description: String,
    isEarned: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) Color(0xFFCDDC39).copy(alpha = 0.2f)
            else Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = if (isEarned) Color(0xFFCDDC39) else Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isEarned) Color.Black else Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isEarned) Color(0xFFCDDC39) else Color.White
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            if (isEarned) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Earned",
                    tint = Color(0xFFCDDC39),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String
) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun BMIScale(currentBMI: Float) {
    val bmiRanges = listOf(15f, 18.5f, 25f, 30f, 40f)
    val bmiLabels = listOf("15", "18.5", "25", "30", "40")
    val bmiColors = listOf(
        Color(0xFF4285F4), // Blue - Underweight
        Color(0xFF34A853), // Green - Normal
        Color(0xFFFBBC04), // Yellow - Overweight
        Color(0xFFEA4335)  // Red - Obese
    )
    Column {
        // Color bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        ) {
            bmiColors.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bmiLabels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    fontSize = 10.sp
                )
            }
        }
    }
}

fun calculateBMI(weightKg: Float, heightCm: Float): Float {
    val heightM = heightCm / 100
    return weightKg / (heightM * heightM)
}

fun getBMIStatus(bmi: Float): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "You're Healthy"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    val viewModel: MainViewModel = viewModel()
    ProfileScreen(
        viewModel = viewModel,
        onBack = { }
    )
}
