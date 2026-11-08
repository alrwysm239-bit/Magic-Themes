package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavTab
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CustomizerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IconsScreen
import com.example.ui.screens.MenuScreen
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.ui.screens.MyContentScreen
import com.example.ui.screens.ThemeDetailsScreen
import com.example.ui.screens.WallpapersScreen
import com.example.ui.theme.ArabicStrings
import com.example.ui.theme.EnglishStrings
import com.example.ui.theme.FrenchStrings
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalCurrentLanguage
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.DarkBackground

@Composable
fun ThemeStoreApp(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val currentSubScreen by viewModel.currentSubScreen.collectAsStateWithLifecycle()
    val themes by viewModel.themes.collectAsStateWithLifecycle()
    val wallpapers by viewModel.wallpapers.collectAsStateWithLifecycle()
    val iconPacks by viewModel.iconPacks.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val customizerState by viewModel.customizerState.collectAsStateWithLifecycle()
    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
    val selectedCategoryInitial by viewModel.selectedCategoryInitial.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val currentUserEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()

    val currentStrings = when (currentLanguage) {
        AppLanguage.ARABIC -> ArabicStrings
        AppLanguage.ENGLISH -> EnglishStrings
        AppLanguage.FRENCH -> FrenchStrings
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides currentLanguage.direction,
        LocalAppStrings provides currentStrings,
        LocalCurrentLanguage provides currentLanguage
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground),
            containerColor = DarkBackground,
            bottomBar = {
                if (currentSubScreen == AppSubScreen.NONE) {
                    BottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Main Content Switching
                    AnimatedContent(
                        targetState = currentSubScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screenTransition"
                    ) { subScreen ->
                    when (subScreen) {
                        AppSubScreen.THEME_DETAILS -> {
                            selectedTheme?.let { theme ->
                                ThemeDetailsScreen(
                                    theme = theme,
                                    themeEngineManager = viewModel.themeEngineManager,
                                    onBackClick = { viewModel.closeSubScreen() },
                                    onToggleFavorite = { viewModel.toggleFavoriteTheme(it) },
                                    onThemeDownloaded = { viewModel.markThemeDownloaded(it) }
                                )
                            }
                        }

                        AppSubScreen.CATEGORIES_DETAIL -> {
                            CategoriesScreen(
                                categories = categories,
                                themes = themes,
                                selectedCategoryInitial = selectedCategoryInitial,
                                onThemeClick = { viewModel.openThemeDetails(it) },
                                onBackClick = { viewModel.closeSubScreen() }
                            )
                        }

                        AppSubScreen.MY_CONTENT -> {
                            MyContentScreen(
                                themes = themes,
                                themeEngineManager = viewModel.themeEngineManager,
                                onBackClick = { viewModel.closeSubScreen() },
                                onThemeClick = { viewModel.openThemeDetails(it) }
                            )
                        }

                        AppSubScreen.NONE -> {
                            // Primary Tab views
                            AnimatedContent(
                                targetState = selectedTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tabTransition"
                            ) { tab ->
                                when (tab) {
                                    NavTab.THEMES -> {
                                        HomeScreen(
                                            themes = themes,
                                            categories = categories,
                                            onThemeClick = { viewModel.openThemeDetails(it) },
                                            onCategoryClick = { viewModel.openCategories(it) },
                                            onQuickBubbleClick = { bubbleId ->
                                                when (bubbleId) {
                                                    "my_content" -> viewModel.openMyContent()
                                                    "noir" -> viewModel.openCategories("Noir")
                                                    "video" -> viewModel.selectTab(NavTab.WALLPAPERS)
                                                    "best" -> viewModel.openCategories("Cyberpunk")
                                                }
                                            }
                                        )
                                    }

                                    NavTab.WALLPAPERS -> {
                                        WallpapersScreen(
                                            wallpapers = wallpapers,
                                            onToggleFavorite = { viewModel.toggleFavoriteWallpaper(it) }
                                        )
                                    }

                                    NavTab.ICONS -> {
                                        IconsScreen(
                                            iconPacks = iconPacks
                                        )
                                    }

                                    NavTab.CUSTOMIZER -> {
                                        CustomizerScreen(
                                            customizerState = customizerState,
                                            themeEngineManager = viewModel.themeEngineManager,
                                            onWallpaperSelected = { res, name ->
                                                viewModel.updateCustomizerWallpaper(res, name)
                                            },
                                            onAccentColorSelected = { color ->
                                                viewModel.updateCustomizerAccentColor(color)
                                            },
                                            onClockStyleSelected = { style ->
                                                viewModel.updateCustomizerClockStyle(style)
                                            },
                                            onTogglePreviewMode = {
                                                viewModel.toggleCustomizerPreviewMode()
                                            }
                                        )
                                    }

                                    NavTab.MENU -> {
                                        MenuScreen(
                                            themeEngineManager = viewModel.themeEngineManager,
                                            onNavigateToMyContent = { viewModel.openMyContent() },
                                            currentUserEmail = currentUserEmail,
                                            isAdmin = isAdmin,
                                            onLogin = { viewModel.loginUser(it) },
                                            onLogout = { viewModel.logoutUser() },
                                            themes = themes,
                                            wallpapers = wallpapers,
                                            iconPacks = iconPacks,
                                            onAddTheme = { viewModel.addTheme(it) },
                                            onDeleteTheme = { viewModel.deleteTheme(it) },
                                            onAddWallpaper = { viewModel.addWallpaper(it) },
                                            onDeleteWallpaper = { viewModel.deleteWallpaper(it) },
                                            onAddIconPack = { viewModel.addIconPack(it) },
                                            onDeleteIconPack = { viewModel.deleteIconPack(it) },
                                            onLanguageChanged = { viewModel.setLanguage(it) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
    }
}
