package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.R
import com.example.model.CategoryItem
import com.example.model.ThemeItem
import com.example.ui.components.HeaderFilter
import com.example.ui.components.QuickCategoryBubblesRow
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    themes: List<ThemeItem>,
    categories: List<CategoryItem>,
    onThemeClick: (ThemeItem) -> Unit,
    onCategoryClick: (String) -> Unit,
    onQuickBubbleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(HeaderFilter.FEATURED) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredThemes = remember(themes, selectedFilter, searchQuery) {
        val base = when (selectedFilter) {
            HeaderFilter.FEATURED -> themes.filter { it.isEditorsChoice || it.isPopular }
            HeaderFilter.TOP -> themes.sortedByDescending { it.rating }
        }
        if (searchQuery.isBlank()) base
        else base.filter { it.title.contains(searchQuery, ignoreCase = true) || it.creator.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // Top Header Bar
        item {
            TopHeaderBar(
                title = "Magic Themes",
                subtitle = "Honor & Huawei EMUI",
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                onSearchClick = { isSearchActive = !isSearchActive }
            )
        }

        // Inline Search Bar if active
        if (isSearchActive) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_input_field"),
                        placeholder = { Text("Rechercher un thème, créateur...", color = TextSecondary) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0x338C52FF),
                            focusedContainerColor = Color(0xE6120F24),
                            unfocusedContainerColor = Color(0xE6120F24)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Quick Category Glass Bubbles Row
        item {
            QuickCategoryBubblesRow(
                onBubbleClick = onQuickBubbleClick
            )
        }

        // Section: "Thèmes toujours populaires" (Trending/Popular)
        item {
            SectionHeader(
                title = "Thèmes toujours populaires",
                onSeeAllClick = { onCategoryClick("popular") }
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredThemes, key = { it.id }) { theme ->
                    ThemePreviewCard(
                        theme = theme,
                        onClick = { onThemeClick(theme) }
                    )
                }
            }
        }

        // Promotional Banner Carousel: "Les thèmes que tout le monde essaie"
        item {
            Spacer(modifier = Modifier.height(24.dp))
            PromoBanner(
                onClick = {
                    val target = themes.firstOrNull { it.id == "theme_phin_fall" } ?: themes.first()
                    onThemeClick(target)
                }
            )
        }

        // Section: "Thèmes choisis par l'éditeur" (Editor's Choice)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Thèmes choisis par l'éditeur",
                onSeeAllClick = { onCategoryClick("editors_choice") }
            )
        }

        item {
            val editorsChoice = themes.filter { it.isEditorsChoice }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(editorsChoice, key = { "ed_${it.id}" }) { theme ->
                    ThemePreviewCard(
                        theme = theme,
                        onClick = { onThemeClick(theme) }
                    )
                }
            }
        }

        // Section: Visual Categories (Cyberpunk, Anime, Minimalist, Gaming)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Explorer par Catégories",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(categories, key = { it.id }) { cat ->
            CategoryStripCard(
                category = cat,
                onClick = { onCategoryClick(cat.id) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = onSeeAllClick,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Voir tout",
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun ThemePreviewCard(
    theme: ThemeItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .testTag("theme_card_${theme.id}")
            .width(136.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Phone mockup preview screen
        Box(
            modifier = Modifier
                .width(136.dp)
                .height(220.dp)
                .shadow(12.dp, cardShape, ambientColor = NeonPurple, spotColor = NeonCyan)
                .clip(cardShape)
                .background(
                    Brush.verticalGradient(
                        theme.previewGradientColors.map { Color(it) }
                    )
                )
                .border(1.2.dp, Color(0x338C52FF), cardShape)
        ) {
            if (theme.previewDrawableRes != null) {
                Image(
                    painter = painterResource(id = theme.previewDrawableRes),
                    contentDescription = theme.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dark vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0x99000000))
                        )
                    )
            )

            // Animated badge if present
            if (theme.isAnimated) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC000000))
                        .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Animated",
                        color = NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = theme.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )

        // Price & Rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = theme.price,
                color = if (theme.price == "Gratuit") NeonCyan else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = String.format("%.1f", theme.rating),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PromoBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(130.dp)
            .shadow(16.dp, bannerShape, ambientColor = NeonAmber, spotColor = NeonPurple)
            .clip(bannerShape)
            .clickable(onClick = onClick)
    ) {
        // Banner background image
        Image(
            painter = painterResource(id = R.drawable.banner_promo_theme),
            contentDescription = "Promotion banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for high text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xEEFFE033), Color(0x88FFE033), Color(0x33000000))
                    )
                )
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Les thèmes que\ntout le monde essaie",
                    color = Color(0xFF1E103A),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ne tardez pas, achetez dès aujourd'hui",
                    color = Color(0xFF4A346E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Pager badge indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x99000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "1/3",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryStripCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(shape)
            .border(1.dp, Color(0x338C52FF), shape)
            .clickable(onClick = onClick)
    ) {
        if (category.drawableRes != null) {
            Image(
                painter = painterResource(id = category.drawableRes),
                contentDescription = category.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xEE0B091A), Color(0x880B091A), Color(0x220B091A))
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = category.subtitle,
                    color = NeonCyan,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NeonPurple.copy(alpha = 0.25f))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
