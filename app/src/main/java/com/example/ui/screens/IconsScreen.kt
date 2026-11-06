package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconPackItem
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import com.example.ui.theme.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconsScreen(
    iconPacks: List<IconPackItem>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var selectedFilterKey by remember { mutableStateOf("Icon Packs") }
    var selectedPackForDetails by remember { mutableStateOf<IconPackItem?>(null) }

    val filterOptions = listOf(
        "Icon Packs" to strings.iconPacks,
        "Icons" to strings.iconsSimple,
        "Minimal" to strings.minimal
    )

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
                text = strings.iconsTitle,
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

        // Sub-filters
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
                        .testTag("icon_filter_${key.lowercase().replace(" ", "_")}")
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(Color(0xFF231E44)).border(1.2.dp, NeonPurple, RoundedCornerShape(20.dp))
                            } else {
                                Modifier.background(Color(0x1F2B244D)).border(1.dp, Color(0x228C52FF), RoundedCornerShape(20.dp))
                            }
                        )
                        .clickable { selectedFilterKey = key }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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

        // 2-Column Grid of Icon Packs matching reference image
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(iconPacks, key = { it.id }) { pack ->
                IconPackGridCard(
                    pack = pack,
                    onClick = { selectedPackForDetails = pack }
                )
            }
        }
    }

    selectedPackForDetails?.let { pack ->
        ModalBottomSheet(
            onDismissRequest = { selectedPackForDetails = null },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = pack.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Par ${pack.creator} · ${pack.iconCount} · Compatible MagicOS 7/8/9 & EMUI 12/13/14",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                // 4 Icon Preview row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF141128))
                        .border(1.dp, Color(0x338C52FF), RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val icons = listOf(Icons.Default.Phone, Icons.Default.Chat, Icons.Default.Language, Icons.Default.MusicNote)
                    icons.forEach { icon ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF231E44))
                                .border(1.dp, Color(pack.glowColorLong), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color(pack.glowColorLong),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                NeonGradientButton(
                    text = strings.btnDownloadIconPack,
                    onClick = {
                        Toast.makeText(context, "Pack ${pack.title} prêt dans /Honor/Themes/icons", Toast.LENGTH_SHORT).show()
                        selectedPackForDetails = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_download_icon_pack"
                )
            }
        }
    }
}

@Composable
fun IconPackGridCard(
    pack: IconPackItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(22.dp)
    val packColor = Color(pack.glowColorLong)

    Column(
        modifier = modifier
            .testTag("icon_pack_card_${pack.id}")
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Icon Pack preview box (with 2x2 grid of 3D-styled icons)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(12.dp, cardShape, ambientColor = packColor, spotColor = NeonPurple)
                .clip(cardShape)
                .background(Color(0xFF120E26))
                .border(1.2.dp, Brush.linearGradient(listOf(packColor.copy(0.7f), Color(0x228C52FF))), cardShape)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniIconCell(icon = Icons.Default.Phone, tint = packColor)
                    MiniIconCell(icon = Icons.Default.Chat, tint = NeonCyan)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniIconCell(icon = Icons.Default.CameraAlt, tint = NeonAmber)
                    MiniIconCell(icon = Icons.Default.Language, tint = packColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = pack.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        // Rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = String.format("%.1f", pack.rating),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = pack.styleTag,
                color = NeonCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MiniIconCell(icon: ImageVector, tint: Color) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .size(46.dp)
            .shadow(4.dp, shape, ambientColor = tint, spotColor = tint)
            .clip(shape)
            .background(Color(0xFF1D1838))
            .border(1.dp, tint.copy(alpha = 0.5f), shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}
