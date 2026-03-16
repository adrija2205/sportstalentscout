package com.adrija.sportstalentscout.ml

import com.adrija.sportstalentscout.data.models.VideoAnalysisResult
import com.adrija.sportstalentscout.data.models.AngleSample
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.adrija.sportstalentscout.data.models.TestResult
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Processes a recorded video file frame by frame.
 * Uses MediaPipe Pose Landmarker (Android SDK) to extract landmarks,
 * then runs the ported rep-counting logic from ExerciseTrainer.py.
 *
 * Required in build.gradle:
 *   implementation 'com.google.mediapipe:tasks-vision:0.10.14'
 *
 * Required in assets/:
 *   pose_landmarker_lite.task   (download from MediaPipe Model Hub)
 */
class VideoProcessor(private val context: Context) {

    companion object {
        // How many frames per second to sample from video.
        // Python code runs at webcam FPS (~30fps). We sample at 15fps for speed.
        private const val SAMPLE_FPS = 15

        // Model asset file name — place in app/src/main/assets/
        private const val POSE_MODEL_ASSET = "pose_landmarker_lite.task"
    }

    /**
     * Main entry point. Call from ViewModel via coroutine.
     *
     * @param videoPath  Absolute path to the recorded .mp4 file
     * @param exerciseType  One of: "Push-ups", "Squats", "Bicep Curls", "Shoulder Press"
     * @return TestResult with real rep count and computed score
     */
    suspend fun analyzeVideo(videoPath: String, exerciseType: String): VideoAnalysisResult =
    withContext(Dispatchers.Default) {

        val landmarker = buildPoseLandmarker()
        val frames = extractFrames(videoPath)

        var state = RepCountState()

        val angleSamples = mutableListOf<AngleSample>()

        for (frame in frames) {

            val landmarks = detectLandmarks(landmarker, frame)

            if (landmarks.isNotEmpty()) {

                val angle = when (exerciseType) {

                    "Push-ups" -> {
                        val s = landmarks[PoseLandmark.LEFT_SHOULDER]
                        val e = landmarks[PoseLandmark.LEFT_ELBOW]
                        val w = landmarks[PoseLandmark.LEFT_WRIST]
                        if (s != null && e != null && w != null)
                            ExerciseAnalyzer.findAngle(s, e, w)
                        else null
                    }

                    "Squats" -> {
                        val h = landmarks[PoseLandmark.RIGHT_HIP]
                        val k = landmarks[PoseLandmark.RIGHT_KNEE]
                        val a = landmarks[PoseLandmark.RIGHT_ANKLE]
                        if (h != null && k != null && a != null)
                            ExerciseAnalyzer.findAngle(h, k, a)
                        else null
                    }

                    "Bicep Curls" -> {
                        val s = landmarks[PoseLandmark.RIGHT_SHOULDER]
                        val e = landmarks[PoseLandmark.RIGHT_ELBOW]
                        val w = landmarks[PoseLandmark.RIGHT_WRIST]
                        if (s != null && e != null && w != null)
                            ExerciseAnalyzer.findAngle(s, e, w)
                        else null
                    }

                    "Shoulder Press" -> {
                        val s = landmarks[PoseLandmark.RIGHT_SHOULDER]
                        val e = landmarks[PoseLandmark.RIGHT_ELBOW]
                        val w = landmarks[PoseLandmark.RIGHT_WRIST]
                        if (s != null && e != null && w != null)
                            ExerciseAnalyzer.findAngle(s, e, w)
                        else null
                    }

                    else -> null
                }

                angle?.let {
                    angleSamples.add(
                        AngleSample(
                            time = System.currentTimeMillis(),
                            angle = it
                        )
                    )
                }

                state = countRep(exerciseType, landmarks, state)
            }

            frame.recycle()
        }

        landmarker.close()

        val repCount = state.counter
        val score = ExerciseAnalyzer.calculateScore(exerciseType, repCount)

        val result = TestResult(
            testType = exerciseType,
            value = repCount.toString(),
            unit = "reps",
            score = score
        )

        VideoAnalysisResult(
            testResult = result,
            angleSamples = angleSamples
        )
    }

    // ── Build MediaPipe PoseLandmarker in VIDEO mode ──────────────────────────
    private fun buildPoseLandmarker(): PoseLandmarker {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(POSE_MODEL_ASSET)
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.IMAGE)   // IMAGE mode: process one frame at a time
            .setNumPoses(1)
            .setMinPoseDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .build()

        return PoseLandmarker.createFromOptions(context, options)
    }

    // ── Extract frames at SAMPLE_FPS using MediaMetadataRetriever ────────────
    private fun extractFrames(videoPath: String): List<Bitmap> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)

        val durationMs = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull() ?: 0L

        val frames    = mutableListOf<Bitmap>()
        val intervalUs = (1_000_000L / SAMPLE_FPS)  // microseconds between samples
        var timeUs     = 0L

        while (timeUs <= durationMs * 1000L) {
            val bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
            if (bitmap != null) frames.add(bitmap)
            timeUs += intervalUs
        }

        retriever.release()
        return frames
    }

    // ── Run MediaPipe on a single Bitmap frame ────────────────────────────────
    private fun detectLandmarks(landmarker: PoseLandmarker, bitmap: Bitmap): List<LandmarkPoint?> {
        val mpImage = BitmapImageBuilder(bitmap).build()
        val result  = landmarker.detect(mpImage)

        if (result.landmarks().isEmpty()) return emptyList()

        // MediaPipe returns normalized [0,1] coordinates — same as Python
        val rawLandmarks = result.landmarks()[0]  // first (only) person

        // Build a 33-element list indexed by landmark ID so our
        // PoseLandmark.* constants map correctly (same as Python landmark_list[idx])
        val landmarkList = MutableList<LandmarkPoint?>(33) { null }
        rawLandmarks.forEachIndexed { idx, lm ->
            if (idx < 33) {
                landmarkList[idx] = LandmarkPoint(lm.x(), lm.y())
            }
        }
        return landmarkList
    }

    // ── Dispatch to correct exercise counter ──────────────────────────────────
    private fun countRep(
        exerciseType: String,
        landmarks: List<LandmarkPoint?>,
        state: RepCountState
    ): RepCountState {
        return when (exerciseType) {
            "Push-ups"       -> ExerciseAnalyzer.countPushUp(landmarks, state)
            "Squats"         -> ExerciseAnalyzer.countSquat(landmarks, state)
            "Bicep Curls"    -> ExerciseAnalyzer.countBicepCurl(landmarks, state)
            "Shoulder Press" -> ExerciseAnalyzer.countShoulderPress(landmarks, state)
            else             -> state
        }
    }
}