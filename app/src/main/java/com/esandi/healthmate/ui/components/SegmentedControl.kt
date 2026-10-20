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
 * Metric picker for the Add and Edit record wireframes (Steps / Calories / Water / Sleep),
 * built on the shared [SegmentedControl]; a coloured dot links each option to its metric.
 */
@Composable
fun MetricTabs(
    selected: MetricType,
    onSelect: (MetricType) -> Unit,
    modifier: Modifier = Modifier,
    metrics: List<MetricType> = MetricType.entries,
) {
    SegmentedControl(
        options = metrics,
        selected = selected,
        onSelect = onSelect,
        label = { it.label },
        modifier = modifier,
        dotColor = { it.color },
    )
}

/**
 * iOS-style segmented control: one rounded track with a raised "thumb" that slides to the
 * selected option with a spring, plus a light haptic tick on change. Used for the metric picker
 * and the History date filter. Each segment is 42 dp tall (48 dp touch target with the track).
 */
@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    dotColor: (@Composable (T) -> Color)? = null,
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val thumbColor = if (isDark) Color(0xFF5A5A5F) else Color.White
    val segmentShape = RoundedCornerShape(11.dp)
    val haptic = LocalHapticFeedback.current
    val gap = 2.dp

    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        BoxWithConstraints(Modifier.padding(3.dp)) {
            val segmentWidth = (maxWidth - gap * (options.size - 1)) / options.size
            val index = options.indexOf(selected).coerceAtLeast(0)
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
                options.forEach { option ->
                    val isSelected = option == selected
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
                                    onSelect(option)
                                },
                                role = Role.Tab,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (dotColor != null) {
                                Box(Modifier.size(7.dp).background(dotColor(option), CircleShape))
                            }
                            Text(
                                text = label(option),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier.padding(start = if (dotColor != null) 5.dp else 0.dp),
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}
