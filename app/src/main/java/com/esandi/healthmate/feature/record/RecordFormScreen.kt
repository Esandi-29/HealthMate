package com.esandi.healthmate.feature.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esandi.healthmate.ui.components.DetailTopBar
import com.esandi.healthmate.ui.components.PlaceholderBody

/**
 * Add or edit a health record. [recordId] is null when adding a new record.
 * Real form arrives in task 2.4.
 */
@Composable
fun RecordFormScreen(
    recordId: Long?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditing = recordId != null
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            DetailTopBar(title = if (isEditing) "Edit record" else "Add health record", onBack = onBack)
            PlaceholderBody(
                title = if (isEditing) "Edit record #$recordId" else "Add record",
                message = "The form with Steps / Calories / Water / Sleep tabs will appear here (task 2.4).",
            )
        }
    }
}
