package com.adrija.sportstalentscout.data.models

data class TestResult(
    val testType: String,
    val value: String,
    val unit: String,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)
