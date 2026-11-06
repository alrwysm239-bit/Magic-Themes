package com.example.model

data class CustomizerState(
    val selectedWallpaperRes: Int? = null,
    val wallpaperGradient: List<Long> = listOf(0xFF0F0C20, 0xFF2D1B69),
    val wallpaperName: String = "Cyber Nebula",
    val selectedIconPackName: String = "Neon Cyber 3D",
    val iconPackGlowColor: Long = 0xFF8C52FF,
    val clockWidgetStyle: Int = 0, // 0: Neon Digital, 1: Minimalist, 2: Dual World, 3: Classic
    val accentColorLong: Long = 0xFF00D2FF,
    val fontStyleName: String = "Futuristic Bold",
    val previewMode: PreviewMode = PreviewMode.HOME_SCREEN
)

enum class PreviewMode {
    HOME_SCREEN,
    LOCK_SCREEN
}
