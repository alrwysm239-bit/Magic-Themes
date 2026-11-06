package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PreviewMode
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun PhoneMockupPreview(
    wallpaperRes: Int?,
    wallpaperGradient: List<Long>,
    previewMode: PreviewMode = PreviewMode.HOME_SCREEN,
    clockStyle: Int = 0,
    accentColor: Color = NeonCyan,
    modifier: Modifier = Modifier
) {
    val bezelShape = RoundedCornerShape(36.dp)
    val screenShape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier
            .shadow(24.dp, bezelShape, ambientColor = accentColor, spotColor = NeonPurple)
            .clip(bezelShape)
            .background(Color(0xFF0F0E17)) // Realistic dark titanium phone chassis
            .border(3.dp, Color(0xFF28253D), bezelShape)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Phone Screen Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(screenShape)
                .background(
                    Brush.verticalGradient(
                        wallpaperGradient.map { Color(it) }
                    )
                )
        ) {
            // Background Wallpaper image if available
            if (wallpaperRes != null) {
                Image(
                    painter = painterResource(id = wallpaperRes),
                    contentDescription = "Wallpaper preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dark gradient overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x66000000),
                                Color(0x11000000),
                                Color(0x88000000)
                            )
                        )
                    )
            )

            // Screen Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Status Bar & Camera Punch Hole
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "12:45",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Central Camera Punch Hole
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(0.5.dp, Color(0x33FFFFFF), CircleShape)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "100%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Middle area: Lock Screen or Home Screen Widget
                AnimatedContent(
                    targetState = previewMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "previewContent"
                ) { mode ->
                    when (mode) {
                        PreviewMode.LOCK_SCREEN -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                when (clockStyle) {
                                    0 -> { // Neon Futuristic Digital Clock
                                        Text(
                                            text = "12:45",
                                            color = accentColor,
                                            fontSize = 44.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "Vendredi, 6 Novembre",
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    1 -> { // Minimalist Thin Clock
                                        Text(
                                            text = "12:45",
                                            color = Color.White,
                                            fontSize = 38.sp,
                                            fontWeight = FontWeight.Light
                                        )
                                        Text(
                                            text = "MagicOS · 24°C Ensoleillé",
                                            color = accentColor,
                                            fontSize = 10.sp
                                        )
                                    }
                                    2 -> { // Dual World Clock
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("PARIS", color = Color(0xAAFFFFFF), fontSize = 9.sp)
                                                Text("12:45", color = accentColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("TOKYO", color = Color(0xAAFFFFFF), fontSize = 9.sp)
                                                Text("20:45", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = "12:45",
                                            color = accentColor,
                                            fontSize = 42.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Lock Screen notification card
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x33000000))
                                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Glisser pour déverrouiller",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        PreviewMode.HOME_SCREEN -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Glass Weather Widget
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.92f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0x33100E26))
                                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(18.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Paris · 24°C",
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Honor MagicOS Theme",
                                                color = accentColor,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.WbSunny,
                                            contentDescription = null,
                                            tint = NeonAmber,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom App Dock (4 glowing 3D-styled icons)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icons = listOf(
                        Icons.Default.Phone to accentColor,
                        Icons.Default.Chat to NeonPurple,
                        Icons.Default.Language to NeonCyan,
                        Icons.Default.CameraAlt to NeonAmber
                    )

                    icons.forEach { (icon, color) ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(Color(0xE6141229))
                                .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(11.dp))
                                .shadow(6.dp, RoundedCornerShape(11.dp), ambientColor = color, spotColor = color),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
