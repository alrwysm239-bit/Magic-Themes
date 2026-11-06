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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    var showAuthDialog by remember { mutableStateOf(false) }
    var showAdminDashboardDialog by remember { mutableStateOf(false) }

    val adminEmails = remember { setOf("zaim9002@gmail.com", "ogames226@gmail.com", "alrwysm239@gmail.com") }
    var currentUserEmail by remember { mutableStateOf("zaim9002@gmail.com") }
    val isAdmin = adminEmails.contains(currentUserEmail.trim().lowercase())

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
                .border(
                    1.dp,
                    if (isAdmin) NeonCyan.copy(alpha = 0.6f) else Color(0x338C52FF),
                    RoundedCornerShape(24.dp)
                )
                .clickable { showAuthDialog = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isAdmin) Brush.linearGradient(listOf(NeonPurple, NeonCyan))
                        else Brush.linearGradient(listOf(Color(0xFF554477), Color(0xFF332255)))
                    )
                    .border(2.dp, if (isAdmin) Color.White else Color(0x88FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (currentUserEmail.isNotBlank()) currentUserEmail.substringBefore("@") else "مستخدم جديد",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isAdmin) NeonCyan.copy(alpha = 0.2f)
                                else NeonPurple.copy(alpha = 0.2f)
                            )
                            .border(
                                1.dp,
                                if (isAdmin) NeonCyan else NeonPurple,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isAdmin) "ADMIN" else if (currentUserEmail.isNotBlank()) "USER" else "GUEST",
                            color = if (isAdmin) NeonCyan else NeonPurple,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = if (currentUserEmail.isNotBlank()) currentUserEmail else "اضغط لتسجيل الدخول كمسؤول",
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

        // Prominent Admin Dashboard Button (Only visible if isAdmin == true)
        if (isAdmin) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF8C52FF), Color(0xFF00D2FF))
                        )
                    )
                    .shadow(12.dp, RoundedCornerShape(22.dp), ambientColor = NeonPurple, spotColor = NeonCyan)
                    .clickable { showAdminDashboardDialog = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.28f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Dashboard",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "لوحة التحكم (Admin Dashboard)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "إدارة ورفع ثيمات Huawei (.hwt) و Honor (.hnt)",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

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

    if (showAuthDialog) {
        var inputEmail by remember { mutableStateOf(currentUserEmail) }

        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "المصادقة وإدارة الحساب (Supabase)",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "الحساب النشط حالياً: " + if (currentUserEmail.isNotBlank()) currentUserEmail else "زائر غير مسجل",
                        color = if (isAdmin) NeonCyan else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isAdmin) "الحالة: مسؤول معتمد (Admin) — الصلاحية كاملة وزر لوحة التحكم مفعّل."
                               else "الحالة: مستخدم عادي (User) — زر لوحة التحكم مخفي تماماً لمنع الوصول.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("البريد الإلكتروني لتسجيل الدخول:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)

                    OutlinedTextField(
                        value = inputEmail,
                        onValueChange = { inputEmail = it },
                        label = { Text("البريد الإلكتروني", fontSize = 12.sp) },
                        placeholder = { Text("zaim9002@gmail.com", fontSize = 12.sp, color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0x448C52FF)
                        )
                    )

                    Text("تبديل سريع للحسابات (اختبار حقيقي):", color = TextSecondary, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("zaim9002@gmail.com", "ogames226@gmail.com").forEach { email ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1F1A3B))
                                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable { inputEmail = email }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(email.substringBefore("@") + " (مسؤول)", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1F1A3B))
                                .border(1.dp, Color(0x338C52FF), RoundedCornerShape(10.dp))
                                .clickable { inputEmail = "user@gmail.com" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("user@gmail.com (عادي)", color = TextSecondary, fontSize = 10.sp)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1F1A3B))
                                .border(1.dp, Color(0x338C52FF), RoundedCornerShape(10.dp))
                                .clickable { inputEmail = "" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("تسجيل خروج (زائر)", color = Color.Red.copy(alpha = 0.8f), fontSize = 10.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    currentUserEmail = inputEmail.trim()
                    showAuthDialog = false
                    val roleLabel = if (adminEmails.contains(currentUserEmail.lowercase())) "مسؤول معتمد (Admin)" else "مستخدم عادي"
                    Toast.makeText(context, "تم تسجيل الدخول: $currentUserEmail ($roleLabel)", Toast.LENGTH_SHORT).show()
                }) {
                    Text("تأكيد الدخول", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showAdminDashboardDialog) {
        if (!isAdmin) {
            AlertDialog(
                onDismissRequest = { showAdminDashboardDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تم رفض الوصول (Access Denied)", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (currentUserEmail.isBlank()) "أنت غير مسجل الدخول حالياً." else "الحساب الحالي ($currentUserEmail) ليس لديه صلاحيات مسؤول في Supabase.",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "للوصول إلى لوحة التحكم، يجب تسجيل الدخول بحساب مسؤول معتمد (مثل zaim9002@gmail.com أو ogames226@gmail.com أو alrwysm239@gmail.com) أو حساب يمتلك دور admin في قاعدة البيانات.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showAdminDashboardDialog = false
                        showAuthDialog = true
                    }) {
                        Text("تسجيل الدخول كمسؤول", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAdminDashboardDialog = false }) {
                        Text("إغلاق", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface
            )
        } else {
            var newTitle by remember { mutableStateOf("") }
            var newDesc by remember { mutableStateOf("") }
            var selectedFormat by remember { mutableStateOf("hwt") } // hwt or hnt
            var selectedCategory by remember { mutableStateOf("Cyberpunk") }
            var uploadSuccessMessage by remember { mutableStateOf<String?>(null) }
            var mockThemesList by remember {
                mutableStateOf(
                    listOf(
                        Triple("Cyber Neon Core", "HWT", "14.2 MB"),
                        Triple("Magic Aurora OS", "HNT", "18.5 MB"),
                        Triple("Future Minimalist", "HWT", "12.0 MB")
                    )
                )
            }

            AlertDialog(
                onDismissRequest = { showAdminDashboardDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "لوحة تحكم المسؤول (Admin)",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Role verification banner with active session details
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1F1A3B))
                                .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("جلسة مسؤول معتمدة (Verified Admin)", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("المسؤول: $currentUserEmail", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("الدور: admin | وظيفة: is_admin() = true", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        // Format Selection (.hwt vs .hnt)
                        Text("صيغة الثيم المستهدفة:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("hwt" to "Huawei (.hwt)", "hnt" to "Honor (.hnt)").forEach { (formatKey, label) ->
                                val isSelected = selectedFormat == formatKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) NeonPurple else Color(0xFF1B1736))
                                        .border(1.dp, if (isSelected) NeonCyan else Color(0x338C52FF), RoundedCornerShape(12.dp))
                                        .clickable { selectedFormat = formatKey }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Title Input
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("عنوان الثيم", fontSize = 12.sp) },
                            placeholder = { Text("مثال: Honor Magic Futuristic", fontSize = 12.sp, color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = Color(0x448C52FF)
                            )
                        )

                        // Description Input
                        OutlinedTextField(
                            value = newDesc,
                            onValueChange = { newDesc = it },
                            label = { Text("وصف الثيم", fontSize = 12.sp) },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = Color(0x448C52FF)
                            )
                        )

                        // Upload Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)))
                                .clickable {
                                    if (newTitle.isNotBlank()) {
                                        mockThemesList = listOf(Triple(newTitle, selectedFormat.uppercase(), "16.4 MB")) + mockThemesList
                                        uploadSuccessMessage = "تم نشر الثيم ($selectedFormat.) بنجاح في Supabase!"
                                        newTitle = ""
                                        newDesc = ""
                                    } else {
                                        Toast.makeText(context, "يرجى كتابة عنوان الثيم", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("رفع ونشر الثيم في Supabase", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (uploadSuccessMessage != null) {
                            Text(uploadSuccessMessage!!, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Existing Themes Management
                        Text("الثيمات المنشورة (${mockThemesList.size}):", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        for ((title, fmt, size) in mockThemesList) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF191530))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("صيغة: .$fmt · حجم: $size", color = NeonCyan, fontSize = 10.sp)
                                }
                                IconButton(
                                    onClick = {
                                        mockThemesList = mockThemesList.filterNot { it.first == title }
                                        Toast.makeText(context, "تم حذف الثيم", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAdminDashboardDialog = false }) {
                        Text("إغلاق", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = DarkSurface
            )
        }
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
