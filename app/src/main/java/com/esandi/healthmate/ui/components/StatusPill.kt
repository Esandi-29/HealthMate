package com.esandi.healthmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.theme.GoldenYellow
import com.esandi.healthmate.ui.theme.HealthMateStyle

/** How the user is doing against today's goals; decides the pill's text and colour. */
enum class GoalStatus(val label: String) {
    ON_TRACK("On track"),
    HALFWAY("Halfway there"),
    STARTING("Let's get moving");

    companion object {
        fun from(progress: Float): GoalStatus = when {
            progress >= 0.75f -> ON_TRACK
            progress >= 0.40f -> HALFWAY
            else -> STARTING
        }
    }
}

/**
 * Small rounded status label. Green (palette G1 / S1) for "On track", Golden Yellow (Y1) for
 * "Halfway there": the same yellow and green as the app icon's gradient. Text is always dark on
 * these bright fills so it stays readable (at least 6.9:1).
 */
@Composable
fun StatusPill(status: GoalStatus, modifier: Modifier = Modifier) {
    val darkText = Color(0xFF111114)
    val (background, content) = when (status) {
        GoalStatus.ON_TRACK -> HealthMateStyle.metrics.success to darkText
        GoalStatus.HALFWAY -> GoldenYellow to darkText
        GoalStatus.STARTING -> MaterialTheme.colorScheme.surfaceContainerHigh to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = modifier
            .background(background, CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(6.dp).background(content, CircleShape))
        Text(status.label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = content)
    }
}
