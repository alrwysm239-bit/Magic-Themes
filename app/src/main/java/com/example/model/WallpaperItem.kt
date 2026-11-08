package com.example.model

data class WallpaperItem(
    val id: String,
    val title: String,
    val category: String, // "Live Wallpapers", "Wallpapers", "Art/Design"
    val drawableRes: Int? = null,
    val imageUri: String? = null,
    val imageUrl: String? = null,
    val gradientColors: List<Long> = listOf(0xFF8C52FF, 0xFF00D2FF),
    val downloadsCount: String = "120k",
    val isLive: Boolean = false,
    val isFavorite: Boolean = false
)
