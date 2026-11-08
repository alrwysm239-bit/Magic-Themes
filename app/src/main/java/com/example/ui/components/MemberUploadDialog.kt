package com.example.ui.components

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.IconPackItem
import com.example.model.ThemeItem
import com.example.model.WallpaperItem
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleCyanBrush
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun MemberUploadDialog(
    onDismissRequest: () -> Unit,
    onAddTheme: (ThemeItem) -> Unit,
    onAddWallpaper: (WallpaperItem) -> Unit,
    onAddIconPack: (IconPackItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategoryIndex by remember { mutableIntStateOf(0) } // 0: Themes, 1: Wallpapers, 2: Icons
    var isUploading by remember { mutableStateOf(false) }
    var uploadSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Common State
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var creatorName by remember { mutableStateOf("عضو Magic Themes") }
    var pickedFileName by remember { mutableStateOf<String?>(null) }
    var pickedFileUri by remember { mutableStateOf<Uri?>(null) }

    // Theme Specific
    var selectedFormat by remember { mutableStateOf("hwt") } // hwt or hnt
    var themeCategory by remember { mutableStateOf("Cyberpunk") }

    // Wallpaper Specific
    var wallpaperCategory by remember { mutableStateOf("Live Wallpapers") }

    // Icons Specific
    var iconCount by remember { mutableStateOf("+1600") }
    var iconStyleTag by remember { mutableStateOf("Neon 3D") }

    // File Picker Launchers
    val themePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedFileUri = uri
            val fileName = queryFileName(context.contentResolver, uri) ?: uri.lastPathSegment ?: "theme_package.hwt"
            pickedFileName = fileName
            if (fileName.lowercase().endsWith(".hnt")) {
                selectedFormat = "hnt"
            } else if (fileName.lowercase().endsWith(".hwt")) {
                selectedFormat = "hwt"
            }
            if (title.isBlank()) {
                title = fileName.substringBeforeLast(".")
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedFileUri = uri
            val fileName = queryFileName(context.contentResolver, uri) ?: uri.lastPathSegment ?: "wallpaper.jpg"
            pickedFileName = fileName
            if (title.isBlank()) {
                title = fileName.substringBeforeLast(".")
            }
        }
    }

    val iconArchivePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedFileUri = uri
            val fileName = queryFileName(context.contentResolver, uri) ?: uri.lastPathSegment ?: "icons_pack.zip"
            pickedFileName = fileName
            if (title.isBlank()) {
                title = fileName.substringBeforeLast(".")
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(28.dp)),
            color = DarkSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "رفع ملفات من الجهاز",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "استيراد مباشر عبر مدير الملفات",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("close_upload_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: 0: Themes, 1: Wallpapers, 2: Icons
                TabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    containerColor = DarkBackground,
                    contentColor = NeonCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                            color = NeonCyan,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF28234D), RoundedCornerShape(16.dp))
                ) {
                    Tab(
                        selected = selectedCategoryIndex == 0,
                        onClick = {
                            selectedCategoryIndex = 0
                            pickedFileName = null
                            pickedFileUri = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ثيمات", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedCategoryIndex == 1,
                        onClick = {
                            selectedCategoryIndex = 1
                            pickedFileName = null
                            pickedFileUri = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Wallpaper, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("خلفيات", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedCategoryIndex == 2,
                        onClick = {
                            selectedCategoryIndex = 2
                            pickedFileName = null
                            pickedFileUri = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("أيقونات", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pick from File Manager Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (selectedCategoryIndex) {
                                0 -> themePickerLauncher.launch("*/*")
                                1 -> imagePickerLauncher.launch("image/*")
                                2 -> iconArchivePickerLauncher.launch("*/*")
                            }
                        }
                        .testTag("file_manager_pick_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF191438)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = NeonPurpleCyanBrush,
                        width = 1.5.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (pickedFileName != null) Icons.Default.CheckCircle else Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = if (pickedFileName != null) NeonCyan else NeonAmber,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (pickedFileName != null)
                                "تم اختيار الملف بنجاح ✓"
                            else
                                "اضغط لفتح مدير الملفات في جهازك",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (pickedFileName != null)
                                pickedFileName!!
                            else
                                when (selectedCategoryIndex) {
                                    0 -> "اختر حزمة ثيم بصيغة .hwt أو .hnt أو .zip"
                                    1 -> "اختر صورة خلفية عالية الدقة (JPG, PNG, WebP)"
                                    else -> "اختر ملف حزمة الأيقونات المضغوطة (ZIP)"
                                },
                            color = if (pickedFileName != null) NeonCyan else TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form Inputs
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("العنوان / الاسم") },
                    placeholder = {
                        Text(
                            when (selectedCategoryIndex) {
                                0 -> "مثال: ثيم Magic Neon Cyber"
                                1 -> "مثال: خلفية الفضاء العميق 4K"
                                else -> "مثال: حزمة أيقونات الزجاج النيون"
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("member_upload_title_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF332B5C)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = creatorName,
                    onValueChange = { creatorName = it },
                    label = { Text("اسم المصمم / العضو") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF332B5C)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Format & category selectors based on tab
                if (selectedCategoryIndex == 0) {
                    // Themes Format
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("صيغة الثيم المستهدفة:", color = TextSecondary, fontSize = 12.sp)
                        Row {
                            ChoiceChipButton(
                                label = "Huawei (.hwt)",
                                isSelected = selectedFormat == "hwt",
                                onClick = { selectedFormat = "hwt" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ChoiceChipButton(
                                label = "Honor (.hnt)",
                                isSelected = selectedFormat == "hnt",
                                onClick = { selectedFormat = "hnt" }
                            )
                        }
                    }
                } else if (selectedCategoryIndex == 1) {
                    // Wallpapers Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("نوع الخلفية:", color = TextSecondary, fontSize = 12.sp)
                        Row {
                            ChoiceChipButton(
                                label = "متحركة (Live)",
                                isSelected = wallpaperCategory == "Live Wallpapers",
                                onClick = { wallpaperCategory = "Live Wallpapers" }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ChoiceChipButton(
                                label = "ثابتة (Static)",
                                isSelected = wallpaperCategory == "Wallpapers",
                                onClick = { wallpaperCategory = "Wallpapers" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف والمميزات") },
                    placeholder = { Text("وصف مختصر للمحتوى المرفوع...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF332B5C)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            title = "محتوى جديد مستورد"
                        }
                        isUploading = true

                        when (selectedCategoryIndex) {
                            0 -> {
                                val newTheme = ThemeItem(
                                    id = "imported_theme_${UUID.randomUUID()}",
                                    title = title,
                                    creator = creatorName,
                                    price = "مجاني (مرفوع)",
                                    rating = 5.0f,
                                    downloadsCount = "1",
                                    sizeMb = "18.5 MB",
                                    isPopular = true,
                                    isEditorsChoice = false,
                                    category = themeCategory,
                                    description = description.ifBlank { "تم استيراد هذا الثيم مباشرة عبر مدير الملفات لهواتف هونر وهواوي." },
                                    compatibleVersions = if (selectedFormat == "hnt") "Honor MagicOS 7/8/9 Edition" else "Huawei EMUI 12/13/14 Edition",
                                    hwtFileName = pickedFileName ?: "imported_theme.${selectedFormat}",
                                    format = selectedFormat,
                                    previewGradientColors = listOf(0xFF8C52FF, 0xFF00D2FF)
                                )
                                onAddTheme(newTheme)
                            }

                            1 -> {
                                val newWallpaper = WallpaperItem(
                                    id = "imported_wall_${UUID.randomUUID()}",
                                    title = title,
                                    category = wallpaperCategory,
                                    imageUri = pickedFileUri?.toString(),
                                    downloadsCount = "1",
                                    isLive = wallpaperCategory == "Live Wallpapers",
                                    isFavorite = false,
                                    gradientColors = listOf(0xFF00D2FF, 0xFF8C52FF)
                                )
                                onAddWallpaper(newWallpaper)
                            }

                            2 -> {
                                val newIcon = IconPackItem(
                                    id = "imported_icon_${UUID.randomUUID()}",
                                    title = title,
                                    creator = creatorName,
                                    rating = 5.0f,
                                    iconCount = iconCount,
                                    styleTag = iconStyleTag,
                                    packageFileUri = pickedFileUri?.toString(),
                                    glowColorLong = 0xFF8C52FF,
                                    isDownloaded = false
                                )
                                onAddIconPack(newIcon)
                            }
                        }

                        isUploading = false
                        uploadSuccessMessage = "تمت إضافة المحتوى بنجاح للتطبيق!"
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_member_upload_button"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تأكيد الرفع والإضافة للتطبيق",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceChipButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) NeonPurple else Color(0xFF221C44))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun queryFileName(resolver: android.content.ContentResolver, uri: Uri): String? {
    val returnCursor = resolver.query(uri, null, null, null, null) ?: return null
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = if (nameIndex != -1) returnCursor.getString(nameIndex) else null
    returnCursor.close()
    return name
}
