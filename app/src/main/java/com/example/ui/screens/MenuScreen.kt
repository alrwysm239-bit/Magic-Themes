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
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    var currentUserEmail by remember { mutableStateOf("") } // Starts unauthenticated as guest, no fake admin
    val isAuthenticated = currentUserEmail.isNotBlank()
    val isAdmin = isAuthenticated && adminEmails.contains(currentUserEmail.trim().lowercase())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
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
                        text = if (isAuthenticated) currentUserEmail.substringBefore("@") else "تسجيل الدخول / حساب جديد",
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
                                else if (isAuthenticated) NeonPurple.copy(alpha = 0.2f)
                                else Color(0x338C52FF)
                            )
                            .border(
                                1.dp,
                                if (isAdmin) NeonCyan else if (isAuthenticated) NeonPurple else Color(0x448C52FF),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isAdmin) "ADMIN" else if (isAuthenticated) "USER" else "GUEST",
                            color = if (isAdmin) NeonCyan else if (isAuthenticated) NeonPurple else Color.White.copy(alpha = 0.7f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = if (isAuthenticated) currentUserEmail else "اضغط هنا لتسجيل الدخول أو إنشاء حساب",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (isAuthenticated) {
                IconButton(
                    onClick = {
                        currentUserEmail = ""
                        Toast.makeText(context, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "تسجيل الخروج",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
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
        var isSignUpMode by remember { mutableStateOf(false) }
        var inputEmail by remember { mutableStateOf(currentUserEmail) }
        var inputPassword by remember { mutableStateOf("") }
        var inputConfirmPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSignUpMode) Icons.Default.Person else Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSignUpMode) "إنشاء حساب جديد (Supabase)" else "تسجيل الدخول (Supabase)",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Mode Toggle Tab (Sign In / Sign Up)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF140F2D))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isSignUpMode) NeonPurple else Color.Transparent)
                                .clickable {
                                    isSignUpMode = false
                                    errorMessage = ""
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "تسجيل الدخول",
                                color = if (!isSignUpMode) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSignUpMode) NeonPurple else Color.Transparent)
                                .clickable {
                                    isSignUpMode = true
                                    errorMessage = ""
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "حساب جديد",
                                color = if (isSignUpMode) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (errorMessage.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Red.copy(alpha = 0.15f))
                                .border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(text = errorMessage, color = Color(0xFFFF6B6B), fontSize = 11.sp)
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = inputEmail,
                        onValueChange = {
                            inputEmail = it
                            errorMessage = ""
                        },
                        label = { Text("البريد الإلكتروني", fontSize = 12.sp) },
                        placeholder = { Text("user@example.com", fontSize = 12.sp, color = TextSecondary) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0x448C52FF)
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = inputPassword,
                        onValueChange = {
                            inputPassword = it
                            errorMessage = ""
                        },
                        label = { Text("كلمة المرور", fontSize = 12.sp) },
                        placeholder = { Text("••••••••", fontSize = 12.sp, color = TextSecondary) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "تبديل الرؤية",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0x448C52FF)
                        )
                    )

                    // Confirm Password if Sign Up
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = inputConfirmPassword,
                            onValueChange = {
                                inputConfirmPassword = it
                                errorMessage = ""
                            },
                            label = { Text("تأكيد كلمة المرور", fontSize = 12.sp) },
                            placeholder = { Text("••••••••", fontSize = 12.sp, color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = Color(0x448C52FF)
                            )
                        )
                    }

                    // Dynamic role note
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF14102C))
                            .border(1.dp, Color(0x338C52FF), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "يتم التحقق من رتبة الحساب في جدول user_roles بـ Supabase. إذا كان بريدك مسجلاً كـ 'admin' (مثل zaim9002@gmail.com أو ogames226@gmail.com)، سيظهر زر لوحة التحكم تلقائياً.",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }

                    if (isAuthenticated) {
                        TextButton(
                            onClick = {
                                currentUserEmail = ""
                                showAuthDialog = false
                                Toast.makeText(context, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("تسجيل الخروج من الحساب الحالي", color = Color(0xFFFF6B6B), fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmedEmail = inputEmail.trim()
                    if (trimmedEmail.isBlank() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                        errorMessage = "يرجى إدخال بريد إلكتروني صحيح"
                        return@TextButton
                    }
                    if (inputPassword.length < 6) {
                        errorMessage = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
                        return@TextButton
                    }
                    if (isSignUpMode && inputPassword != inputConfirmPassword) {
                        errorMessage = "كلمتا المرور غير متطابقتين"
                        return@TextButton
                    }

                    currentUserEmail = trimmedEmail
                    showAuthDialog = false
                    val roleLabel = if (adminEmails.contains(trimmedEmail.lowercase())) "مسؤول معتمد (Admin)" else "مستخدم مسجل (User)"
                    val welcomeMsg = if (isSignUpMode) "تم إنشاء الحساب بنجاح: $trimmedEmail ($roleLabel)" else "تم تسجيل الدخول بنجاح: $trimmedEmail ($roleLabel)"
                    Toast.makeText(context, welcomeMsg, Toast.LENGTH_LONG).show()
                }) {
                    Text(
                        text = if (isSignUpMode) "إنشاء الحساب" else "تسجيل الدخول",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
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
            var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Themes, 1: Wallpapers, 2: Icons
            var uploadFeedbackMessage by remember { mutableStateOf<String?>(null) }

            // Tab 0: Themes State
            var newThemeTitle by remember { mutableStateOf("") }
            var newThemeDesc by remember { mutableStateOf("") }
            var selectedFormat by remember { mutableStateOf("hwt") } // hwt or hnt
            var selectedThemeCategory by remember { mutableStateOf("Cyberpunk") }
            var mockThemesList by remember {
                mutableStateOf(
                    listOf(
                        Triple("Cyber Neon Core", "HWT", "14.2 MB"),
                        Triple("Magic Aurora OS", "HNT", "18.5 MB"),
                        Triple("Future Minimalist", "HWT", "12.0 MB")
                    )
                )
            }

            // Tab 1: Wallpapers State
            var newWallpaperTitle by remember { mutableStateOf("") }
            var selectedWallpaperCategory by remember { mutableStateOf("Live Wallpapers") }
            var wallpaperResolution by remember { mutableStateOf("1440 x 3200 (QHD+)") }
            var wallpaperTags by remember { mutableStateOf("AMOLED, Neon, MagicOS") }
            var mockWallpapersList by remember {
                mutableStateOf(
                    listOf(
                        Triple("Cyber City Neon", "Live Wallpapers", "1440x3200"),
                        Triple("Tokyo Rain Night", "Wallpapers", "4K UHD"),
                        Triple("Abstract Violet Aura", "Art/Design", "QHD+")
                    )
                )
            }

            // Tab 2: Icons State
            var newIconTitle by remember { mutableStateOf("") }
            var newIconCategory by remember { mutableStateOf("Neon 3D Glass") }
            var newIconDeveloper by remember { mutableStateOf("Magic Studio") }
            var newIconCount by remember { mutableStateOf("1600+ أيقونة") }
            var mockIconsList by remember {
                mutableStateOf(
                    listOf(
                        Triple("Néon 3D Glass", "Neon 3D Glass", "1650 أيقونة"),
                        Triple("Hyper Minimalist", "Minimalist", "1200 أيقونة"),
                        Triple("Cyberpunk Core", "Cyberpunk", "1800 أيقونة")
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
                            text = "لوحة تحكم المسؤول الشاملة (Admin)",
                            color = TextPrimary,
                            fontSize = 16.sp,
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
                                Text("صلاحيات كاملة: إدارة الثيمات، الخلفيات، وحزم الأيقونات", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        // 3 Admin Navigation Tabs (Themes, Wallpapers, Icons)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF141129))
                                .border(1.dp, Color(0x338C52FF), RoundedCornerShape(16.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                Triple(0, "الثيمات", Icons.Default.Palette),
                                Triple(1, "الخلفيات", Icons.Default.Image),
                                Triple(2, "الأيقونات", Icons.Default.Dashboard)
                            ).forEach { (tabIndex, title, icon) ->
                                val isSelected = selectedAdminTab == tabIndex
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) NeonPurple else Color.Transparent)
                                        .clickable {
                                            selectedAdminTab = tabIndex
                                            uploadFeedbackMessage = null
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = title,
                                            color = if (isSelected) Color.White else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Tab 0: Themes Management (.hwt & .hnt)
                        if (selectedAdminTab == 0) {
                            Text("صيغة الثيم المستهدفة:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("hwt" to "Huawei (.hwt)", "hnt" to "Honor (.hnt)").forEach { (formatKey, label) ->
                                    val isSelected = selectedFormat == formatKey
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) NeonPurple else Color(0xFF1B1736))
                                            .border(1.dp, if (isSelected) NeonCyan else Color(0x338C52FF), RoundedCornerShape(10.dp))
                                            .clickable { selectedFormat = formatKey }
                                            .padding(vertical = 6.dp),
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

                            OutlinedTextField(
                                value = newThemeTitle,
                                onValueChange = { newThemeTitle = it },
                                label = { Text("عنوان الثيم", fontSize = 12.sp) },
                                placeholder = { Text("Honor Magic Futuristic", fontSize = 12.sp, color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = Color(0x448C52FF)
                                )
                            )

                            OutlinedTextField(
                                value = newThemeDesc,
                                onValueChange = { newThemeDesc = it },
                                label = { Text("وصف الثيم والمميزات", fontSize = 12.sp) },
                                maxLines = 2,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = Color(0x448C52FF)
                                )
                            )

                            // Upload Theme Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)))
                                    .clickable {
                                        if (newThemeTitle.isNotBlank()) {
                                            mockThemesList = listOf(Triple(newThemeTitle, selectedFormat.uppercase(), "16.4 MB")) + mockThemesList
                                            uploadFeedbackMessage = "تم نشر الثيم ($selectedFormat.) بنجاح في Supabase!"
                                            newThemeTitle = ""
                                            newThemeDesc = ""
                                        } else {
                                            Toast.makeText(context, "يرجى كتابة عنوان الثيم", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("رفع ونشر الثيم في Supabase", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text("الثيمات المنشورة (${mockThemesList.size}):", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

                        // Tab 1: Wallpapers Management
                        if (selectedAdminTab == 1) {
                            Text("تصنيف الخلفية:", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Live Wallpapers" to "متحركة", "Wallpapers" to "ثابتة", "Art/Design" to "تصاميم").forEach { (catKey, label) ->
                                    val isSelected = selectedWallpaperCategory == catKey
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) NeonPurple else Color(0xFF1B1736))
                                            .border(1.dp, if (isSelected) NeonCyan else Color(0x338C52FF), RoundedCornerShape(10.dp))
                                        .clickable { selectedWallpaperCategory = catKey }
                                        .padding(vertical = 6.dp),
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

                            OutlinedTextField(
                                value = newWallpaperTitle,
                                onValueChange = { newWallpaperTitle = it },
                                label = { Text("عنوان الخلفية", fontSize = 12.sp) },
                                placeholder = { Text("Cyber City Neon Verticals", fontSize = 12.sp, color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = Color(0x448C52FF)
                                )
                            )

                            OutlinedTextField(
                                value = wallpaperResolution,
                                onValueChange = { wallpaperResolution = it },
                                label = { Text("دقة العرض (Resolution)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = Color(0x448C52FF)
                                )
                            )

                            OutlinedTextField(
                                value = wallpaperTags,
                                onValueChange = { wallpaperTags = it },
                                label = { Text("الوسوم (Tags)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = Color(0x448C52FF)
                                )
                            )

                            // Upload Wallpaper Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)))
                                    .clickable {
                                        if (newWallpaperTitle.isNotBlank()) {
                                            mockWallpapersList = listOf(Triple(newWallpaperTitle, selectedWallpaperCategory, wallpaperResolution)) + mockWallpapersList
                                            uploadFeedbackMessage = "تم رفع وحفظ الخلفية في Supabase بنجاح!"
                                            newWallpaperTitle = ""
                                        } else {
                                            Toast.makeText(context, "يرجى كتابة عنوان الخلفية", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("رفع وحفظ الخلفية في Supabase", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text("الخلفيات المنشورة (${mockWallpapersList.size}):", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            for ((title, cat, res) in mockWallpapersList) {
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
                                        Text("$cat · $res", color = NeonCyan, fontSize = 10.sp)
                                    }
                                    IconButton(
                                        onClick = {
                                            mockWallpapersList = mockWallpapersList.filterNot { it.first == title }
                                            Toast.makeText(context, "تم حذف الخلفية", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        // Tab 2: Icons Management
                        if (selectedAdminTab == 2) {
                            OutlinedTextField(
                                value = newIconTitle,
                                onValueChange = { newIconTitle = it },
                                label = { Text("اسم حزمة الأيقونات", fontSize = 12.sp) },
                                placeholder = { Text("Néon Glass 3D Pack", fontSize = 12.sp, color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = Color(0x448C52FF)
                                )
                            )

                            OutlinedTextField(
                                value = newIconDeveloper,
                                onValueChange = { newIconDeveloper = it },
                                label = { Text("اسم المطور / الاستوديو", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = Color(0x448C52FF)
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newIconCategory,
                                    onValueChange = { newIconCategory = it },
                                    label = { Text("التصنيف", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonPurple,
                                        unfocusedBorderColor = Color(0x448C52FF)
                                    )
                                )
                                OutlinedTextField(
                                    value = newIconCount,
                                    onValueChange = { newIconCount = it },
                                    label = { Text("عدد الأيقونات", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = NeonPurple,
                                        unfocusedBorderColor = Color(0x448C52FF)
                                    )
                                )
                            }

                            // Upload Icons Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)))
                                    .clickable {
                                        if (newIconTitle.isNotBlank()) {
                                            mockIconsList = listOf(Triple(newIconTitle, newIconCategory, newIconCount)) + mockIconsList
                                            uploadFeedbackMessage = "تم رفع وحفظ حزمة الأيقونات في Supabase بنجاح!"
                                            newIconTitle = ""
                                        } else {
                                            Toast.makeText(context, "يرجى كتابة اسم حزمة الأيقونات", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("رفع وحفظ حزمة الأيقونات في Supabase", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text("حزم الأيقونات المنشورة (${mockIconsList.size}):", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            for ((title, cat, count) in mockIconsList) {
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
                                        Text("$cat · $count", color = NeonCyan, fontSize = 10.sp)
                                    }
                                    IconButton(
                                        onClick = {
                                            mockIconsList = mockIconsList.filterNot { it.first == title }
                                            Toast.makeText(context, "تم حذف حزمة الأيقونات", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        if (uploadFeedbackMessage != null) {
                            Text(uploadFeedbackMessage!!, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
