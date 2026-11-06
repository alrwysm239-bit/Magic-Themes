package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    THEMES("Thèmes", Icons.Filled.Palette, Icons.Outlined.Palette, "nav_tab_themes"),
    WALLPAPERS("Wallpapers", Icons.Filled.Image, Icons.Outlined.Image, "nav_tab_wallpapers"),
    ICONS("Icons", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_tab_icons"),
    CUSTOMIZER("Customizer", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tab_customizer"),
    MENU("Menu", Icons.Filled.Menu, Icons.Outlined.Menu, "nav_tab_menu")
}

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        val pillShape = RoundedCornerShape(32.dp)
        val borderGlowBrush = Brush.horizontalGradient(
            listOf(
                NeonCyan.copy(alpha = 0.8f),
                NeonPurple.copy(alpha = 0.9f),
                NeonCyan.copy(alpha = 0.8f)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(elevation = 16.dp, shape = pillShape, ambientColor = NeonPurple, spotColor = NeonCyan)
                .clip(pillShape)
                .background(Color(0xE60D0B1C)) // Translucent dark glass
                .border(width = 1.5.dp, brush = borderGlowBrush, shape = pillShape)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) NeonCyan else TextSecondary,
                    animationSpec = tween(durationMillis = 200),
                    label = "iconColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) TextPrimary else TextSecondary.copy(alpha = 0.7f),
                    animationSpec = tween(durationMillis = 200),
                    label = "textColor"
                )

                Column(
                    modifier = Modifier
                        .testTag(tab.testTag)
                        .weight(1f)
                        .height(58.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(tab)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.radialGradient(
                                                listOf(NeonPurple.copy(alpha = 0.45f), Color.Transparent)
                                            )
                                        )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    val strings = com.example.ui.theme.LocalAppStrings.current
                    val tabTitle = when (tab) {
                        NavTab.THEMES -> strings.tabThemes
                        NavTab.WALLPAPERS -> strings.tabWallpapers
                        NavTab.ICONS -> strings.tabIcons
                        NavTab.CUSTOMIZER -> strings.tabCustomizer
                        NavTab.MENU -> strings.tabMenu
                    }

                    Text(
                        text = tabTitle,
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
