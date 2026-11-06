package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Dark Obsidian & Void Colors
val DarkBackground = Color(0xFF07060E)
val DarkSurface = Color(0xFF110F20)
val DarkSurfaceVariant = Color(0xFF1A1730)
val GlassSurface = Color(0x281F1A3A)
val GlassBorder = Color(0x33A78BFA)
val GlassCardFill = Color(0x1F2A244D)

// Neon Accent Colors
val NeonPurple = Color(0xFF8C52FF)
val NeonPurpleLight = Color(0xFFA78BFA)
val NeonPurpleDark = Color(0xFF5B21B6)

val NeonCyan = Color(0xFF00D2FF)
val NeonCyanLight = Color(0xFF67E8F9)
val NeonCyanDark = Color(0xFF0284C7)

val NeonMagenta = Color(0xFFFF2A85)
val NeonPink = Color(0xFFF43F5E)
val NeonAmber = Color(0xFFFFB800)
val NeonOrange = Color(0xFFFF7A00)
val NeonGreen = Color(0xFF10B981)

// Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// Gradients
val NeonPurpleCyanBrush = Brush.horizontalGradient(
    colors = listOf(NeonPurple, NeonCyan)
)

val NeonCyanPurpleBrush = Brush.horizontalGradient(
    colors = listOf(NeonCyan, NeonPurple)
)

val NeonPurpleMagentaBrush = Brush.linearGradient(
    colors = listOf(NeonPurple, NeonMagenta)
)

val AmberOrangeBrush = Brush.horizontalGradient(
    colors = listOf(NeonAmber, NeonOrange)
)

val GlassCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x2E3B3363),
        Color(0x1818122D)
    )
)

val BottomBarGlowBrush = Brush.horizontalGradient(
    colors = listOf(
        NeonCyan.copy(alpha = 0.6f),
        NeonPurple.copy(alpha = 0.8f),
        NeonMagenta.copy(alpha = 0.6f)
    )
)
