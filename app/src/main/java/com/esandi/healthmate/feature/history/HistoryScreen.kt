package com.esandi.healthmate.feature.history

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esandi.healthmate.di.AppViewModelProvider
import com.esandi.healthmate.data.model.DailyTotals
import com.esandi.healthmate.data.sample.SampleData
import com.esandi.healthmate.ui.components.EmptyState
import com.esandi.healthmate.ui.components.HealthCard
import com.esandi.healthmate.ui.components.HealthDatePickerDialog
import com.esandi.healthmate.ui.components.LargeTitle
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.RecordRow
import com.esandi.healthmate.ui.components.SegmentedControl
import com.esandi.healthmate.ui.components.color
import com.esandi.healthmate.ui.theme.HealthMateTheme
import com.esandi.healthmate.util.Formatters
import java.time.LocalDate

/**
 * History tab: every past record, grouped by day, with search and filters by date
 * (brief: "Health Record List screen - list with search by date").
 */
@Composable
fun HistoryScreen(
    onOpenRecord: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryContent(
        state = state,
        onRangeChange = viewModel::onRangeChange,
        onDateSelected = viewModel::onDateSelected,
        onClearDate = viewModel::onClearDate,
        onMetricChange = viewModel::onMetricChange,
        onClearAll = viewModel::onClearAll,
        onOpenRecord = onOpenRecord,
        modifier = modifier,
    )
}

/** Stateless History layout. */
@Composable
fun HistoryContent(
    state: HistoryUiState,
    onRangeChange: (DateRange) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onClearDate: () -> Unit,
    onMetricChange: (MetricType?) -> Unit,
    onClearAll: () -> Unit,
    onOpenRecord: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = state.filters
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { LargeTitle("History") }
        item {
            SegmentedControl(
                options = DateRange.entries,
                selected = filters.range,
                onSelect = onRangeChange,
                label = { it.label },
            )
        }
        item { DateSearchField(date = filters.date, onDateSelected = onDateSelected, onClear = onClearDate) }
        item { MetricFilterRow(selected = filters.metric, onSelect = onMetricChange) }
        item {
            Row(Modifier.fillMaxWidth().heightIn(min = 40.dp), verticalAlignment = Alignment.CenterVertically) {
                val what = if (state.resultCount == 1) "record" else "records"
                val onDate = filters.date?.let { " on ${Formatters.date(it)}" }.orEmpty()
                Text(
                    "${state.resultCount} $what$onDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                if (state.hasActiveFilters) {
                    TextButton(onClick = onClearAll) { Text("Clear filters") }
                }
            }
        }

        if (state.isLoading) {
            // Nothing to show yet; the first database result arrives in a few milliseconds.
        } else if (state.groups.isEmpty()) {
            item {
                HealthCard(Modifier.fillMaxWidth()) {
                    EmptyState(
                        icon = Icons.Rounded.EventBusy,
                        title = "No records found",
                        message = "Nothing matches these filters. Try another date or clear the filters.",
                        actionLabel = "Clear filters",
                        onAction = onClearAll,
                    )
                }
            }
        } else {
            state.groups.forEach { group ->
                item(key = "header-${group.date}") { DayHeader(group) }
                item(key = "group-${group.date}") {
                    HealthCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 4.dp)) {
                        group.records.forEachIndexed { index, record ->
                            RecordRow(
                                record = record,
                                onClick = { onOpenRecord(record.id) },
                                showDivider = index < group.records.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** "Search by date" field: opens the calendar; shows the chosen date with a clear (×) button. */
@Composable
private fun DateSearchField(date: LocalDate?, onDateSelected: (LocalDate) -> Unit, onClear: () -> Unit) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { showPicker = true },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.heightIn(min = 52.dp).padding(start = 14.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = date?.let { Formatters.date(it) } ?: "Search by date",
                style = MaterialTheme.typography.bodyLarge,
                color = if (date == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f).padding(start = 10.dp),
            )
            if (date != null) {
                IconButton(onClick = onClear) { Icon(Icons.Rounded.Close, contentDescription = "Clear date") }
            } else {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
    if (showPicker) {
        HealthDatePickerDialog(
            initialDate = date,
            onDismiss = { showPicker = false },
            onConfirm = {
                onDateSelected(it)
                showPicker = false
            },
        )
    }
}

/** Chips to show only one kind of metric (All / Steps / Calories / Water / Sleep). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricFilterRow(selected: MetricType?, onSelect: (MetricType?) -> Unit) {
    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text("All metrics") },
            colors = chipColors,
            border = null,
            shape = CircleShape,
        )
        MetricType.entries.forEach { metric ->
            FilterChip(
                selected = selected == metric,
                onClick = { onSelect(if (selected == metric) null else metric) },
                label = { Text(metric.label) },
                leadingIcon = { Box(Modifier.size(8.dp).background(metric.color, CircleShape)) },
                colors = chipColors,
                border = null,
                shape = CircleShape,
            )
        }
    }
}

/** Day heading above each group, e.g. "Yesterday · 7,850 steps · 2.0 L". */
@Composable
private fun DayHeader(group: DayGroup) {
    val totals = DailyTotals.of(group.records)
    val summary = listOfNotNull(
        totals.steps.takeIf { it > 0 }?.let { "${Formatters.number(it)} steps" },
        totals.calories.takeIf { it > 0 }?.let { "${Formatters.number(it)} kcal" },
        totals.waterMl.takeIf { it > 0 }?.let { Formatters.waterShort(it) },
        totals.sleepMinutes.takeIf { it > 0 }?.let { Formatters.sleep(it) },
    ).joinToString(" · ")
    Row(Modifier.fillMaxWidth().padding(top = 8.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.Bottom) {
        Text(Formatters.relativeDay(group.date), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(summary, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
    }
}

@Preview(name = "History – light", showBackground = true, heightDp = 1100)
@Preview(name = "History – dark", showBackground = true, heightDp = 1100, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun HistoryPreview() {
    val matching = HistoryFilter.apply(SampleData.records(), HistoryFilters(range = DateRange.WEEK))
    HealthMateTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HistoryContent(
                state = HistoryUiState(HistoryFilters(range = DateRange.WEEK), HistoryFilter.groupByDay(matching), matching.size),
                onRangeChange = {}, onDateSelected = {}, onClearDate = {}, onMetricChange = {}, onClearAll = {}, onOpenRecord = {},
            )
        }
    }
}
