package com.esandi.healthmate.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.theme.HealthMateStyle
import com.esandi.healthmate.util.Formatters
import java.time.LocalDate
import java.util.Locale
import java.time.format.TextStyle as JavaTextStyle

/**
 * This week's days (Mon-Sun) from the Dashboard wireframe. Today is highlighted;
 * a mint-green dot (as in the app icon) marks days that have records; future days are dimmed.
 */
@Composable
fun WeekStrip(
    week: List<LocalDate>,
    today: LocalDate,
    daysWithRecords: Set<LocalDate>,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        week.forEach { day -> DayChip(day, isToday = day == today, isFuture = day.isAfter(today), hasRecords = day in daysWithRecords) }
    }
}

@Composable
private fun DayChip(day: LocalDate, isToday: Boolean, isFuture: Boolean, hasRecords: Boolean) {
    val scheme = MaterialTheme.colorScheme
    val background = if (isToday) scheme.inverseSurface else Color.Transparent
    val content = if (isToday) scheme.inverseOnSurface else scheme.onSurface
    val label = day.dayOfWeek.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
    Column(
        modifier = Modifier
            .width(44.dp)
            .background(background, RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp)
            .alpha(if (isFuture) 0.4f else 1f)
            .clearAndSetSemantics {
                contentDescription = Formatters.date(day) + (if (isToday) ", today" else "") + (if (hasRecords) ", has records" else "")
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (isToday) content else scheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(day.dayOfMonth.toString(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = content)
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .size(4.dp)
                .background(if (hasRecords) HealthMateStyle.metrics.success else Color.Transparent, CircleShape),
        )
    }
}
