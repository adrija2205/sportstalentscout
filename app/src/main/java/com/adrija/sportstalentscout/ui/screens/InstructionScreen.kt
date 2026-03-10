package com.adrija.sportstalentscout.ui.screens

import android.media.MediaPlayer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adrija.sportstalentscout.R
import com.adrija.sportstalentscout.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionScreen(
    viewModel: MainViewModel,
    onStartTest: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTest by viewModel.selectedTest.collectAsState()
    val context = LocalContext.current

    var isPlaying       by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration        by remember { mutableStateOf(0) }
    var mediaPlayer     by remember { mutableStateOf<MediaPlayer?>(null) }

    // ── Instructions for each exercise ───────────────────────────────────────
    val instructions = when (selectedTest) {
        "Push-ups" -> listOf(
            "Place your phone/camera so it can see your full body from the side",
            "Start in a high plank — hands shoulder-width apart, body straight",
            "Lower your chest until your elbows reach ~90°",
            "Push back up to full arm extension to complete one rep",
            "Keep your core tight and hips level throughout",
            "Perform as many clean reps as possible in 60 seconds"
        )
        "Squats" -> listOf(
            "Place your phone/camera to capture your full body from the side",
            "Stand with feet shoulder-width apart, toes slightly turned out",
            "Keep your chest tall and core engaged",
            "Lower down until your thighs are parallel to the floor",
            "Drive through your heels to stand back up — that's one rep",
            "Keep your knees tracking over your toes throughout"
        )
        "Bicep Curls" -> listOf(
            "Stand facing the camera so both arms are fully visible",
            "Hold weights (or resistance bands) with arms fully extended at your sides",
            "Keep your elbows pinned close to your torso",
            "Curl both arms up toward your shoulders simultaneously",
            "Lower slowly back to full extension — that completes one rep",
            "Avoid swinging your back or shoulders to lift"
        )
        "Shoulder Press" -> listOf(
            "Stand or sit facing the camera so your full upper body is visible",
            "Hold weights at shoulder height, elbows at ~90° and out to the sides",
            "Press both arms straight up until fully extended overhead",
            "Lower back down to shoulder height with control — that's one rep",
            "Keep your core braced and avoid arching your lower back",
            "Perform as many clean reps as possible in 60 seconds"
        )
        else -> listOf("Follow the on-screen instructions carefully")
    }

    // ── Audio resource mapping (kept exactly as original) ────────────────────
    val audioResource = when (selectedTest) {
        "Push-ups"       -> R.raw.pushups
        "Squats"         -> R.raw.speed
        "Bicep Curls"    -> R.raw.agility
        "Shoulder Press" -> R.raw.endurance
        else             -> R.raw.flexibility
    }

    LaunchedEffect(selectedTest) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, audioResource)
            duration = mediaPlayer?.duration ?: 0
        } catch (e: Exception) {
            duration = 30000
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying && mediaPlayer != null) {
            currentPosition = mediaPlayer?.currentPosition ?: 0
            if (currentPosition >= duration) {
                isPlaying = false
                currentPosition = 0
                mediaPlayer?.seekTo(0)
            }
            delay(1000)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = { Text("Test Instructions", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC21E56),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Test title card ───────────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = selectedTest,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Follow these instructions for best results:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // ── Step-by-step instructions card ────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            instructions.forEachIndexed { index, instruction ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFC21E56),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = instruction,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Voice instructions card (unchanged) ───────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFCDDC39).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = Color(0xFFCDDC39),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Voice Instructions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (mediaPlayer != null) {
                                            if (isPlaying) {
                                                mediaPlayer?.pause()
                                                isPlaying = false
                                            } else {
                                                mediaPlayer?.start()
                                                isPlaying = true
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color(0xFFCDDC39), RoundedCornerShape(28.dp))
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    LinearProgressIndicator(
                                        progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFCDDC39),
                                        trackColor = Color.White.copy(alpha = 0.3f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = formatTime(currentPosition),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = formatTime(duration),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Listen to detailed voice instructions for $selectedTest",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // ── Safety tips card ──────────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Safety Tips",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Warm up for at least 5 minutes before starting\n" +
                                        "• Ensure you have enough clear space around you\n" +
                                        "• Stop immediately if you feel pain or discomfort\n" +
                                        "• Have water nearby and stay hydrated",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // ── Start Test button (unchanged) ─────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shadowElevation = 8.dp,
            color = Color(0xFFC21E56)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFC21E56))
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onStartTest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCDDC39))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Test",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

fun formatTime(milliseconds: Int): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}

@Preview
@Composable
fun InstructionScreenPreview() {
    val viewModel: MainViewModel = viewModel()
    viewModel.setSelectedTest("Push-ups")
    InstructionScreen(
        viewModel = viewModel,
        onStartTest = { },
        onBack = { }
    )
}