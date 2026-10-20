package com.esandi.healthmate.feature.record

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esandi.healthmate.data.model.DailyGoals
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.sample.SampleData
import com.esandi.healthmate.ui.components.DetailTopBar
import com.esandi.healthmate.ui.components.EmptyState
import com.esandi.healthmate.ui.components.GoalProgressBar
import com.esandi.healthmate.ui.components.HealthCard
import com.esandi.healthmate.ui.components.MetricBadge
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.PrimaryButton
import com.esandi.healthmate.ui.components.color
import com.esandi.healthmate.ui.components.primaryMetric
import com.esandi.healthmate.ui.theme.HealthMateStyle
import com.esandi.healthmate.ui.theme.HealthMateTheme
import com.esandi.healthmate.util.Formatters

/** Shows one saved record with Edit and Delete (wireframe "Record Details"). */
@Composable
fun RecordDetailsScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordDetailsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.isDeleted) { if (state.isDeleted) onDeleted() }

    RecordDetailsContent(
        state = state,
        onBack = onBack,
        onEdit = onEdit,
        onDeleteClick = viewModel::onDeleteClick,
        modifier = modifier,
    )
    if (state.showDeleteConfirmation && state.record != null) {
        DeleteConfirmationDialog(
            record = state.record!!,
            onConfirm = viewModel::onDeleteConfirm,
            onDismiss = viewModel::onDeleteDismiss,
        )
    }
}

/** Stateless Record Details layout. */
@Composable
fun RecordDetailsContent(
    state: RecordDetailsUiState,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val record = state.record
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            DetailTopBar(title = "Record details", onBack = onBack)
            if (record == null) {
                EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = "Record not found",
                    message = "This record may have been deleted.",
                    actionLabel = "Go back",
                    onAction = onBack,
                )
                return@Column
            }
            val metric = record.primaryMetric()
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column {
                    Text("${metric.label} record", style = MaterialTheme.typography.headlineMedium)
                    Text(Formatters.date(record.date), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                ValueCard(record, metric, state.goals)

                HealthCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 4.dp)) {
                    InfoRow("Date", Formatters.relativeDay(record.date).let { if (it == Formatters.date(record.date)) it else "$it · ${Formatters.date(record.date)}" }, showDivider = true)
                    InfoRow("Time", Formatters.time(record.time), showDivider = true)
                    InfoRow("Source", "Manual entry", showDivider = false)
                }

                HealthCard(Modifier.fillMaxWidth()) {
                    Text("Note", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        record.note ?: "No note added",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (record.note == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                PrimaryButton(text = "Edit record", onClick = { onEdit(record.id) }, icon = Icons.Rounded.Edit)
                // Destructive action: red text, no filled background (iOS convention), and always confirmed.
                TextButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                ) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text("Delete record", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

/** Large value with its badge, plus how much of the daily goal this one entry covers. */
@Composable
private fun ValueCard(record: HealthRecord, metric: MetricType, goals: DailyGoals) {
    val (number, unit, value, goal) = when (metric) {
        MetricType.STEPS -> Quad(Formatters.number(record.steps), "steps", record.steps, goals.steps)
        MetricType.CALORIES -> Quad(Formatters.number(record.calories), "kcal burned", record.calories, goals.calories)
        MetricType.WATER -> Quad(Formatters.number(record.waterMl), "ml of water", record.waterMl, goals.waterMl)
        MetricType.SLEEP -> Quad(Formatters.sleep(record.sleepMinutes), "of sleep", record.sleepMinutes, goals.sleepMinutes)
    }
    HealthCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MetricBadge(metric, size = 48)
                Column(Modifier.padding(start = 14.dp)) {
                    Text(number, style = HealthMateStyle.numbers.hero)
                    Text(unit, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            GoalProgressBar(
                label = "Share of daily goal",
                progress = if (goal > 0) value.toFloat() / goal else 0f,
                color = metric.color,
            )
        }
    }
}

private data class Quad(val number: String, val unit: String, val value: Int, val goal: Int)

@Composable
private fun InfoRow(label: String, value: String, showDivider: Boolean) {
    Column {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (showDivider) {
            HorizontalDivider(Modifier.padding(start = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(record: HealthRecord, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val metric = record.primaryMetric()
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Delete this record?") },
        text = { Text("Your ${metric.label.lowercase()} record from ${Formatters.date(record.date)} will be removed. This can't be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Preview(name = "Details – light", showBackground = true, heightDp = 900)
@Preview(name = "Details – dark", showBackground = true, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun RecordDetailsPreview() {
    HealthMateTheme {
        RecordDetailsContent(
            state = RecordDetailsUiState(record = SampleData.records().first { it.steps > 0 }),
            onBack = {}, onEdit = {}, onDeleteClick = {},
        )
    }
}
