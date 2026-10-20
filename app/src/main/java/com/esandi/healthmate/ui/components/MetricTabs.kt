package com.esandi.healthmate.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * iOS-style segmented control for choosing a metric (Steps / Calories / Water / Sleep),
 * as in the Add and Edit record wireframes. One rounded track; the selected segment is a
 * raised "thumb". Each segment is 42 dp tall, so the control meets the 48 dp touch-target guideline.
 */
@Composable
fun MetricTabs(
    selected: MetricType,
    onSelect: (MetricType) -> Unit,
    modifier: Modifier = Modifier,
    metrics: List<MetricType> = MetricType.entries,
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val thumbColor = if (isDark) Color(0xFF5A5A5F) else Color.White
    val segmentShape = RoundedCornerShape(11.dp)

    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Row(
            modifier = Modifier.padding(3.dp).selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            metrics.forEach { metric ->
                val isSelected = metric == selected
                val background by animateColorAsState(if (isSelected) thumbColor else Color.Transparent, label = "segment")
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .then(if (isSelected && !isDark) Modifier.shadow(2.dp, segmentShape) else Modifier)
                        .clip(segmentShape)
                        .background(background)
                        .selectable(selected = isSelected, onClick = { onSelect(metric) }, role = Role.Tab),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).background(metric.color, CircleShape))
                        Text(
                            text = metric.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 5.dp),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
