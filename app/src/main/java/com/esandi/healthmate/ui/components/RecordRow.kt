package com.esandi.healthmate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.ui.theme.HealthMateStyle
import com.esandi.healthmate.util.Formatters

/** The metric an entry is mainly about: the first one with a value (steps, calories, water, sleep). */
fun HealthRecord.primaryMetric(): MetricType = when {
    steps > 0 -> MetricType.STEPS
    calories > 0 -> MetricType.CALORIES
    waterMl > 0 -> MetricType.WATER
    else -> MetricType.SLEEP
}

/** Human-readable value of one metric in this record, e.g. "2,250 steps", "250 ml", "7h 31m". */
fun HealthRecord.valueText(metric: MetricType): String = when (metric) {
    MetricType.STEPS -> "${Formatters.number(steps)} steps"
    MetricType.CALORIES -> "${Formatters.number(calories)} kcal"
    MetricType.WATER -> "${Formatters.number(waterMl)} ml"
    MetricType.SLEEP -> Formatters.sleep(sleepMinutes)
}

/**
 * One row of an iOS-style grouped list: metric badge, title and time, value, and a chevron.
 * Used for "Recent records" on the Dashboard and the record list in History.
 */
@Composable
fun RecordRow(
    record: HealthRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
    showDate: Boolean = false,
) {
    val metric = record.primaryMetric()
    val whenText = if (showDate) "${Formatters.relativeDay(record.date)} · ${Formatters.time(record.time)}" else Formatters.time(record.time)
    val value = record.valueText(metric)
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .clickable(onClick = onClick)
                .clearAndSetSemantics {
                    contentDescription = "${metric.label}, $value, $whenText"
                    role = Role.Button
                }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricBadge(metric)
            Column(Modifier.weight(1f)) {
                Text(metric.label, style = MaterialTheme.typography.titleSmall)
                Text(whenText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Text(value, style = HealthMateStyle.numbers.small, color = MaterialTheme.colorScheme.onSurface)
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
        if (showDivider) {
            // Inset divider that starts after the badge, as in iOS grouped lists
            HorizontalDivider(
                modifier = Modifier.padding(start = 62.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}
