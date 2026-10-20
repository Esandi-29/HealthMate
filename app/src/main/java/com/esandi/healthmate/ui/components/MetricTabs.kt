package com.esandi.healthmate.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.theme.HealthMateMotion

/**
 * iOS-style segmented control for choosing a metric (Steps / Calories / Water / Sleep),
 * as in the Add and Edit record wireframes. One rounded track with a raised "thumb" that
 * slides to the selected segment with a spring, plus a light haptic tick on change.
 * Each segment is 42 dp tall, so the control meets the 48 dp touch-target guideline.
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
    val haptic = LocalHapticFeedback.current
    val gap = 2.dp

    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        BoxWithConstraints(Modifier.padding(3.dp)) {
            val segmentWidth = (maxWidth - gap * (metrics.size - 1)) / metrics.size
            val index = metrics.indexOf(selected).coerceAtLeast(0)
            val thumbOffset by animateDpAsState(
                targetValue = (segmentWidth + gap) * index,
                animationSpec = HealthMateMotion.snappy(),
                label = "segmentThumb",
            )
            // The sliding thumb, drawn under the labels
            Box(
                Modifier
                    .offset(x = thumbOffset)
                    .width(segmentWidth)
                    .height(42.dp)
                    .then(if (!isDark) Modifier.shadow(2.dp, segmentShape) else Modifier)
                    .clip(segmentShape)
                    .background(thumbColor),
            )
            Row(
                modifier = Modifier.fillMaxWidth().selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                metrics.forEach { metric ->
                    val isSelected = metric == selected
                    val textColor by animateColorAsState(
                        if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "segmentText",
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(segmentShape)
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelect(metric)
                                },
                                role = Role.Tab,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(7.dp).background(metric.color, CircleShape))
                            Text(
                                text = metric.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier.padding(start = 5.dp),
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}
