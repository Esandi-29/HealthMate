package com.esandi.healthmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.util.Formatters
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * A tappable row showing a date ("Tue, 27 Oct 2026") that opens Material's calendar picker.
 * Future dates are blocked by default: a health record cannot be logged for tomorrow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Date",
    allowFutureDates: Boolean = false,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = { showPicker = true },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(Formatters.date(date), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Change date", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (showPicker) {
        HealthDatePickerDialog(
            initialDate = date,
            allowFutureDates = allowFutureDates,
            onDismiss = { showPicker = false },
            onConfirm = {
                onDateChange(it)
                showPicker = false
            },
        )
    }
}

/**
 * Material calendar picker in a dialog, shared by [DateField] and the History date search.
 * Future dates are disabled unless [allowFutureDates] is true.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDatePickerDialog(
    initialDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    allowFutureDates: Boolean = false,
) {
    val todayUtcMillis = remember { LocalDate.now().toUtcMillis() }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = (initialDate ?: LocalDate.now()).toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                allowFutureDates || utcTimeMillis <= todayUtcMillis
        },
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { pickerState.selectedDateMillis?.let { onConfirm(it.toLocalDate()) } ?: onDismiss() }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) {
        DatePicker(state = pickerState)
    }
}

/** The date picker works in UTC milliseconds; these convert to and from LocalDate. */
private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
