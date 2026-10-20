package com.esandi.healthmate.feature.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esandi.healthmate.ui.components.DetailTopBar
import com.esandi.healthmate.ui.components.PlaceholderBody
import com.esandi.healthmate.ui.components.PrimaryButton

/** Shows one saved record with Edit and Delete actions. Real layout arrives in task 2.4. */
@Composable
fun RecordDetailsScreen(
    recordId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            DetailTopBar(title = "Record details", onBack = onBack)
            PlaceholderBody(
                title = "Record #$recordId",
                message = "Values, date, time and note for this record will appear here (task 2.4).",
            ) {
                PrimaryButton(text = "Edit", onClick = { onEdit(recordId) })
            }
        }
    }
}
