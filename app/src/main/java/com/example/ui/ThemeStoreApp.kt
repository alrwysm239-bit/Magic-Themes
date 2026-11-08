package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.MemberUploadDialog
import com.example.ui.components.NavTab
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CustomizerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IconsScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.MyContentScreen
import com.example.ui.screens.ThemeDetailsScreen
import com.example.ui.screens.WallpapersScreen
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.ArabicStrings
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.EnglishStrings
import com.example.ui.theme.FrenchStrings
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalCurrentLanguage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

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
    var showMemberUploadDialog by remember { mutableStateOf(false) }

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
            floatingActionButton = {
                // Strictly visible only for verified logged-in Admins
                if (isAdmin && currentSubScreen == AppSubScreen.NONE && selectedTab != NavTab.MENU) {
                    ExtendedFloatingActionButton(
                        onClick = { showMemberUploadDialog = true },
                        containerColor = NeonPurple,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .testTag("fab_member_upload")
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "رفع من مدير الملفات",
                                tint = NeonCyan
                            )
                        },
                        text = {
                            Text(
                                text = "رفع من الملفات",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            },
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
                                            },
                                            onOpenUploadClick = { showMemberUploadDialog = true },
                                            isAdmin = isAdmin
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
                                            onLanguageChanged = { viewModel.setLanguage(it) },
                                            onOpenUploadClick = { showMemberUploadDialog = true }
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

        if (showMemberUploadDialog) {
            MemberUploadDialog(
                onDismissRequest = { showMemberUploadDialog = false },
                onAddTheme = {
                    viewModel.addTheme(it)
                    showMemberUploadDialog = false
                },
                onAddWallpaper = {
                    viewModel.addWallpaper(it)
                    showMemberUploadDialog = false
                },
                onAddIconPack = {
                    viewModel.addIconPack(it)
                    showMemberUploadDialog = false
                }
            )
        }
    }
}
