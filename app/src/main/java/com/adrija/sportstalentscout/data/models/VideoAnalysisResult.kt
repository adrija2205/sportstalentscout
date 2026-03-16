package com.adrija.sportstalentscout.data.models

data class VideoAnalysisResult(
    val testResult: TestResult,
    val angleSamples: List<AngleSample>
)