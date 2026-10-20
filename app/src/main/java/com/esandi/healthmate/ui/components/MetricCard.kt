package com.esandi.healthmate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.theme.HealthMateStyle

/**
 * Compact tile showing one metric value, e.g. "420 kcal". Used on the Dashboard and Progress.
 * A neutral card with a coloured icon badge keeps the screen calm while each metric stays
 * recognisable by colour. Screen readers read it as one sentence ("Calories: 420 kcal").
 */
@Composable
fun MetricCard(
    metric: MetricType,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val content: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .clearAndSetSemantics { contentDescription = "${metric.label}: $value $caption" },
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            MetricBadge(metric)
            Spacer(Modifier.height(8.dp))
            Text(value, style = HealthMateStyle.numbers.medium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Text(caption, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
    val cardColor = metricCardSurface()
    if (onClick != null) {
        Surface(onClick = onClick, modifier = modifier, shape = MaterialTheme.shapes.medium, color = cardColor, content = content)
    } else {
        Surface(modifier = modifier, shape = MaterialTheme.shapes.medium, color = cardColor, content = content)
    }
}

/** Round badge with the metric's icon on its soft colour. */
@Composable
fun MetricBadge(metric: MetricType, modifier: Modifier = Modifier, size: Int = 34) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(metric.containerColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(metric.icon, contentDescription = null, tint = metric.color, modifier = Modifier.size((size * 0.56f).dp))
    }
}

/** Card colour: white on the grey page (light), elevated grey on black (dark). */
@Composable
internal fun metricCardSurface(): Color = MaterialTheme.colorScheme.surfaceContainer
