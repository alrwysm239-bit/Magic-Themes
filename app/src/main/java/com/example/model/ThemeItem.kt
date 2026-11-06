package com.example.model

data class ThemeItem(
    val id: String,
    val title: String,
    val creator: String,
    val price: String, // e.g. "Gratuit", "MAD 19,00", "MAD 30,00"
    val rating: Float = 5.0f,
    val reviewsCount: Int = 124,
    val downloadsCount: String = "48k",
    val sizeMb: String = "28.4 MB",
    val isAnimated: Boolean = false,
    val isPopular: Boolean = false,
    val isEditorsChoice: Boolean = false,
    val category: String = "Cyberpunk",
    val previewDrawableRes: Int? = null,
    val previewGradientColors: List<Long> = listOf(0xFF8C52FF, 0xFF00D2FF),
    val description: String = "Thème exclusif haute définition avec icônes 3D futuristes, widget horloge néon dynamique, et écran de verrouillage interactif pour EMUI et MagicOS.",
    val compatibleVersions: String = "MagicOS 7/8/9 & EMUI 12/13/14",
    val hwtFileName: String = "magic_theme_pack.hwt",
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false
)
