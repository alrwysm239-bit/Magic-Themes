package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ThemeEngineManager
import com.example.data.ThemeRepository
import com.example.model.CategoryItem
import com.example.model.CustomizerState
import com.example.model.IconPackItem
import com.example.model.ThemeItem
import com.example.model.WallpaperItem
import com.example.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class AppSubScreen {
    NONE,
    THEME_DETAILS,
    CATEGORIES_DETAIL,
    MY_CONTENT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val themeEngineManager = ThemeEngineManager(application)
    private val repository = ThemeRepository()

    val themes: StateFlow<List<ThemeItem>> = repository.themes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallpapers: StateFlow<List<WallpaperItem>> = repository.wallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val iconPacks: StateFlow<List<IconPackItem>> = repository.iconPacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryItem>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customizerState: StateFlow<CustomizerState> = repository.customizerState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CustomizerState())

    private val _selectedTab = MutableStateFlow(NavTab.THEMES)
    val selectedTab: StateFlow<NavTab> = _selectedTab.asStateFlow()

    private val _currentSubScreen = MutableStateFlow(AppSubScreen.NONE)
    val currentSubScreen: StateFlow<AppSubScreen> = _currentSubScreen.asStateFlow()

    private val _selectedTheme = MutableStateFlow<ThemeItem?>(null)
    val selectedTheme: StateFlow<ThemeItem?> = _selectedTheme.asStateFlow()

    private val _selectedCategoryInitial = MutableStateFlow<String?>(null)
    val selectedCategoryInitial: StateFlow<String?> = _selectedCategoryInitial.asStateFlow()

    private val _currentLanguage = MutableStateFlow(com.example.ui.theme.AppLanguage.ARABIC)
    val currentLanguage: StateFlow<com.example.ui.theme.AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(language: com.example.ui.theme.AppLanguage) {
        _currentLanguage.value = language
    }

    fun selectTab(tab: NavTab) {
        _selectedTab.value = tab
        _currentSubScreen.value = AppSubScreen.NONE
    }

    fun openThemeDetails(theme: ThemeItem) {
        _selectedTheme.value = theme
        _currentSubScreen.value = AppSubScreen.THEME_DETAILS
    }

    fun openCategories(categoryId: String? = null) {
        _selectedCategoryInitial.value = categoryId
        _currentSubScreen.value = AppSubScreen.CATEGORIES_DETAIL
    }

    fun openMyContent() {
        _currentSubScreen.value = AppSubScreen.MY_CONTENT
    }

    fun closeSubScreen() {
        _currentSubScreen.value = AppSubScreen.NONE
    }

    fun toggleFavoriteTheme(themeId: String) {
        repository.toggleFavoriteTheme(themeId)
        if (_selectedTheme.value?.id == themeId) {
            _selectedTheme.update { it?.copy(isFavorite = !(it.isFavorite)) }
        }
    }

    fun toggleFavoriteWallpaper(wallpaperId: String) {
        repository.toggleFavoriteWallpaper(wallpaperId)
    }

    fun markThemeDownloaded(themeId: String) {
        repository.markThemeDownloaded(themeId)
        if (_selectedTheme.value?.id == themeId) {
            _selectedTheme.update { it?.copy(isDownloaded = true) }
        }
    }

    fun updateCustomizerWallpaper(drawableRes: Int?, name: String) {
        repository.updateCustomizerWallpaper(drawableRes, name)
    }

    fun updateCustomizerAccentColor(colorLong: Long) {
        repository.updateCustomizerAccentColor(colorLong)
    }

    fun updateCustomizerClockStyle(style: Int) {
        repository.updateCustomizerClockStyle(style)
    }

    fun toggleCustomizerPreviewMode() {
        repository.togglePreviewMode()
    }
}
