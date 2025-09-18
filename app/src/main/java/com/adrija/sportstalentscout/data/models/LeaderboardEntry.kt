package com.adrija.sportstalentscout.data.models

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
    val profileImage: String? = null
)
