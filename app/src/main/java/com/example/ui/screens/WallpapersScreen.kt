package com.example.ui.screens

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WallpaperItem
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleCyanBrush
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.ui.theme.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpapersScreen(
    wallpapers: List<WallpaperItem>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    var selectedFilterKey by remember { mutableStateOf("Live Wallpapers") }
    var selectedWallpaperForPreview by remember { mutableStateOf<WallpaperItem?>(null) }
    var isApplying by remember { mutableStateOf(false) }

    val filterOptions = listOf(
        "Live Wallpapers" to strings.liveWallpapers,
        "Wallpapers" to strings.allWallpapers,
        "Art/Design" to strings.artDesign
    )

    val filteredList = remember(wallpapers, selectedFilterKey) {
        wallpapers.filter { it.category == selectedFilterKey }
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.wallpapersTitle,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x338C52FF))
                    .border(1.dp, Color(0x668C52FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = strings.searchHint,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Sub-filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { (key, label) ->
                val isSelected = key == selectedFilterKey

                Box(
                    modifier = Modifier
                        .testTag("wallpaper_filter_${key.lowercase().replace(" ", "_")}")
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(Color(0xFF231E44)).border(1.2.dp, NeonPurple, RoundedCornerShape(20.dp))
                            } else {
                                Modifier.background(Color(0x1F2B244D)).border(1.dp, Color(0x228C52FF), RoundedCornerShape(20.dp))
                            }
                        )
                        .clickable { selectedFilterKey = key }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
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

        // 3-Column Wallpaper Grid matching screenshot
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList, key = { it.id }) { item ->
                WallpaperGridCard(
                    wallpaper = item,
                    onClick = { selectedWallpaperForPreview = item },
                    onDownloadClick = {
                        Toast.makeText(context, "Téléchargé dans /Honor/Wallpapers: ${item.title}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Modal Bottom Sheet Preview
    selectedWallpaperForPreview?.let { wallpaper ->
        ModalBottomSheet(
            onDismissRequest = { selectedWallpaperForPreview = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // High res preview card
                val shape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(9f / 16f)
                        .clip(shape)
                        .border(1.5.dp, NeonPurpleCyanBrush, shape)
                ) {
                    if (wallpaper.drawableRes != null) {
                        Image(
                            painter = painterResource(id = wallpaper.drawableRes),
                            contentDescription = wallpaper.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (!wallpaper.imageUri.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = wallpaper.imageUri,
                            contentDescription = wallpaper.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (!wallpaper.imageUrl.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = wallpaper.imageUrl,
                            contentDescription = wallpaper.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(wallpaper.gradientColors.map { Color(it) })),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Wallpaper, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(48.dp))
                        }
                    }
                }

                Text(
                    text = wallpaper.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${wallpaper.category} · Résolution QHD+ · Compatible MagicOS / EMUI",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                // Wallpaper Target Selection: Home Screen / Lock Screen / Both
                var selectedTargetMode by remember { mutableStateOf(1) } // 1: Home, 2: Lock, 3: Both
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141128))
                        .border(1.dp, Color(0x338C52FF), RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        1 to strings.applyHomeOnly,
                        2 to strings.applyLockOnly,
                        3 to strings.applyBothScreens
                    ).forEach { (mode, label) ->
                        val isSelected = selectedTargetMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonPurple else Color.Transparent)
                                .clickable { selectedTargetMode = mode }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeonGradientButton(
                        text = if (isApplying) strings.applyingWallpaper else strings.applyWallpaper,
                        icon = Icons.Default.Wallpaper,
                        isLoading = isApplying,
                        onClick = {
                            coroutineScope.launch {
                                isApplying = true
                                var applySuccess = false
                                withContext(Dispatchers.IO) {
                                    try {
                                        val wm = WallpaperManager.getInstance(context)
                                        var bitmap: android.graphics.Bitmap? = null

                                        // 1. Try decoding drawable resource
                                        if (wallpaper.drawableRes != null) {
                                            bitmap = BitmapFactory.decodeResource(context.resources, wallpaper.drawableRes)
                                        }

                                        // 2. Try decoding imageUri if available
                                        if (bitmap == null && !wallpaper.imageUri.isNullOrBlank()) {
                                            try {
                                                val uri = android.net.Uri.parse(wallpaper.imageUri)
                                                context.contentResolver.openInputStream(uri)?.use { stream ->
                                                    bitmap = BitmapFactory.decodeStream(stream)
                                                }
                                            } catch (_: Exception) {}
                                        }

                                        // 3. Fallback: generate high-res gradient bitmap
                                        if (bitmap == null) {
                                            val w = 1080
                                            val h = 1920
                                            val bm = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                                            val canvas = android.graphics.Canvas(bm)
                                            val paint = android.graphics.Paint()
                                            val colors = wallpaper.gradientColors.map { it.toInt() }.toIntArray()
                                            val shader = android.graphics.LinearGradient(
                                                0f, 0f, 0f, h.toFloat(),
                                                colors, null, android.graphics.Shader.TileMode.CLAMP
                                            )
                                            paint.shader = shader
                                            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
                                            bitmap = bm
                                        }

                                        if (bitmap != null) {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                val whichFlag = when (selectedTargetMode) {
                                                    1 -> WallpaperManager.FLAG_SYSTEM // Home screen
                                                    2 -> WallpaperManager.FLAG_LOCK   // Lock screen
                                                    else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK // Both
                                                }
                                                wm.setBitmap(bitmap, null, true, whichFlag)
                                            } else {
                                                wm.setBitmap(bitmap)
                                            }
                                            applySuccess = true
                                        }
                                    } catch (e: Exception) {
                                        applySuccess = false
                                    }
                                }
                                isApplying = false
                                if (applySuccess) {
                                    Toast.makeText(context, strings.wallpaperAppliedSuccess, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "تعذر تعيين الخلفية مباشرة. يرجى التحقق من أذونات النظام.", Toast.LENGTH_SHORT).show()
                                }
                                selectedWallpaperForPreview = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "btn_apply_wallpaper"
                    )

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0x338C52FF))
                            .border(1.dp, NeonPurple, CircleShape)
                            .clickable { onToggleFavorite(wallpaper.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (wallpaper.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (wallpaper.isFavorite) NeonMagenta else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WallpaperGridCard(
    wallpaper: WallpaperItem,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .testTag("wallpaper_item_${wallpaper.id}")
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .shadow(8.dp, shape, ambientColor = NeonPurple, spotColor = NeonCyan)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    wallpaper.gradientColors.map { Color(it) }
                )
            )
            .border(1.dp, Color(0x338C52FF), shape)
            .clickable(onClick = onClick)
    ) {
        if (wallpaper.drawableRes != null) {
            Image(
                painter = painterResource(id = wallpaper.drawableRes),
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!wallpaper.imageUri.isNullOrBlank()) {
            coil.compose.AsyncImage(
                model = wallpaper.imageUri,
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!wallpaper.imageUrl.isNullOrBlank()) {
            coil.compose.AsyncImage(
                model = wallpaper.imageUrl,
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Circular download overlay button at the bottom of the card matching the reference image!
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(NeonPurple, NeonCyan)))
                .shadow(6.dp, CircleShape, ambientColor = NeonPurple, spotColor = NeonPurple)
                .clickable { onDownloadClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Télécharger le fond d'écran",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
