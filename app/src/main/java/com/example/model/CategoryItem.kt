package com.example.model

data class CategoryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val drawableRes: Int? = null,
    val gradientColors: List<Long> = listOf(0xFF8C52FF, 0xFF00D2FF),
    val themeCount: String = "240+ thèmes"
)
