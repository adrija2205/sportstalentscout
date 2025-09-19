package com.adrija.sportstalentscout.data.models

data class PastAssessment(
    val testType: String,
    val date: String,
    val score: Int,
    val result: String,
    val unit: String,
    val rank: Int,
    val analysis: String,
    val recommendations: List<String>
)
