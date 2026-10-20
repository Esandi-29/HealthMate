package com.esandi.healthmate.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esandi.healthmate.ui.components.PlaceholderBody
import com.esandi.healthmate.ui.components.PrimaryButton

/** Home tab: today's summary. Real layout arrives in task 2.4. */
@Composable
fun DashboardScreen(
    onAddRecord: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderBody(
        largeTitle = "Today",
        title = "Dashboard",
        message = "Today's steps ring, calories, water and sleep will appear here (task 2.4).",
        modifier = modifier,
    ) {
        PrimaryButton(text = "Add a record", onClick = onAddRecord)
    }
}
