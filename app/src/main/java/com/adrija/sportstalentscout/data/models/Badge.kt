package com.adrija.sportstalentscout.data.models

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val isEarned: Boolean = false,
    val iconResource: Int? = null,
    val progress: Float = 0f
)
