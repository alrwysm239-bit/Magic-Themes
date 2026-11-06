package com.example

import com.example.data.ThemeRepository
import com.example.model.CustomizerState
import com.example.model.PreviewMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeEngineUnitTest {

    @Test
    fun repository_initializesThemesAndWallpapers() {
        val repo = ThemeRepository()
        val themes = repo.themes.value
        val wallpapers = repo.wallpapers.value
        val iconPacks = repo.iconPacks.value
        val categories = repo.categories.value

        assertTrue(themes.isNotEmpty())
        assertTrue(wallpapers.isNotEmpty())
        assertTrue(iconPacks.isNotEmpty())
        assertTrue(categories.isNotEmpty())

        val firstTheme = themes.first()
        assertNotNull(firstTheme.title)
        assertTrue(firstTheme.hwtFileName.endsWith(".hwt"))
    }

    @Test
    fun repository_favoriteToggle_works() {
        val repo = ThemeRepository()
        val initialThemes = repo.themes.value
        val testId = initialThemes.first().id
        val wasFav = initialThemes.first().isFavorite

        repo.toggleFavoriteTheme(testId)
        val updated = repo.themes.value.first { it.id == testId }
        assertEquals(!wasFav, updated.isFavorite)
    }

    @Test
    fun customizer_stateUpdates_correctly() {
        val repo = ThemeRepository()
        repo.updateCustomizerAccentColor(0xFF00D2FF)
        assertEquals(0xFF00D2FF, repo.customizerState.value.accentColorLong)

        repo.updateCustomizerClockStyle(2)
        assertEquals(2, repo.customizerState.value.clockWidgetStyle)

        val initialMode = repo.customizerState.value.previewMode
        repo.togglePreviewMode()
        val newMode = repo.customizerState.value.previewMode
        assertTrue(initialMode != newMode)
    }
}
