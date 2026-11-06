package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CategoryItem
import com.example.model.ThemeItem
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import com.example.ui.theme.LocalAppStrings

@Composable
fun CategoriesScreen(
    categories: List<CategoryItem>,
    themes: List<ThemeItem>,
    selectedCategoryInitial: String? = null,
    onThemeClick: (ThemeItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var selectedCategory by remember { mutableStateOf(selectedCategoryInitial) }

    BackHandler {
        if (selectedCategory != null) {
            selectedCategory = null
        } else {
            onBackClick()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (selectedCategory != null) selectedCategory = null else onBackClick()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.btnClose,
                        tint = TextPrimary
                    )
                }
                Text(
                    text = if (selectedCategory != null) {
                        categories.firstOrNull { it.id == selectedCategory }?.title ?: strings.tabThemes
                    } else strings.exploreCategories,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x338C52FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Recherche",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (selectedCategory == null) {
            // Category list with large visual cards matching the reference image!
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories, key = { it.id }) { cat ->
                    LargeCategoryCard(
                        category = cat,
                        onClick = { selectedCategory = cat.id }
                    )
                }
            }
        } else {
            // Themes filtered by the selected category
            val filtered = remember(selectedCategory, themes) {
                val catObj = categories.firstOrNull { it.id == selectedCategory }
                val title = catObj?.title ?: ""
                themes.filter { it.category.equals(title, ignoreCase = true) || it.category == selectedCategory }
                    .ifEmpty { themes } // fallback to all if none
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { theme ->
                    ThemePreviewCard(
                        theme = theme,
                        onClick = { onThemeClick(theme) }
                    )
                }
            }
        }
    }
}

@Composable
fun LargeCategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .testTag("category_card_${category.id}")
            .fillMaxWidth()
            .height(130.dp)
            .shadow(12.dp, shape, ambientColor = NeonPurple, spotColor = NeonCyan)
            .clip(shape)
            .border(1.2.dp, Color(0x338C52FF), shape)
            .clickable(onClick = onClick)
    ) {
        if (category.drawableRes != null) {
            Image(
                painter = painterResource(id = category.drawableRes),
                contentDescription = category.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            category.gradientColors.map { Color(it) }
                        )
                    )
            )
        }

        // Dark gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xEE090815),
                            Color(0x99090815),
                            Color(0x33090815)
                        )
                    )
                )
        )

        // Title and description
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.title,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.subtitle,
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = category.themeCount,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
