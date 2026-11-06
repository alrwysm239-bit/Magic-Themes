package com.example.model

data class IconPackItem(
    val id: String,
    val title: String,
    val creator: String,
    val rating: Float = 5.0f,
    val reviewsCount: Int = 89,
    val iconCount: String = "1,850+ icônes",
    val styleTag: String = "Neon 3D",
    val sampleIcons: List<String> = listOf("phone", "message", "camera", "browser"),
    val glowColorLong: Long = 0xFF8C52FF,
    val isDownloaded: Boolean = false
)
