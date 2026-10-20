package com.esandi.healthmate.feature.record

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esandi.healthmate.ui.components.DateField
import com.esandi.healthmate.ui.components.DetailTopBar
import com.esandi.healthmate.ui.components.HealthCard
import com.esandi.healthmate.ui.components.MetricTabs
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.PrimaryButton
import com.esandi.healthmate.ui.components.SecondaryButton
import com.esandi.healthmate.ui.components.color
import com.esandi.healthmate.ui.theme.HealthMateMotion
import com.esandi.healthmate.ui.theme.HealthMateTheme
import com.esandi.healthmate.util.Formatters
import java.time.LocalDate
import java.time.LocalTime

/**
 * Add or edit a health record (brief: "Add Record screen - form with input validation").
 * Connects the form to [RecordFormViewModel] and closes the screen once the record is saved.
 */
@Composable
fun RecordFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordFormViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // When the ViewModel reports a successful save, leave the screen (one-off event).
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    RecordFormContent(
        state = state,
        onBack = onBack,
        onMetricChange = viewModel::onMetricChange,
        onStepsChange = viewModel::onStepsChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onWaterChange = viewModel::onWaterChange,
        onBedtimeChange = viewModel::onBedtimeChange,
        onWakeTimeChange = viewModel::onWakeTimeChange,
        onDateChange = viewModel::onDateChange,
        onNoteChange = viewModel::onNoteChange,
        onSave = viewModel::onSave,
        modifier = modifier,
    )
}

/** Stateless form layout. Every change is passed up to the ViewModel through a callback. */
@Composable
fun RecordFormContent(
    state: RecordFormUiState,
    onBack: () -> Unit,
    onMetricChange: (MetricType) -> Unit,
    onStepsChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onWaterChange: (String) -> Unit,
    onBedtimeChange: (LocalTime) -> Unit,
    onWakeTimeChange: (LocalTime) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            DetailTopBar(title = if (state.isEditing) "Edit record" else "Add health record", onBack = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MetricTabs(selected = state.metric, onSelect = onMetricChange)

                HealthCard(Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Switching tabs cross-fades the panels (see transitionSpec below).
                        AnimatedContent(
                            targetState = state.metric,
                            // iOS-style cross-fade with a slight zoom; the card resizes with a gentle spring.
                            transitionSpec = {
                                (fadeIn(tween(220, delayMillis = 60)) + scaleIn(tween(220, delayMillis = 60), initialScale = 0.97f)) togetherWith
                                    fadeOut(tween(120)) using
                                    SizeTransform(clip = false) { _, _ -> HealthMateMotion.gentle() }
                            },
                            label = "metricPanel",
                        ) { metric ->
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                when (metric) {
                                    MetricType.STEPS -> BigNumberInput(
                                        metric = MetricType.STEPS, value = state.stepsText, onValueChange = onStepsChange,
                                        unitLabel = "steps", sliderMax = 20_000, sliderStep = 50,
                                        caption = "${Formatters.number(state.todayTotals.steps)} already today",
                                        illustration = { FootprintTrail(progress = state.stepsProgress, color = MetricType.STEPS.color) },
                                    )
                                    MetricType.CALORIES -> BigNumberInput(
                                        metric = MetricType.CALORIES, value = state.caloriesText, onValueChange = onCaloriesChange,
                                        unitLabel = "kcal burned", sliderMax = 1_500, sliderStep = 10,
                                        trailing = { CalorieFlame(intensity = state.calorieIntensity) },
                                    )
                                    MetricType.WATER -> {
                                        BigNumberInput(
                                            metric = MetricType.WATER, value = state.waterText, onValueChange = onWaterChange,
                                            unitLabel = "ml of water",
                                            caption = "${Formatters.waterShort(state.waterAfterEntryMl)} of ${Formatters.waterShort(state.goals.waterMl)} today",
                                            trailing = { WaterBottle(fillFraction = state.waterProgress, color = MetricType.WATER.color) },
                                        )
                                        WaterQuickChips(selectedText = state.waterText, onSelect = onWaterChange)
                                    }
                                    MetricType.SLEEP -> SleepInput(
                                        bedtime = state.bedtime, wakeTime = state.wakeTime, sleepMinutes = state.sleepMinutes,
                                        onBedtimeChange = onBedtimeChange, onWakeTimeChange = onWakeTimeChange,
                                    )
                                }
                            }
                        }
                        state.valueError?.let { FieldError(it) }
                    }
                }

                DateField(date = state.date, onDateChange = onDateChange)

                NoteField(note = state.note, error = state.noteError, onNoteChange = onNoteChange)

                PrimaryButton(
                    text = if (state.isEditing) "Update record" else "Save record",
                    onClick = onSave,
                    icon = Icons.Rounded.Check,
                )
                SecondaryButton(text = "Cancel", onClick = onBack)
            }
        }
    }
}

@Composable
private fun NoteField(note: String, error: String?, onNoteChange: (String) -> Unit) {
    TextField(
        value = note,
        onValueChange = onNoteChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Add note (optional)") },
        leadingIcon = { Icon(Icons.Rounded.EditNote, contentDescription = null) },
        maxLines = 3,
        isError = error != null,
        supportingText = { Text(error ?: "${note.length}/${RecordValidator.MAX_NOTE_LENGTH}") },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            errorContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
    )
}

@Preview(name = "Add record – light", showBackground = true, heightDp = 900)
@Preview(name = "Add record – dark", showBackground = true, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun RecordFormPreview() {
    HealthMateTheme {
        RecordFormContent(
            state = RecordFormUiState(stepsText = "4250", valueError = null),
            onBack = {}, onMetricChange = {}, onStepsChange = {}, onCaloriesChange = {}, onWaterChange = {},
            onBedtimeChange = {}, onWakeTimeChange = {}, onDateChange = {}, onNoteChange = {}, onSave = {},
        )
    }
}
