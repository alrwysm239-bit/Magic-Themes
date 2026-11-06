package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassCardFill
import com.example.ui.theme.GlassCardGradient
import com.example.ui.theme.NeonPurple

fun Modifier.glassCard(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassCardFill,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(borderWidth, borderColor, shape)

fun Modifier.glassGradientCard(
    shape: Shape = RoundedCornerShape(20.dp),
    gradient: Brush = GlassCardGradient,
    borderBrush: Brush = Brush.linearGradient(
        listOf(NeonPurple.copy(alpha = 0.5f), Color(0x2200D2FF))
    ),
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(gradient)
    .border(borderWidth, borderBrush, shape)

fun Modifier.neonGlowBehind(
    glowColor: Color = NeonPurple,
    alpha: Float = 0.35f,
    radiusRatio: Float = 0.8f
): Modifier = this.drawBehind {
    drawCircle(
        color = glowColor.copy(alpha = alpha),
        radius = size.minDimension * radiusRatio
    )
}
