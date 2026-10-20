package com.esandi.healthmate.feature.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.sample.SampleData
import com.esandi.healthmate.navigation.HealthMateDestination.Companion.RECORD_ID_ARG
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.primaryMetric
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

/** Everything the Add / Edit Record form shows. Text fields hold exactly what the user typed. */
data class RecordFormUiState(
    val recordId: Long? = null,
    val metric: MetricType = MetricType.STEPS,
    val date: LocalDate = LocalDate.now(),
    val stepsText: String = "",
    val caloriesText: String = "",
    val waterText: String = "",
    val bedtime: LocalTime = LocalTime.of(22, 30),
    val wakeTime: LocalTime = LocalTime.of(6, 30),
    val note: String = "",
    /** Validation message for the selected metric, shown after the user taps Save. */
    val valueError: String? = null,
    val noteError: String? = null,
    /** Becomes true once the record passed validation and was saved; the screen then closes. */
    val isSaved: Boolean = false,
) {
    val isEditing: Boolean get() = recordId != null
    val sleepMinutes: Int get() = RecordValidator.sleepMinutes(bedtime, wakeTime)
}

/**
 * ViewModel for the Add / Edit Record form (MVVM). The screen reports every user action here;
 * the ViewModel updates [uiState], validates on Save, and (from Phase 3) writes to the database.
 *
 * The record id comes from the navigation route ("record/edit/{recordId}") via [SavedStateHandle];
 * it is absent when adding a new record.
 */
class RecordFormViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordFormUiState())
    val uiState: StateFlow<RecordFormUiState> = _uiState.asStateFlow()

    init {
        val id = savedStateHandle.get<Long>(RECORD_ID_ARG)
        if (id != null) {
            // Phase 3 (task 3.2): load from the repository instead of sample data.
            SampleData.records().find { it.id == id }?.let { loadForEditing(it) }
                ?: _uiState.update { it.copy(recordId = id) }
        }
    }

    private fun loadForEditing(record: HealthRecord) {
        val metric = record.primaryMetric()
        _uiState.value = RecordFormUiState(
            recordId = record.id,
            metric = metric,
            date = record.date,
            stepsText = record.steps.takeIf { it > 0 }?.toString().orEmpty(),
            caloriesText = record.calories.takeIf { it > 0 }?.toString().orEmpty(),
            waterText = record.waterMl.takeIf { it > 0 }?.toString().orEmpty(),
            // Sleep records are logged at wake-up time, so bedtime = wake-up minus the duration.
            wakeTime = if (metric == MetricType.SLEEP) record.time else LocalTime.of(6, 30),
            bedtime = if (metric == MetricType.SLEEP) record.time.minusMinutes(record.sleepMinutes.toLong()) else LocalTime.of(22, 30),
            note = record.note.orEmpty(),
        )
    }

    fun onMetricChange(metric: MetricType) = _uiState.update { it.copy(metric = metric, valueError = null) }
    fun onDateChange(date: LocalDate) = _uiState.update { it.copy(date = date) }
    fun onStepsChange(text: String) = _uiState.update { it.copy(stepsText = digitsOnly(text), valueError = null) }
    fun onCaloriesChange(text: String) = _uiState.update { it.copy(caloriesText = digitsOnly(text), valueError = null) }
    fun onWaterChange(text: String) = _uiState.update { it.copy(waterText = digitsOnly(text), valueError = null) }
    fun onBedtimeChange(time: LocalTime) = _uiState.update { it.copy(bedtime = time, valueError = null) }
    fun onWakeTimeChange(time: LocalTime) = _uiState.update { it.copy(wakeTime = time, valueError = null) }
    fun onNoteChange(text: String) = _uiState.update {
        it.copy(note = text.take(RecordValidator.MAX_NOTE_LENGTH), noteError = null)
    }

    /** Validates the selected metric, the date and the note. Saves only when everything is valid. */
    fun onSave() {
        val state = _uiState.value
        val valueError = when (state.metric) {
            MetricType.STEPS -> RecordValidator.validateSteps(state.stepsText)
            MetricType.CALORIES -> RecordValidator.validateCalories(state.caloriesText)
            MetricType.WATER -> RecordValidator.validateWater(state.waterText)
            MetricType.SLEEP -> RecordValidator.validateSleep(state.bedtime, state.wakeTime)
        } ?: RecordValidator.validateDate(state.date)
        val noteError = RecordValidator.validateNote(state.note)

        if (valueError != null || noteError != null) {
            _uiState.update { it.copy(valueError = valueError, noteError = noteError) }
            return
        }
        // Phase 3 (task 3.2): insert or update the record in the Room database here.
        _uiState.update { it.copy(isSaved = true) }
    }

    /** Keeps digits only and limits the length, so the field can never hold letters or symbols. */
    private fun digitsOnly(text: String) = text.filter(Char::isDigit).take(6)
}
