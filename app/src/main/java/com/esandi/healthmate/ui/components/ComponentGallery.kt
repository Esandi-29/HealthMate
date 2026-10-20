package com.esandi.healthmate.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.theme.HealthMateStyle
import com.esandi.healthmate.ui.theme.HealthMateTheme
import java.time.LocalDate

/**
 * Every reusable component with sample values (task 2.3). Shown on the theme preview screen
 * and in Android Studio's preview, so components can be checked before they are used in screens.
 */
@Composable
fun ComponentGallery(modifier: Modifier = Modifier) {
    var selectedMetric by rememberSaveable { mutableStateOf(MetricType.STEPS) }
    var date by rememberSaveable { mutableStateOf(LocalDate.now()) }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Components", actionLabel = "See all", onAction = {})

        HealthCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ProgressRing(progress = 0.53f, color = MetricType.STEPS.color, size = 190.dp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(MetricType.STEPS.icon, contentDescription = null, tint = MetricType.STEPS.color)
                        Text("4,250", style = HealthMateStyle.numbers.hero)
                        Text("of 8,000 steps", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(MetricType.CALORIES, "320", "kcal", Modifier.weight(1f))
            MetricCard(MetricType.WATER, "1.2 L", "water", Modifier.weight(1f))
            MetricCard(MetricType.SLEEP, "7h 31m", "sleep", Modifier.weight(1f))
        }

        GoalProgressBar(label = "Today's goal", progress = 0.53f)

        MetricTabs(selected = selectedMetric, onSelect = { selectedMetric = it })

        DateField(date = date, onDateChange = { date = it })

        PrimaryButton(text = "Save record", onClick = {})
        SecondaryButton(text = "Cancel", onClick = {})

        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerLow) {
            EmptyState(
                icon = Icons.Rounded.CalendarMonth,
                title = "No records yet",
                message = "Add your first health record to see it here.",
                actionLabel = "Add record",
                onAction = {},
            )
        }
    }
}

@Preview(name = "Components – light", showBackground = true, heightDp = 1300)
@Preview(name = "Components – dark", showBackground = true, heightDp = 1300, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ComponentGalleryPreview() {
    HealthMateTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ComponentGallery(Modifier.padding(20.dp))
        }
    }
}
