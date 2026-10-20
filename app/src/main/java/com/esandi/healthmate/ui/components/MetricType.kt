package com.esandi.healthmate.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.esandi.healthmate.ui.theme.HealthMateStyle

/**
 * The four health metrics HealthMate tracks. Each one carries its label, unit and icon,
 * and maps to its theme colour, so every screen shows a metric the same way.
 */
enum class MetricType(val label: String, val unit: String, val icon: ImageVector) {
    STEPS("Steps", "steps", Icons.AutoMirrored.Rounded.DirectionsWalk),
    CALORIES("Calories", "kcal", Icons.Rounded.LocalFireDepartment),
    WATER("Water", "ml", Icons.Rounded.WaterDrop),
    SLEEP("Sleep", "sleep", Icons.Rounded.Bedtime),
}

/** Main colour of a metric (icons, rings, chart bars). */
val MetricType.color: Color
    @Composable @ReadOnlyComposable get() = when (this) {
        MetricType.STEPS -> HealthMateStyle.metrics.steps
        MetricType.CALORIES -> HealthMateStyle.metrics.calories
        MetricType.WATER -> HealthMateStyle.metrics.water
        MetricType.SLEEP -> HealthMateStyle.metrics.sleep
    }

/** Soft background colour of a metric (tiles, selected tabs). */
val MetricType.containerColor: Color
    @Composable @ReadOnlyComposable get() = when (this) {
        MetricType.STEPS -> HealthMateStyle.metrics.stepsContainer
        MetricType.CALORIES -> HealthMateStyle.metrics.caloriesContainer
        MetricType.WATER -> HealthMateStyle.metrics.waterContainer
        MetricType.SLEEP -> HealthMateStyle.metrics.sleepContainer
    }
