package com.adrija.sportstalentscout.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrija.sportstalentscout.data.models.Badge
import com.adrija.sportstalentscout.data.models.LeaderboardEntry
import com.adrija.sportstalentscout.data.models.TestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow("Max")
    val currentUser = _currentUser.asStateFlow()

    private val _testResults = MutableStateFlow<List<TestResult>>(emptyList())
    val testResults = _testResults.asStateFlow()

    private val _isAssessing = MutableStateFlow(false)
    val isAssessing = _isAssessing.asStateFlow()

    private val _selectedTest = MutableStateFlow("")
    val selectedTest = _selectedTest.asStateFlow()

    // Dummy leaderboard data
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

    // Dummy data
    private val _badges = MutableStateFlow(
        listOf(
            Badge("first_test", "First Test", "Complete your first fitness test", true),
            Badge("speed_demon", "Speed Demon", "Run 100m under 12 seconds", false, progress = 0.7f),
            Badge("jump_master", "Jump Master", "Jump over 50cm high", true),
            Badge("endurance_pro", "Endurance Pro", "Complete 5 tests in a row", false, progress = 0.4f),
            Badge("consistency", "Consistency", "Test for 7 days straight", false, progress = 0.3f),
        )
    )
    val badges = _badges.asStateFlow()

    fun setSelectedTest(testType: String) {
        _selectedTest.value = testType
    }

    /**
     * Assess video functionality - currently returns hardcoded results
     * TODO: Integrate ML model for actual video analysis
     */
    fun assessVideo(videoPath: String) {
        viewModelScope.launch {
            _isAssessing.value = true

            kotlinx.coroutines.delay(3000)

            val result = when (_selectedTest.value) {
                "Jump Test" -> TestResult(
                    testType = "Vertical Jump",
                    value = Random.nextInt(35, 65).toString(),
                    unit = "cm",
                    score = Random.nextInt(70, 95)
                )
                "Speed Test" -> TestResult(
                    testType = "100m Sprint",
                    value = String.format("%.2f", Random.nextDouble(10.5, 15.0)),
                    unit = "seconds",
                    score = Random.nextInt(65, 90)
                )
                "Strength Test" -> TestResult(
                    testType = "Push-ups",
                    value = Random.nextInt(15, 35).toString(),
                    unit = "reps",
                    score = Random.nextInt(60, 85)
                )
                "Agility Test" -> TestResult(
                    testType = "Cone Drill",
                    value = String.format("%.2f", Random.nextDouble(8.0, 12.0)),
                    unit = "seconds",
                    score = Random.nextInt(70, 95)
                )
                else -> TestResult(
                    testType = "Generic Test",
                    value = Random.nextInt(50, 100).toString(),
                    unit = "points",
                    score = Random.nextInt(60, 90)
                )
            }

            _testResults.value = _testResults.value + result
            _isAssessing.value = false

            /* 
             * ML INTEGRATION PLACEHOLDER:
             * 
             * Replace the above hardcoded logic with actual ML model inference:
             * 
             * 1. Load video from videoPath
             * 2. Extract frames/features from video
             * 3. Run ML model inference (TensorFlow Lite, MediaPipe, etc.)
             * 4. Parse model output to extract performance metrics
             * 5. Calculate score based on standardized benchmarks
             * 
             * Example implementation:
             * val mlResult = sportAnalysisModel.analyze(videoPath)
             * val actualResult = TestResult(
             *     testType = _selectedTest.value,
             *     value = mlResult.primaryMetric.toString(),
             *     unit = mlResult.unit,
             *     score = calculateScore(mlResult)
             * )
             */
        }
    }

    fun getUserRank(): Int {
        return _leaderboard.value.find { it.name == _currentUser.value }?.rank ?: 0
    }

    fun getUserScore(): Int {
        return _leaderboard.value.find { it.name == _currentUser.value }?.score ?: 0
    }

    fun getEarnedBadgesCount(): Int {
        return _badges.value.count { it.isEarned }
    }
}
