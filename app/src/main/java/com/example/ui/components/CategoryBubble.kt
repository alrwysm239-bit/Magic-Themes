package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary

data class QuickBubbleData(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val glowColor: Color,
    val testTag: String
)

@Composable
fun QuickCategoryBubblesRow(
    onBubbleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val bubbles = listOf(
        QuickBubbleData("my_content", strings.bubbleMyContent, Icons.Default.FolderSpecial, NeonMagenta, "bubble_my_content"),
        QuickBubbleData("noir", strings.bubbleDark, Icons.Default.Brightness2, NeonCyan, "bubble_noir"),
        QuickBubbleData("video", strings.bubbleVideo, Icons.Default.PlayCircleFilled, NeonPurple, "bubble_video"),
        QuickBubbleData("best", strings.bubbleBest, Icons.Default.EmojiEvents, NeonAmber, "bubble_best")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bubbles.forEach { item ->
            val shape = RoundedCornerShape(22.dp)
            val borderBrush = Brush.linearGradient(
                listOf(item.glowColor.copy(alpha = 0.8f), item.glowColor.copy(alpha = 0.2f))
            )

            Column(
                modifier = Modifier
                    .testTag(item.testTag)
                    .size(width = 76.dp, height = 86.dp)
                    .clip(shape)
                    .shadow(10.dp, shape, ambientColor = item.glowColor, spotColor = item.glowColor)
                    .background(Color(0xE6141129))
                    .border(1.2.dp, borderBrush, shape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onBubbleClick(item.id)
                    }
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.glowColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.glowColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
