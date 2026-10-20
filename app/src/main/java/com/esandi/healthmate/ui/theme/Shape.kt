package com.esandi.healthmate.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Soft, rounded shapes matching the wireframes and iOS-style cards (pill buttons, rounded cards). */
val HealthMateShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // small chips, text-field corners
    small = RoundedCornerShape(12.dp),       // filter chips, snackbars
    medium = RoundedCornerShape(18.dp),      // metric tiles
    large = RoundedCornerShape(22.dp),       // main cards, bottom sheets
    extraLarge = RoundedCornerShape(32.dp),  // hero card, dialogs
)
