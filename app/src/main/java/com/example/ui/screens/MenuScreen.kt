package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.ThemeEngineManager
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalCurrentLanguage
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MenuScreen(
    themeEngineManager: ThemeEngineManager,
    onNavigateToMyContent: () -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val strings = LocalAppStrings.current
    val currentLanguage = LocalCurrentLanguage.current

    var isDarkModeEnabled by remember { mutableStateOf(true) }
    var isNotificationsEnabled by remember { mutableStateOf(true) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 120.dp)
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
                text = strings.menuTitle,
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

        Spacer(modifier = Modifier.height(10.dp))

        // Profile Header Card matching reference image ("chunoit", avatar, chevron)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF141129))
                .border(1.dp, Color(0x338C52FF), RoundedCornerShape(24.dp))
                .clickable {
                    Toast.makeText(context, "حساب المصمم نشط", Toast.LENGTH_SHORT).show()
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonPurple, NeonCyan)))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "chunoit",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonPurple.copy(alpha = 0.3f))
                            .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VIP VIP",
                            color = NeonCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "alrwysm239@gmail.com",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Menu items list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF141129))
                .border(1.dp, Color(0x228C52FF), RoundedCornerShape(24.dp))
        ) {
            MenuItemRow(
                icon = Icons.Default.FolderSpecial,
                title = strings.myContentTitle,
                subtitle = strings.myContentSubtitle,
                iconTint = NeonPurple,
                onClick = onNavigateToMyContent
            )

            MenuItemRow(
                icon = Icons.Default.Storage,
                title = strings.themeFolderTitle,
                subtitle = strings.themeFolderSubtitle,
                iconTint = NeonCyan,
                onClick = {
                    themeEngineManager.openHonorHuaweiThemeManager()
                }
            )

            // Notifications Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF221C42)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = strings.notificationsTitle,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = strings.notificationsSubtitle,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = isNotificationsEnabled,
                    onCheckedChange = { isNotificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonPurple,
                        uncheckedTrackColor = Color(0xFF2B2550)
                    )
                )
            }

            // Dark Mode toggle switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF221C42)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = strings.darkModeTitle,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = strings.darkModeSubtitle,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = { isDarkModeEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonCyan,
                        uncheckedTrackColor = Color(0xFF2B2550)
                    )
                )
            }

            // Language Selector Row (العربية | English | Français)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF221C42)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = strings.languageTitle,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentLanguage.displayName,
                                color = NeonCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Language selection pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = lang == currentLanguage
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
                                    else Brush.horizontalGradient(listOf(Color(0xFF1F1A3B), Color(0xFF1F1A3B)))
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) NeonCyan else Color(0x338C52FF),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onLanguageChanged(lang) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.displayName,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            MenuItemRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = strings.guideTitle,
                subtitle = strings.guideSubtitle,
                iconTint = NeonAmber,
                onClick = { showGuideDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // "Paramètres" / "الإعدادات" button with glowing purple pill outline matching screenshot bottom!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp)
                .shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = NeonPurple, spotColor = NeonPurple)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1E153D))
                .border(1.5.dp, Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)), RoundedCornerShape(28.dp))
                .clickable { showSettingsDialog = true }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = strings.btnSettings,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            title = {
                Text(strings.guideDialogTitle, color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.step1, color = TextSecondary, fontSize = 13.sp)
                    Text(strings.step2, color = TextSecondary, fontSize = 13.sp)
                    Text(strings.step3, color = TextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showGuideDialog = false
                    themeEngineManager.openHonorHuaweiThemeManager()
                }) {
                    Text(strings.btnOpenNativeManager, color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuideDialog = false }) {
                    Text(strings.btnClose, color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(strings.settingsDialogTitle, color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(strings.settingsDialogText, color = TextSecondary, fontSize = 13.sp)
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("OK", color = NeonPurple, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun MenuItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF221C42)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}
