package com.esandi.healthmate.feature.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esandi.healthmate.data.model.DailyTotals
import com.esandi.healthmate.data.sample.SampleData
import com.esandi.healthmate.ui.components.EmptyState
import com.esandi.healthmate.ui.components.GoalProgressBar
import com.esandi.healthmate.ui.components.GoalStatus
import com.esandi.healthmate.ui.components.HealthCard
import com.esandi.healthmate.ui.components.MetricCard
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.ProgressRing
import com.esandi.healthmate.ui.components.RecordRow
import com.esandi.healthmate.ui.components.SectionHeader
import com.esandi.healthmate.ui.components.StatusPill
import com.esandi.healthmate.ui.components.color
import com.esandi.healthmate.ui.theme.HealthMateStyle
import com.esandi.healthmate.ui.theme.HealthMateTheme
import com.esandi.healthmate.util.Formatters
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

/**
 * Home tab: a summary of today's activity (brief: "Show a simple summary dashboard").
 * This function connects the screen to its ViewModel; [DashboardContent] does the drawing.
 */
@Composable
fun DashboardScreen(
    onAddRecord: () -> Unit,
    onOpenRecord: (Long) -> Unit,
    onSeeAllRecords: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(state, onAddRecord, onOpenRecord, onSeeAllRecords, modifier)
}

/** Stateless Dashboard layout: draws [state] and reports taps through the callbacks. */
@Composable
fun DashboardContent(
    state: DashboardUiState,
    onAddRecord: () -> Unit,
    onOpenRecord: (Long) -> Unit,
    onSeeAllRecords: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Greeting(state.userName, state.today) }
        item { WeekStrip(state.week, state.today, state.daysWithRecords) }
        item { StepsCard(state) }
        item { TodayTiles(state.totals) }
        item {
            HealthCard(Modifier.fillMaxWidth()) {
                // Filled with the app icon's gradient (amber -> pink -> violet -> blue -> mint)
                GoalProgressBar(
                    label = "Today's goal",
                    progress = state.overallProgress,
                    brush = Brush.horizontalGradient(HealthMateStyle.metrics.brandGradient),
                    trailing = { StatusPill(GoalStatus.from(state.overallProgress)) },
                )
            }
        }
        item { SectionHeader("Latest records", actionLabel = "See all", onAction = onSeeAllRecords) }
        item {
            HealthCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 4.dp)) {
                if (state.recentRecords.isEmpty()) {
                    EmptyState(
                        icon = Icons.Rounded.CalendarMonth,
                        title = "No records yet",
                        message = "Add your first entry to start tracking your day.",
                        actionLabel = "Add record",
                        onAction = onAddRecord,
                    )
                } else {
                    state.recentRecords.forEachIndexed { index, record ->
                        RecordRow(
                            record = record,
                            onClick = { onOpenRecord(record.id) },
                            showDate = true,
                            showDivider = index < state.recentRecords.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Greeting(name: String, today: LocalDate) {
    val hour = LocalTime.now().hour
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
    Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f).padding(top = 8.dp)) {
            Text(Formatters.date(today), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$greeting,", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(name, style = MaterialTheme.typography.displaySmall)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StepsCard(state: DashboardUiState) {
    val steps = MetricType.STEPS
    HealthCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 20.dp, horizontal = 16.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            ProgressRing(progress = state.stepsProgress, color = steps.color, size = 196.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(steps.icon, contentDescription = null, tint = steps.color)
                    Text(Formatters.number(state.totals.steps), style = HealthMateStyle.numbers.hero)
                    Text(
                        "of ${Formatters.number(state.goals.steps)} steps",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "${(state.stepsProgress * 100).roundToInt()}% of your daily step goal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TodayTiles(totals: DailyTotals) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard(MetricType.CALORIES, Formatters.number(totals.calories), "kcal burned", Modifier.weight(1f))
        MetricCard(MetricType.WATER, Formatters.waterShort(totals.waterMl), "water", Modifier.weight(1f))
        MetricCard(MetricType.SLEEP, Formatters.sleep(totals.sleepMinutes), "sleep", Modifier.weight(1f))
    }
}

@Preview(name = "Dashboard – light", showBackground = true, heightDp = 1100)
@Preview(name = "Dashboard – dark", showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun DashboardPreview() {
    val today = LocalDate.now()
    val records = SampleData.records(today)
    val state = DashboardUiState(
        today = today,
        week = (0L..6L).map { today.minusDays(today.dayOfWeek.value - 1L).plusDays(it) },
        daysWithRecords = records.map { it.date }.toSet(),
        totals = DailyTotals.of(records.filter { it.date == today }),
        recentRecords = DashboardViewModel.latestPerMetric(records),
    )
    HealthMateTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardContent(state, onAddRecord = {}, onOpenRecord = {}, onSeeAllRecords = {})
        }
    }
}
