package com.adrija.sportstalentscout.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adrija.sportstalentscout.ui.components.PerformanceChart
import com.adrija.sportstalentscout.ui.components.ExerciseWaveformChart
import com.adrija.sportstalentscout.viewmodel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MainViewModel,
    onBackToHome: () -> Unit,
    onRetakeTest: () -> Unit,
    modifier: Modifier = Modifier
) {

    val isAssessing   by viewModel.isAssessing.collectAsState()
    val testResults   by viewModel.testResults.collectAsState()
    val selectedTest  by viewModel.selectedTest.collectAsState()
    val analysisError by viewModel.analysisError.collectAsState()
    val angleSamples  by viewModel.angleSamples.collectAsState()

    val latestResult = testResults.lastOrNull()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue  = 1.2f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = { Text("Test Results", color = Color.White) },
                actions = {
                    if (!isAssessing && latestResult != null) {
                        IconButton(onClick = onBackToHome) {
                            Icon(Icons.Default.Home, null, tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC21E56)
                )
            )

            when {

                // ───── ANALYSING STATE ─────
                isAssessing -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            Icons.Default.Analytics,
                            null,
                            modifier = Modifier.size(120.dp).scale(scale),
                            tint = Color(0xFFC21E56)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Counting Your Reps...",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Analysing $selectedTest with AI pose detection",
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(0.6f),
                            color = Color(0xFFC21E56)
                        )
                    }
                }

                // ───── ERROR STATE ─────
                analysisError != null -> {

                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            Icons.Default.ErrorOutline,
                            null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFFC21E56)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Analysis Failed",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            analysisError ?: "",
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                viewModel.clearError()
                                onRetakeTest()
                            }
                        ) {
                            Text("Retake Test")
                        }
                    }
                }

                // ───── RESULT STATE ─────
                latestResult != null -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // RESULT CARD
                        item {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFC21E56)
                                )
                            ) {

                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Icon(
                                        Icons.Default.EmojiEvents,
                                        null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text("Your Result", color = Color.White)

                                    Text(
                                        "${latestResult.value} ${latestResult.unit}",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Text(
                                        latestResult.testType,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        // SCORE + RANK
                        item {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f)
                                    )
                                ) {

                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        Icon(
                                            Icons.Default.Star,
                                            null,
                                            tint = Color(0xFFCDDC39)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text("Score", color = Color.White)

                                        Text(
                                            "${latestResult.score}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFCDDC39)
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.1f)
                                    )
                                ) {

                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        Icon(
                                            Icons.AutoMirrored.Filled.TrendingUp,
                                            null,
                                            tint = Color(0xFFC21E56)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text("Rank", color = Color.White)

                                        Text(
                                            "#${viewModel.getUserRank()}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC21E56)
                                        )
                                    }
                                }
                            }
                        }

                        // PERFORMANCE TREND
                        item {

                            PerformanceChart(
                                testResults = testResults.takeLast(5)
                            )
                        }

                        // EXERCISE MOTION GRAPH
                        item {

                            ExerciseWaveformChart(
                                samples = angleSamples
                            )
                        }

                        // PERFORMANCE ANALYSIS
                        item {

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                )
                            ) {

                                Column(modifier = Modifier.padding(16.dp)) {

                                    Text(
                                        "Performance Analysis",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    val analysis = when {
                                        latestResult.score >= 85 -> "Outstanding performance!"
                                        latestResult.score >= 70 -> "Great job!"
                                        latestResult.score >= 50 -> "Good work!"
                                        else -> "Keep practicing."
                                    }

                                    Text(
                                        analysis,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        // SUBMIT BUTTON
                        item {

                            Button(
                                onClick = { viewModel.submitTestResults() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFCDDC39)
                                )
                            ) {

                                Icon(Icons.Default.CloudUpload, null, tint = Color.Black)

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    "Submit Results",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // BOTTOM BAR
        if (!isAssessing && latestResult != null && analysisError == null) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shadowElevation = 12.dp,
                color = Color(0xFFC21E56)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = onRetakeTest,
                        modifier = Modifier.weight(1f)
                    ) {

                        Icon(Icons.Default.Refresh, null)

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Retake")
                    }

                    Button(
                        onClick = onBackToHome,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCDDC39)
                        )
                    ) {

                        Icon(Icons.Default.Home, null)

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Home", color = Color.Black)
                    }
                }
            }
        }
    }
}