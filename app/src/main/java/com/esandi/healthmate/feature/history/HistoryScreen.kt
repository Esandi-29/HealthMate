package com.esandi.healthmate.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esandi.healthmate.ui.components.PlaceholderBody
import com.esandi.healthmate.ui.components.SecondaryButton

/** History tab: past records with date search and filters. Real layout arrives in task 2.4. */
@Composable
fun HistoryScreen(
    onOpenRecord: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderBody(
        largeTitle = "History",
        title = "History",
        message = "Your past records, searchable by date, will appear here (task 2.4).",
        modifier = modifier,
    ) {
        // Lets navigation to the Details screen be tested before real records exist.
        SecondaryButton(text = "Open a sample record", onClick = { onOpenRecord(1L) })
    }
}
