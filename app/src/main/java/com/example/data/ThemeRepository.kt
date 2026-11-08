package com.example.data

import com.example.R
import com.example.model.CategoryItem
import com.example.model.CustomizerState
import com.example.model.IconPackItem
import com.example.model.ThemeItem
import com.example.model.WallpaperItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ThemeRepository {

    private val _themes = MutableStateFlow<List<ThemeItem>>(emptyList())
    val themes: StateFlow<List<ThemeItem>> = _themes.asStateFlow()

    private val _wallpapers = MutableStateFlow<List<WallpaperItem>>(emptyList())
    val wallpapers: StateFlow<List<WallpaperItem>> = _wallpapers.asStateFlow()

    private val _iconPacks = MutableStateFlow<List<IconPackItem>>(emptyList())
    val iconPacks: StateFlow<List<IconPackItem>> = _iconPacks.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories.asStateFlow()

    private val _customizerState = MutableStateFlow(
        CustomizerState(
            selectedWallpaperRes = R.drawable.wallpaper_cyberpunk,
            wallpaperName = "Cyberpunk City",
            selectedIconPackName = "Neon Cyber 3D",
            iconPackGlowColor = 0xFF8C52FF,
            clockWidgetStyle = 0,
            accentColorLong = 0xFF00D2FF,
            fontStyleName = "Futuristic Bold"
        )
    )
    val customizerState: StateFlow<CustomizerState> = _customizerState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _themes.value = listOf(
            ThemeItem(
                id = "theme_phin_fall",
                title = "[PHIN] Fall Sunset",
                creator = "Jokkat by Riode",
                price = "Gratuit",
                rating = 5.0f,
                reviewsCount = 25,
                downloadsCount = "71k",
                sizeMb = "28.4 MB",
                isAnimated = true,
                isPopular = true,
                isEditorsChoice = true,
                category = "Cyberpunk",
                previewDrawableRes = R.drawable.wallpaper_cyberpunk,
                previewGradientColors = listOf(0xFF8C52FF, 0xFF00D2FF),
                description = "Thème exclusif aux teintes chaudes automnales et accents néon dorés. Inclus écran de verrouillage immersif avec widget horloge interactif et pack de 2,400 icônes MagicOS & EMUI.",
                compatibleVersions = "MagicOS 7.0 / 8.0+ & EMUI 12 / 13 / 14",
                hwtFileName = "phin_fall_sunset.hwt"
            ),
            ThemeItem(
                id = "theme_sw_arc",
                title = "[SW] ARC Galaxy",
                creator = "StarWave Studio",
                price = "MAD 19,00",
                rating = 4.9f,
                reviewsCount = 84,
                downloadsCount = "45k",
                sizeMb = "34.2 MB",
                isAnimated = true,
                isPopular = true,
                category = "Noir",
                previewDrawableRes = R.drawable.wallpaper_galaxy,
                previewGradientColors = listOf(0xFF1E1B4B, 0xFF7C3AED),
                description = "Inspiré par les profondeurs nébuleuses de l'espace. Arcs lumineux néon violets et bleus réagissant au déverrouillage.",
                compatibleVersions = "MagicOS 7 / 8 / 9 & EMUI 12 / 13 / 14",
                hwtFileName = "sw_arc_galaxy.hwt"
            ),
            ThemeItem(
                id = "theme_cogul_alice",
                title = "[CoguL] Alice Cyber",
                creator = "CoguL Design",
                price = "MAD 30,00",
                rating = 5.0f,
                reviewsCount = 42,
                downloadsCount = "38k",
                sizeMb = "41.0 MB",
                isAnimated = true,
                isPopular = true,
                category = "Anime",
                previewDrawableRes = R.drawable.wallpaper_anime,
                previewGradientColors = listOf(0xFF4C1D95, 0xFFEC4899),
                description = "Conte fantastique réimaginé en cyber-art féerique. Pack d'icônes 3D pastel et widgets horloge translucides.",
                compatibleVersions = "MagicOS 7.1+ & EMUI 12 / 13",
                hwtFileName = "cogul_alice_cyber.hwt"
            ),
            ThemeItem(
                id = "theme_viking_neon",
                title = "Viking Neon Rune",
                creator = "Nordic Tech",
                price = "MAD 25,00",
                rating = 4.8f,
                reviewsCount = 19,
                downloadsCount = "22k",
                sizeMb = "22.5 MB",
                isPopular = true,
                category = "Gaming",
                previewDrawableRes = R.drawable.wallpaper_cyberpunk,
                previewGradientColors = listOf(0xFF0F172A, 0xFF00D2FF),
                description = "Runes mystiques gravées dans du titane sombre et illuminées de néon cyan polaire.",
                compatibleVersions = "MagicOS 7 / 8 & EMUI 12",
                hwtFileName = "viking_neon.hwt"
            ),
            ThemeItem(
                id = "theme_amoled_noir",
                title = "Noir Pure AMOLED",
                creator = "Zero Dark",
                price = "Gratuit",
                rating = 5.0f,
                reviewsCount = 310,
                downloadsCount = "150k",
                sizeMb = "14.8 MB",
                isEditorsChoice = true,
                category = "Noir",
                previewDrawableRes = R.drawable.wallpaper_galaxy,
                previewGradientColors = listOf(0xFF000000, 0xFF18181B),
                description = "Économisez la batterie de votre écran OLED avec des noirs parfaits à 100% et de discrets reflets néon violets.",
                compatibleVersions = "MagicOS 7/8/9 & EMUI 11/12/13/14",
                hwtFileName = "noir_pure_amoled.hwt",
                isDownloaded = true
            ),
            ThemeItem(
                id = "theme_honor_magic_falcon",
                title = "[Honor] MagicOS 8 Falcon",
                creator = "Honor Studio Pro",
                price = "Gratuit",
                rating = 5.0f,
                reviewsCount = 420,
                downloadsCount = "92k",
                sizeMb = "31.2 MB",
                isAnimated = true,
                isPopular = true,
                isEditorsChoice = true,
                category = "Cyberpunk",
                previewDrawableRes = R.drawable.wallpaper_cyberpunk,
                previewGradientColors = listOf(0xFF00D2FF, 0xFF8C52FF),
                description = "ثيم رسمي مخصص لهواتف هونر MagicOS مع شاشة قفل تفاعلية ثلاثية الأبعاد وحزمة أيقونات فخمة بصيغة .hnt الأصلية.",
                compatibleVersions = "Honor MagicOS 7.0 / 8.0 / 9.0",
                hwtFileName = "honor_magic_falcon.hnt",
                format = "hnt"
            ),
            ThemeItem(
                id = "theme_honor_cyber_matrix",
                title = "[Honor] Cyber Matrix Pro",
                creator = "Honor Lab",
                price = "MAD 20,00",
                rating = 4.9f,
                reviewsCount = 98,
                downloadsCount = "54k",
                sizeMb = "26.8 MB",
                isAnimated = true,
                isPopular = true,
                category = "Gaming",
                previewDrawableRes = R.drawable.wallpaper_anime,
                previewGradientColors = listOf(0xFF059669, 0xFF00D2FF),
                description = "تصميم مصفوفة رقمية خضراء وسيبرانية مخصصة لأجهزة Honor Magic 5/6/7 Pro بصيغة .hnt الأصلية.",
                compatibleVersions = "Honor MagicOS 8.0 / 8.1+",
                hwtFileName = "honor_cyber_matrix.hnt",
                format = "hnt"
            )
        )

        _wallpapers.value = listOf(
            WallpaperItem(
                id = "wall_cyber_city",
                title = "Cyber Metropolis",
                category = "Live Wallpapers",
                drawableRes = R.drawable.wallpaper_cyberpunk,
                downloadsCount = "89k",
                isLive = true
            ),
            WallpaperItem(
                id = "wall_deep_galaxy",
                title = "Cosmic Planet Orbit",
                category = "Live Wallpapers",
                drawableRes = R.drawable.wallpaper_galaxy,
                downloadsCount = "140k",
                isLive = true
            ),
            WallpaperItem(
                id = "wall_anime_neon",
                title = "Neon Samurai Girl",
                category = "Art/Design",
                drawableRes = R.drawable.wallpaper_anime,
                downloadsCount = "95k",
                isLive = false
            ),
            WallpaperItem(
                id = "wall_nebula_purple",
                title = "Deep Space Nebula",
                category = "Wallpapers",
                drawableRes = R.drawable.wallpaper_galaxy,
                downloadsCount = "62k",
                isLive = false
            ),
            WallpaperItem(
                id = "wall_cyber_rain",
                title = "Rainy Cyber Alleys",
                category = "Wallpapers",
                drawableRes = R.drawable.wallpaper_cyberpunk,
                downloadsCount = "51k",
                isLive = false
            ),
            WallpaperItem(
                id = "wall_anime_dreams",
                title = "Cyberpunk Dreams",
                category = "Art/Design",
                drawableRes = R.drawable.wallpaper_anime,
                downloadsCount = "73k",
                isLive = false
            )
        )

        _iconPacks.value = listOf(
            IconPackItem(
                id = "pack_curt",
                title = "Curtanly Curt...",
                creator = "Curt Lab",
                rating = 5.0f,
                reviewsCount = 112,
                iconCount = "1,450+ icônes",
                styleTag = "Clay 3D",
                sampleIcons = listOf("face", "star", "chat", "folder"),
                glowColorLong = 0xFFFFB800
            ),
            IconPackItem(
                id = "pack_cyber_neon",
                title = "Neon Glow Icons",
                creator = "CyberArts",
                rating = 5.0f,
                reviewsCount = 420,
                iconCount = "2,800+ icônes",
                styleTag = "Neon 3D",
                sampleIcons = listOf("phone", "message", "camera", "music"),
                glowColorLong = 0xFF8C52FF
            ),
            IconPackItem(
                id = "pack_pastel_cute",
                title = "Pastel Soft 3D",
                creator = "Mochi Studio",
                rating = 4.9f,
                reviewsCount = 78,
                iconCount = "920+ icônes",
                styleTag = "Soft Pastel",
                sampleIcons = listOf("heart", "image", "cloud", "settings"),
                glowColorLong = 0xFFFF2A85
            ),
            IconPackItem(
                id = "pack_minimal_dark",
                title = "Obsidian Line Minimal",
                creator = "Hexa Design",
                rating = 5.0f,
                reviewsCount = 230,
                iconCount = "3,100+ icônes",
                styleTag = "Minimalist",
                sampleIcons = listOf("home", "search", "shield", "lock"),
                glowColorLong = 0xFF00D2FF
            )
        )

        _categories.value = listOf(
            CategoryItem(
                id = "cat_cyberpunk",
                title = "Cyberpunk",
                subtitle = "Néons holographiques & futurisme",
                drawableRes = R.drawable.wallpaper_cyberpunk,
                gradientColors = listOf(0xFF2E0854, 0xFF00D2FF),
                themeCount = "86 thèmes"
            ),
            CategoryItem(
                id = "cat_anime",
                title = "Anime",
                subtitle = "Styles manga & illustrations néon",
                drawableRes = R.drawable.wallpaper_anime,
                gradientColors = listOf(0xFF4A0E4E, 0xFFFF2A85),
                themeCount = "120 thèmes"
            ),
            CategoryItem(
                id = "cat_minimalist",
                title = "Minimalist",
                subtitle = "Élégance 3D & géométrie pure",
                drawableRes = R.drawable.wallpaper_galaxy,
                gradientColors = listOf(0xFF1E293B, 0xFF64748B),
                themeCount = "64 thèmes"
            ),
            CategoryItem(
                id = "cat_gaming",
                title = "Gaming",
                subtitle = "Contrôleurs RVB & univers e-sport",
                drawableRes = R.drawable.wallpaper_cyberpunk,
                gradientColors = listOf(0xFF064E3B, 0xFF10B981),
                themeCount = "95 thèmes"
            )
        )
    }

    fun addTheme(theme: ThemeItem) {
        _themes.update { listOf(theme) + it }
    }

    fun deleteTheme(themeId: String) {
        _themes.update { list -> list.filter { it.id != themeId } }
    }

    fun addWallpaper(wallpaper: WallpaperItem) {
        _wallpapers.update { listOf(wallpaper) + it }
    }

    fun deleteWallpaper(wallpaperId: String) {
        _wallpapers.update { list -> list.filter { it.id != wallpaperId } }
    }

    fun addIconPack(iconPack: IconPackItem) {
        _iconPacks.update { listOf(iconPack) + it }
    }

    fun deleteIconPack(iconPackId: String) {
        _iconPacks.update { list -> list.filter { it.id != iconPackId } }
    }

    fun toggleFavoriteTheme(themeId: String) {
        _themes.update { list ->
            list.map { if (it.id == themeId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun toggleFavoriteWallpaper(wallpaperId: String) {
        _wallpapers.update { list ->
            list.map { if (it.id == wallpaperId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun markThemeDownloaded(themeId: String) {
        _themes.update { list ->
            list.map { if (it.id == themeId) it.copy(isDownloaded = true) else it }
        }
    }

    fun updateCustomizerWallpaper(drawableRes: Int?, name: String) {
        _customizerState.update {
            it.copy(selectedWallpaperRes = drawableRes, wallpaperName = name)
        }
    }

    fun updateCustomizerIconPack(name: String, glowColor: Long) {
        _customizerState.update {
            it.copy(selectedIconPackName = name, iconPackGlowColor = glowColor)
        }
    }

    fun updateCustomizerAccentColor(colorLong: Long) {
        _customizerState.update {
            it.copy(accentColorLong = colorLong)
        }
    }

    fun updateCustomizerClockStyle(style: Int) {
        _customizerState.update {
            it.copy(clockWidgetStyle = style)
        }
    }

    fun togglePreviewMode() {
        _customizerState.update {
            val next = if (it.previewMode == com.example.model.PreviewMode.HOME_SCREEN) {
                com.example.model.PreviewMode.LOCK_SCREEN
            } else {
                com.example.model.PreviewMode.HOME_SCREEN
            }
            it.copy(previewMode = next)
        }
    }
}
