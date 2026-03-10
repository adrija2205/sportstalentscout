package com.adrija.sportstalentscout.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adrija.sportstalentscout.data.models.Badge
import com.adrija.sportstalentscout.data.models.LeaderboardEntry
import com.adrija.sportstalentscout.data.models.PastAssessment
import com.adrija.sportstalentscout.data.models.TestResult
import com.adrija.sportstalentscout.ml.ExerciseAnalyzer
import com.adrija.sportstalentscout.ml.VideoProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Replaces ViewModel : ViewModel() with AndroidViewModel so we can pass
 * context to VideoProcessor without leaking Activity context.
 *
 * CHANGE IN NavGraph / wherever MainViewModel is created:
 *   val viewModel: MainViewModel = viewModel()
 *   → stays the same, Compose handles AndroidViewModel automatically.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // ── VideoProcessor — uses app context (no leaks) ─────────────────────────
    private val videoProcessor = VideoProcessor(application.applicationContext)

    // ── State flows (unchanged from original) ────────────────────────────────
    private val _currentUser = MutableStateFlow("Max")
    val currentUser = _currentUser.asStateFlow()

    private val _testResults = MutableStateFlow<List<TestResult>>(emptyList())
    val testResults = _testResults.asStateFlow()

    private val _isAssessing = MutableStateFlow(false)
    val isAssessing = _isAssessing.asStateFlow()

    private val _selectedTest = MutableStateFlow("")
    val selectedTest = _selectedTest.asStateFlow()

    private val _currentScreen = MutableStateFlow("home")
    val currentScreen = _currentScreen.asStateFlow()

    private val _selectedAssessment = MutableStateFlow<PastAssessment?>(null)
    val selectedAssessment = _selectedAssessment.asStateFlow()

    private val _showTestsList = MutableStateFlow(false)
    val showTestsList = _showTestsList.asStateFlow()

    // ── Error state — shown in UI if analysis fails ───────────────────────────
    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError = _analysisError.asStateFlow()

    // ── Past assessments — updated to reflect new 4 exercises ────────────────
    private val _pastAssessments = MutableStateFlow(
        mapOf(
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
    )
    val pastAssessments = _pastAssessments.asStateFlow()

    private val _leaderboard = MutableStateFlow(
        listOf(
            LeaderboardEntry(1, "Alex Johnson", 95),
            LeaderboardEntry(2, "Sarah Chen", 92),
            LeaderboardEntry(3, "Mike Rodriguez", 89),
            LeaderboardEntry(4, "Emma Thompson", 87),
            LeaderboardEntry(5, "David Kim", 85),
            LeaderboardEntry(6, "Lisa Wang", 82),
            LeaderboardEntry(7, "Max", 78),
            LeaderboardEntry(8, "John Doe", 75),
            LeaderboardEntry(9, "Jane Smith", 72),
            LeaderboardEntry(10, "Tom Wilson", 69)
        )
    )
    val leaderboard = _leaderboard.asStateFlow()

    private val _badges = MutableStateFlow(
        listOf(
            Badge("first_test",    "First Test",     "Complete your first fitness test", true),
            Badge("push_up_pro",   "Push-up Pro",    "Complete 30 push-ups in one session", false, progress = 0.7f),
            Badge("squat_master",  "Squat Master",   "Complete 30 squats in one session", false, progress = 0.4f),
            Badge("curl_king",     "Curl King",      "Complete 20 bicep curls in one session", false, progress = 0.5f),
            Badge("consistency",   "Consistency",    "Test for 7 days straight", false, progress = 0.3f)
        )
    )
    val badges = _badges.asStateFlow()

    // ── Navigation functions (unchanged) ─────────────────────────────────────
    fun setSelectedTest(testType: String) { _selectedTest.value = testType }

    fun navigateToAssessment(assessment: PastAssessment) {
        _selectedAssessment.value = assessment
        _currentScreen.value = "assessment"
        _showTestsList.value = false
    }

    fun navigateToTestsList() {
        _showTestsList.value = true
        _selectedAssessment.value = null
        _currentScreen.value = "assessment"
        _pastAssessments.value["Push-ups"]?.let { _selectedAssessment.value = it }
    }

    fun navigateToHome() {
        _selectedAssessment.value = null
        _showTestsList.value = false
        _currentScreen.value = "home"
    }

    fun onBackPressed(): Boolean {
        return when {
            _selectedAssessment.value != null || _showTestsList.value -> {
                navigateToHome()
                true
            }
            else -> false
        }
    }

    fun clearError() { _analysisError.value = null }

    // ── REAL assessVideo — replaces the fake Random() implementation ──────────
    /**
     * Called from RecordingScreen after video is saved.
     * Runs VideoProcessor on the recorded file, extracts pose landmarks
     * frame-by-frame, counts reps using the ported Python logic, and
     * updates testResults with real data.
     */
    fun assessVideo(videoPath: String) {
        viewModelScope.launch {
            _isAssessing.value = true
            _analysisError.value = null

            try {
                val result = videoProcessor.analyzeVideo(
                    videoPath    = videoPath,
                    exerciseType = _selectedTest.value
                )

                // Persist as a PastAssessment so it shows in history
                val analysis = when {
                    result.score >= 85 -> "Outstanding performance! You're in the top tier for ${result.testType}."
                    result.score >= 70 -> "Great effort! Above average performance for ${result.testType}."
                    result.score >= 50 -> "Good work! Keep building consistency with ${result.testType}."
                    else               -> "Keep practicing ${result.testType} — every rep counts!"
                }

                val pastAssessment = PastAssessment(
                    testType        = result.testType,
                    date            = java.text.SimpleDateFormat(
                        "MMMM dd, yyyy", java.util.Locale.getDefault()
                    ).format(java.util.Date()),
                    score           = result.score,
                    result          = result.value,
                    unit            = result.unit,
                    rank            = calculateRank(result.score),
                    analysis        = analysis,
                    recommendations = ExerciseAnalyzer.getRecommendations(result.testType, result.score)
                )

                // Update past assessments map
                _pastAssessments.value = _pastAssessments.value.toMutableMap().apply {
                    put(result.testType, pastAssessment)
                }

                _testResults.value = _testResults.value + result

            } catch (e: Exception) {
                _analysisError.value = "Analysis failed: ${e.message}. Please retake the test."
            } finally {
                _isAssessing.value = false
            }
        }
    }

    // ── Submit results (stub — add your backend call here) ───────────────────
    fun submitTestResults() {
        viewModelScope.launch {
            // TODO: POST _testResults to your backend
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    fun getUserRank(): Int  = _leaderboard.value.find { it.name == _currentUser.value }?.rank ?: 0
    fun getUserScore(): Int = _leaderboard.value.find { it.name == _currentUser.value }?.score ?: 0
    fun getEarnedBadgesCount(): Int = _badges.value.count { it.isEarned }

    private fun calculateRank(score: Int): Int {
        // Simple rank: count how many leaderboard entries have higher score
        return (_leaderboard.value.count { it.score > score } + 1)
    }
}