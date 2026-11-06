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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.ThemeEngineManager
import com.example.model.ThemeItem
import com.example.ui.components.HwtApplyDialog
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.io.File

import com.example.ui.theme.LocalAppStrings

@Composable
fun MyContentScreen(
    themes: List<ThemeItem>,
    themeEngineManager: ThemeEngineManager,
    onBackClick: () -> Unit,
    onThemeClick: (ThemeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    val coroutineScope = rememberCoroutineScope()
    val strings = LocalAppStrings.current
    var selectedFilterKey by remember { mutableStateOf("downloaded") }
    var selectedThemeForDialog by remember { mutableStateOf<ThemeItem?>(null) }
    var readyHwtFile by remember { mutableStateOf<File?>(null) }

    val contentList = remember(themes, selectedFilterKey) {
        if (selectedFilterKey == "downloaded") {
            themes.filter { it.isDownloaded }
                .ifEmpty { listOf(themes.first()) }
        } else {
            themes.filter { it.isFavorite }
                .ifEmpty { themes.take(2) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.btnClose,
                        tint = TextPrimary
                    )
                }
                Text(
                    text = strings.myContentTitle,
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
                    contentDescription = strings.searchHint,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Filter Pills: [ المحملة ] [ المفضلة ]
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1B1736))
                .border(1.dp, Color(0x338C52FF), RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            val filterOptions = listOf(
                "downloaded" to strings.tabDownloaded,
                "favorites" to strings.tabFavorites
            )
            filterOptions.forEach { (key, label) ->
                val isSelected = key == selectedFilterKey
                Box(
                    modifier = Modifier
                        .testTag("filter_content_$key")
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)))
                            } else {
                                Modifier.background(Color.Transparent)
                            }
                        )
                        .clickable { selectedFilterKey = key }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // 2-Column Grid of installed .hwt themes matching screenshot
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(contentList, key = { it.id }) { theme ->
                InstalledThemeCard(
                    theme = theme,
                    onClick = { onThemeClick(theme) },
                    onApplyClick = {
                        coroutineScope.launch {
                            val file = themeEngineManager.generateHwtPackage(theme)
                            readyHwtFile = file
                            selectedThemeForDialog = theme
                        }
                    },
                    onUpdateClick = {
                        coroutineScope.launch {
                            val file = themeEngineManager.generateHwtPackage(theme)
                            readyHwtFile = file
                            selectedThemeForDialog = theme
                        }
                    }
                )
            }
        }
    }

    selectedThemeForDialog?.let { theme ->
        HwtApplyDialog(
            themeTitle = theme.title,
            hwtFile = readyHwtFile,
            onOpenThemeManager = {
                themeEngineManager.openHonorHuaweiThemeManager()
            },
            onShareHwt = { file ->
                themeEngineManager.shareHwtFile(file)
            },
            onDismiss = { selectedThemeForDialog = null }
        )
    }
}

@Composable
fun InstalledThemeCard(
    theme: ThemeItem,
    onClick: () -> Unit,
    onApplyClick: () -> Unit,
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = modifier
            .testTag("installed_theme_${theme.id}")
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF141129))
            .border(1.dp, Color(0x338C52FF), shape)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.verticalGradient(theme.previewGradientColors.map { Color(it) }))
        ) {
            if (theme.previewDrawableRes != null) {
                Image(
                    painter = painterResource(id = theme.previewDrawableRes),
                    contentDescription = theme.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = ".HWT",
                    color = NeonCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = theme.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Text(
            text = theme.sizeMb,
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        val strings = LocalAppStrings.current

        // Dual Action buttons: [ تحديث / Update ] [ تطبيق / Apply ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Update button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF241D44))
                    .border(1.dp, NeonPurple, RoundedCornerShape(14.dp))
                    .clickable { onUpdateClick() }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.btnUpdate,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Apply button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)))
                    .clickable { onApplyClick() }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.btnApply,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
