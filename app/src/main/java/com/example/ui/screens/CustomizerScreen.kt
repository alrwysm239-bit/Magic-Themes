package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ThemeEngineManager
import com.example.model.CustomizerState
import com.example.model.PreviewMode
import com.example.ui.components.HwtApplyDialog
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.PhoneMockupPreview
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleCyanBrush
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.io.File

import com.example.ui.theme.LocalAppStrings

@Composable
fun CustomizerScreen(
    customizerState: CustomizerState,
    themeEngineManager: ThemeEngineManager,
    onWallpaperSelected: (Int?, String) -> Unit,
    onAccentColorSelected: (Long) -> Unit,
    onClockStyleSelected: (Int) -> Unit,
    onTogglePreviewMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var activeToolTab by remember { mutableIntStateOf(0) } // 0: Color, 1: Wallpaper, 2: Clock, 3: Icons
    var isExporting by remember { mutableStateOf(false) }
    var exportedHwtFile by remember { mutableStateOf<File?>(null) }
    var showApplyDialog by remember { mutableStateOf(false) }

    val accentColors = listOf(
        0xFF00D2FF, // Cyan
        0xFF8C52FF, // Purple
        0xFFFF2A85, // Magenta
        0xFFFFB800, // Amber
        0xFF10B981, // Emerald
        0xFF3B82F6, // Electric Blue
        0xFFF43F5E, // Rose
        0xFFFFFFFF  // Silver White
    )

    val wallpapers = listOf(
        Triple(R.drawable.wallpaper_cyberpunk, "Cyberpunk City", listOf(0xFF2E0854, 0xFF00D2FF)),
        Triple(R.drawable.wallpaper_anime, "Anime Neon", listOf(0xFF4A0E4E, 0xFFFF2A85)),
        Triple(R.drawable.wallpaper_galaxy, "Deep Galaxy", listOf(0xFF1E1B4B, 0xFF7C3AED))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.customizerTitle,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // Switch Screen preview mode pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF231E44))
                    .border(1.dp, NeonPurple, RoundedCornerShape(20.dp))
                    .clickable { onTogglePreviewMode() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (customizerState.previewMode == PreviewMode.HOME_SCREEN) strings.screenHome else strings.screenLock,
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Phone Mockup with Glowing Radial Aura behind it (Matching image!)
        Box(
            modifier = Modifier
                .size(width = 240.dp, height = 370.dp)
                .drawBehind {
                    // Glowing circular spectrum ring behind phone
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(customizerState.accentColorLong),
                                NeonPurple,
                                NeonMagenta,
                                NeonAmber,
                                Color(customizerState.accentColorLong)
                            )
                        ),
                        radius = size.width * 0.6f,
                        style = Stroke(width = 8.dp.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            PhoneMockupPreview(
                wallpaperRes = customizerState.selectedWallpaperRes,
                wallpaperGradient = customizerState.wallpaperGradient,
                previewMode = customizerState.previewMode,
                clockStyle = customizerState.clockWidgetStyle,
                accentColor = Color(customizerState.accentColorLong),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Customizer Tool selector bar (Wallpaper, Color, Clock Widget, Font)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF141129))
                .border(1.dp, Color(0x338C52FF), RoundedCornerShape(24.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolTabButton(
                icon = Icons.Default.ColorLens,
                label = strings.toolColor,
                isSelected = activeToolTab == 0,
                onClick = { activeToolTab = 0 }
            )
            ToolTabButton(
                icon = Icons.Default.Brush,
                label = strings.toolWallpaper,
                isSelected = activeToolTab == 1,
                onClick = { activeToolTab = 1 }
            )
            ToolTabButton(
                icon = Icons.Default.Schedule,
                label = strings.toolClock,
                isSelected = activeToolTab == 2,
                onClick = { activeToolTab = 2 }
            )
            ToolTabButton(
                icon = Icons.Default.Dashboard,
                label = strings.toolIcons,
                isSelected = activeToolTab == 3,
                onClick = { activeToolTab = 3 }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content
        when (activeToolTab) {
            0 -> {
                // Color swatches row
                Text(
                    text = strings.accentColorHint,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
                ) {
                    items(accentColors) { colorLong ->
                        val isSelected = customizerState.accentColorLong == colorLong
                        Box(
                            modifier = Modifier
                                .testTag("swatch_${java.lang.Long.toHexString(colorLong)}")
                                .size(42.dp)
                                .shadow(8.dp, CircleShape, ambientColor = Color(colorLong), spotColor = Color(colorLong))
                                .clip(CircleShape)
                                .background(Color(colorLong))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color(0x44FFFFFF),
                                    shape = CircleShape
                                )
                                .clickable { onAccentColorSelected(colorLong) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Sélectionné",
                                    tint = if (colorLong == 0xFFFFFFFF) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Wallpapers row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
                ) {
                    items(wallpapers) { (res, name, _) ->
                        val isSelected = customizerState.selectedWallpaperRes == res
                        Box(
                            modifier = Modifier
                                .size(width = 90.dp, height = 70.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonCyan else Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                                .clickable { onWallpaperSelected(res, name) }
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = res),
                                contentDescription = name,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            2 -> {
                // Clock widget styles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    listOf("Néon Digital", "Minimaliste", "Double Fuseau").forEachIndexed { index, styleName ->
                        val isSelected = customizerState.clockWidgetStyle == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF2E2458) else Color(0xFF141128))
                                .border(1.dp, if (isSelected) NeonPurple else Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                                .clickable { onClockStyleSelected(index) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = styleName,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            3 -> {
                // Icons style
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    listOf("Néon 3D Glass", "Pastel Soft", "Cyber Minimal").forEach { style ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E193C))
                                .border(1.dp, Color(customizerState.accentColorLong), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = style,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: "Exporter .hwt" & "Appliquer"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeonGradientButton(
                text = if (isExporting) strings.exportingHwt else strings.exportHwt,
                icon = Icons.Default.AutoAwesome,
                isLoading = isExporting,
                onClick = {
                    coroutineScope.launch {
                        isExporting = true
                        val file = themeEngineManager.exportCustomThemeHwt(
                            state = customizerState,
                            customTitle = "Mon Thème Personnalisé"
                        )
                        isExporting = false
                        exportedHwtFile = file
                        showApplyDialog = true
                    }
                },
                modifier = Modifier.weight(1f),
                testTag = "btn_export_custom_hwt"
            )
        }
    }

    if (showApplyDialog) {
        HwtApplyDialog(
            themeTitle = "Mon Thème Personnalisé",
            hwtFile = exportedHwtFile,
            onOpenThemeManager = {
                themeEngineManager.openHonorHuaweiThemeManager()
            },
            onShareHwt = { file ->
                themeEngineManager.shareHwtFile(file)
            },
            onDismiss = { showApplyDialog = false }
        )
    }
}

@Composable
private fun ToolTabButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) NeonPurple else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
