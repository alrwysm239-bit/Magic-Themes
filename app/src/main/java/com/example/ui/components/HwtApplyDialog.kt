package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleCyanBrush
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File

import com.example.ui.theme.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HwtApplyDialog(
    themeTitle: String,
    hwtFile: File?,
    onOpenThemeManager: () -> Unit,
    onShareHwt: (File) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(16.dp)
    ) {
        val shape = RoundedCornerShape(26.dp)
        val borderBrush = Brush.linearGradient(
            listOf(NeonPurple, NeonCyan)
        )

        Box(
            modifier = Modifier
                .testTag("hwt_apply_dialog")
                .clip(shape)
                .background(DarkSurface)
                .border(1.5.dp, borderBrush, shape)
                .shadow(24.dp, shape, ambientColor = NeonPurple, spotColor = NeonCyan)
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Success Indicator
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = strings.dialogReadyTitle,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$themeTitle: ${strings.dialogReadyDesc}",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // 3 Step Guide Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF191630))
                        .border(1.dp, Color(0x338C52FF), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isHonor = hwtFile?.name?.endsWith(".hnt", ignoreCase = true) == true || themeTitle.contains("Honor", ignoreCase = true)
                    val platformName = if (isHonor) "Honor (MagicOS)" else "Huawei (EMUI)"
                    val formatExt = if (isHonor) ".hnt" else ".hwt"
                    val targetFolder = if (isHonor) "Honor/Themes" else "Huawei/Themes"

                    InstructionStep(
                        step = "1",
                        text = "تم إنشاء وحفظ حزمة $formatExt في مجلد $targetFolder بجهازك."
                    )
                    InstructionStep(
                        step = "2",
                        text = "افتح تطبيق ثيمات $platformName المثبت مسبقاً بهاتفك."
                    )
                    InstructionStep(
                        step = "3",
                        text = "انتقل إلى: أنا (Moi / Me) > ثيماتي (My Themes)، واختر الثيم ثم اضغط تطبيق."
                    )
                }

                val isHonorTheme = hwtFile?.name?.endsWith(".hnt", ignoreCase = true) == true || themeTitle.contains("Honor", ignoreCase = true)
                val platformNameBtn = if (isHonorTheme) "Honor" else "Huawei"
                val formatExtBtn = if (isHonorTheme) ".hnt" else ".hwt"

                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeonGradientButton(
                        text = "فتح تطبيق ثيمات $platformNameBtn",
                        icon = Icons.Default.Launch,
                        onClick = onOpenThemeManager,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_open_native_theme_mgr"
                    )

                    if (hwtFile != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x22FFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                                .clickable { onShareHwt(hwtFile) }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مشاركة أو تطبيق حزمة $formatExtBtn",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.btnClose,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionStep(step: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(NeonPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
