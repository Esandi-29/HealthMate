package com.esandi.healthmate.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esandi.healthmate.ui.components.PlaceholderBody
import com.esandi.healthmate.ui.components.SecondaryButton

/** Profile tab: name, goals and settings (Phase 4). Also links to the design-system page. */
@Composable
fun ProfileScreen(
    onOpenDesignSystem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderBody(
        largeTitle = "Profile",
        title = "Profile & settings",
        message = "Personal details, daily goals and theme settings will appear here (task 4.2).",
        modifier = modifier,
    ) {
        SecondaryButton(text = "View design system", onClick = onOpenDesignSystem)
    }
}
