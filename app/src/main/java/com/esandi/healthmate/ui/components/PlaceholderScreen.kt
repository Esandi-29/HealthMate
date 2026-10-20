package com.esandi.healthmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TEMPORARY body for screens not built yet (task 2.3). Each feature screen replaces this
 * with its real layout in task 2.4 or Phase 4.
 */
@Composable
fun PlaceholderBody(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    largeTitle: String? = null,
    extra: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (largeTitle != null) LargeTitle(largeTitle)
        HealthCard {
            EmptyState(icon = Icons.Rounded.Construction, title = title, message = message)
        }
        extra()
    }
}
