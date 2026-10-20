package com.esandi.healthmate.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.components.ComponentGallery
import com.esandi.healthmate.ui.components.DetailTopBar
import com.esandi.healthmate.ui.components.MetricCard
import com.esandi.healthmate.ui.components.MetricType

/**
 * TEMPORARY design-system preview (task 2.2).
 * Shows the HealthMate colours, metric colours, fonts and buttons in one place so the theme
 * can be checked in light and dark mode. Replaced by the real Dashboard in task 2.4,
 * but kept in the project as a living style guide for the report's UI/UX section.
 */
@Composable
fun ThemeShowcaseScreen(modifier: Modifier = Modifier, onBack: (() -> Unit)? = null) {
    Scaffold(
        modifier = modifier,
        topBar = { if (onBack != null) DetailTopBar(title = "Design system", onBack = onBack) },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { ShowcaseHeader() }
            item { MetricSection() }
            item { ComponentGallery() }
            item { ColourSection() }
            item { TypeSection() }
            item { ControlsSection() }
        }
    }
}

@Composable
private fun ShowcaseHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Good evening,",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = "HealthMate", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Brush.horizontalGradient(HealthMateStyle.metrics.brandGradient), CircleShape),
        )
        Text(
            text = "Small steps for a healthier you. This screen previews the theme: colours, fonts and controls.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MetricSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Metric colours")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(MetricType.STEPS, "7,850", "steps", Modifier.weight(1f))
            MetricCard(MetricType.CALORIES, "420", "kcal burned", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(MetricType.WATER, "2,000", "ml water", Modifier.weight(1f))
            MetricCard(MetricType.SLEEP, "7h 31m", "sleep", Modifier.weight(1f))
        }
    }
}

@Composable
private fun ColourSection() {
    val c = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Colour scheme")
        SwatchRow("Primary" to c.primary, "Secondary" to c.secondary)
        SwatchRow("Tertiary" to c.tertiary, "Error" to c.error)
        SwatchRow("Background" to c.background, "Surface" to c.surfaceContainer)
    }
}

@Composable
private fun SwatchRow(first: Pair<String, Color>, second: Pair<String, Color>) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Swatch(first.first, first.second, Modifier.weight(1f))
        Swatch(second.first, second.second, Modifier.weight(1f))
    }
}

@Composable
private fun Swatch(name: String, color: Color, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier
                .size(36.dp)
                .background(color, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        )
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TypeSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Typography")
        Text("Headline · Plus Jakarta Sans", style = MaterialTheme.typography.headlineSmall)
        Text("12,480", style = HealthMateStyle.numbers.hero, color = HealthMateStyle.metrics.steps)
        Text("Numbers · Inter (tabular figures)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Title and labels · Roboto", style = MaterialTheme.typography.titleMedium)
        Text(
            "Paragraph · Inter. You walked 1,240 more steps than yesterday. Keep it up to reach your weekly goal.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Controls")
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Save record") }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Add note") }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Cancel") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = true, onClick = {}, label = { Text("Today") })
            FilterChip(selected = false, onClick = {}, label = { Text("7 days") })
            FilterChip(selected = false, onClick = {}, label = { Text("30 days") })
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge)
}

@Preview(name = "Light", showBackground = true, heightDp = 1400)
@Preview(name = "Dark", showBackground = true, heightDp = 1400, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ThemeShowcasePreview() {
    HealthMateTheme { ThemeShowcaseScreen() }
}
