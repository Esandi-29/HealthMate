package com.esandi.healthmate.feature.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esandi.healthmate.ui.components.PlaceholderBody

/** Progress tab: charts and averages (extra feature, Phase 4). */
@Composable
fun ProgressScreen(modifier: Modifier = Modifier) {
    PlaceholderBody(
        largeTitle = "Progress",
        title = "Progress",
        message = "Day, week and month charts with averages will appear here (task 4.2).",
        modifier = modifier,
    )
}
