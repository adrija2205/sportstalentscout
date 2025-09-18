package com.adrija.sportstalentscout.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.adrija.sportstalentscout.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecordingScreen(
    viewModel: MainViewModel,
    onRecordingComplete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedTest by viewModel.selectedTest.collectAsState()

    var isRecording by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }
    var recordingTime by remember { mutableStateOf(0) }
    var recording by remember { mutableStateOf<Recording?>(null) }


    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.VIDEO_CAPTURE)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown -= 1
        } else if (countdown == 0 && isRecording && recording == null) {
            recordingTime = 10
            recording = startVideoRecording(
                controller = controller,
                context = context,
                onVideoRecorded = { videoPath ->
                    viewModel.assessVideo(videoPath)
                    onRecordingComplete()
                },
                onError = {
                    isRecording = false
                    recordingTime = 0
                    recording = null
                }
            )
        }
    }


    LaunchedEffect(recordingTime) {
        if (recordingTime > 0 && isRecording) {
            delay(1000)
            recordingTime -= 1
        } else if (recordingTime == 0 && isRecording && recording != null) {

            recording?.stop()
            recording = null
            isRecording = false
        }
    }


    if (!permissionState.allPermissionsGranted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Camera and audio permissions are required",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { permissionState.launchMultiplePermissionRequest() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFCDDC39)
                    )
                ) {
                    Text(
                        "Grant Permissions",
                        color = Color.White
                    )
                }
            }
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = {
                    Text(
                        "Recording - $selectedTest",
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
                    containerColor = Color(0xFFC21E56),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )

            Box(modifier = Modifier.weight(1f)) {

                CameraPreview(
                    controller = controller,
                    modifier = Modifier.fillMaxSize()
                )

                if (countdown > 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = Color(0xFFCDDC39).copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = countdown.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    }
                }


                if (isRecording && countdown == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.9f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.FiberManualRecord,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recording: ${recordingTime}s",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shadowElevation = 12.dp,
            color = Color(0xFFC21E56)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isRecording) {
                    Button(
                        onClick = {
                            isRecording = true
                            countdown = 3
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCDDC39)
                        )
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Recording",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "Recording in progress...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@SuppressLint("MissingPermission")
private fun startVideoRecording(
    controller: LifecycleCameraController,
    context: android.content.Context,
    onVideoRecorded: (String) -> Unit,
    onError: () -> Unit
): Recording? {
    val outputFile = File(
        context.externalCacheDir,
        "sports_recording_${System.currentTimeMillis()}.mp4"
    )

    return controller.startRecording(
        FileOutputOptions.Builder(outputFile).build(),
        AudioConfig.create(true),
        ContextCompat.getMainExecutor(context)
    ) { event ->
        when (event) {
            is VideoRecordEvent.Finalize -> {
                if (event.hasError()) {
                    onError()
                } else {

                    onVideoRecorded(outputFile.absolutePath)
                }
            }
        }
    }
}

@Preview
@Composable
fun RecordingScreenPreview() {
    val viewModel: MainViewModel = viewModel()
    RecordingScreen(
        viewModel = viewModel,
        onRecordingComplete = { },
        onBack = { }
    )
}
