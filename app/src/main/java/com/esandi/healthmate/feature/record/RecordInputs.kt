package com.esandi.healthmate.feature.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.esandi.healthmate.ui.components.MetricBadge
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.color
import com.esandi.healthmate.ui.components.containerColor
import com.esandi.healthmate.ui.theme.HealthMateStyle
import com.esandi.healthmate.util.Formatters
import java.time.LocalTime
import kotlin.math.roundToInt

/**
 * Large number entry for steps, calories or water: the value is typed with the number keyboard
 * and shown in big Inter figures, like iOS Health. An optional slider (the wireframe's "ruler")
 * lets the user drag to a value instead of typing.
 */
@Composable
fun BigNumberInput(
    metric: MetricType,
    value: String,
    onValueChange: (String) -> Unit,
    unitLabel: String,
    modifier: Modifier = Modifier,
    sliderMax: Int? = null,
    sliderStep: Int = 1,
    caption: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    illustration: (@Composable () -> Unit)? = null,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MetricBadge(metric, size = 44)
            Column(Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = HealthMateStyle.numbers.hero.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(metric.color),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "${metric.label} in $unitLabel" },
                    decorationBox = { inner ->
                        Box {
                            if (value.isEmpty()) {
                                Text("0", style = HealthMateStyle.numbers.hero, color = MaterialTheme.colorScheme.outline)
                            }
                            inner()
                        }
                    },
                )
                Text(unitLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (caption != null) {
                    Text(caption, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (trailing != null) trailing()
        }
        if (illustration != null) illustration()
        if (sliderMax != null) {
            val current = value.toIntOrNull()?.coerceIn(0, sliderMax) ?: 0
            GradientSlider(
                value = current.toFloat(),
                onValueChange = { raw ->
                    val snapped = (raw / sliderStep).roundToInt() * sliderStep
                    onValueChange(snapped.toString())
                },
                valueRange = 0f..sliderMax.toFloat(),
                color = metric.color,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(Formatters.number(sliderMax), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Water quick amounts from the wireframe: 250 ml, 500 ml, 750 ml and 1 L. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterQuickChips(selectedText: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    val metric = MetricType.WATER
    val haptic = LocalHapticFeedback.current
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(250, 500, 750, 1000).forEach { ml ->
            FilterChip(
                selected = selectedText == ml.toString(),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelect(ml.toString())
                },
                label = { Text(if (ml == 1000) "1 L" else "$ml ml") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = metric.containerColor,
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Sleep entry: bedtime and wake-up tiles that open time pickers, with the duration shown large. */
@Composable
fun SleepInput(
    bedtime: LocalTime,
    wakeTime: LocalTime,
    sleepMinutes: Int,
    onBedtimeChange: (LocalTime) -> Unit,
    onWakeTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val metric = MetricType.SLEEP
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBadge(metric, size = 44)
                Text(Formatters.sleep(sleepMinutes), style = HealthMateStyle.numbers.hero)
                Text("time asleep", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            SleepDial(bedtime = bedtime, wakeTime = wakeTime, color = metric.color)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimeTile("Bedtime", Icons.Rounded.Bedtime, bedtime, onBedtimeChange, Modifier.weight(1f))
            TimeTile("Wake up", Icons.Rounded.WbSunny, wakeTime, onWakeTimeChange, Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeTile(
    label: String,
    icon: ImageVector,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { showPicker = true },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, contentDescription = null, tint = MetricType.SLEEP.color, modifier = Modifier.padding(end = 2.dp))
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(Formatters.time(time), style = HealthMateStyle.numbers.medium)
        }
    }
    if (showPicker) {
        val state = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute, is24Hour = false)
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(label) },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(LocalTime.of(state.hour, state.minute))
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } },
        )
    }
}

/** Validation message under an input, announced by screen readers when it appears. */
@Composable
fun FieldError(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
}
