package com.nemo.veloon.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val md_theme_dark_primary = Color(0xFF69DD96)
val md_theme_dark_onPrimary = Color(0xFF00391D)
val md_theme_dark_primaryContainer = Color(0xFF00522D)
val md_theme_dark_secondary = Color(0xFFB6CCB9)
val md_theme_dark_onSecondary = Color(0xFF223527)
val md_theme_dark_secondaryContainer = Color(0xFF384B3D)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)

val wearColorPalette: Colors = Colors(
    primary = md_theme_dark_primary,
    primaryVariant = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_secondary,
    secondaryVariant = md_theme_dark_secondaryContainer,
    error = md_theme_dark_error,
    onPrimary = md_theme_dark_onPrimary,
    onSecondary = md_theme_dark_onSecondary,
    onError = md_theme_dark_onError,
)