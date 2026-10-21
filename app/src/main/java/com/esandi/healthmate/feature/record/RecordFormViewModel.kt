package com.esandi.healthmate.feature.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmate.data.model.DailyGoals
import com.esandi.healthmate.data.model.DailyTotals
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.repository.HealthRecordRepository
import com.esandi.healthmate.navigation.HealthMateDestination.Companion.RECORD_ID_ARG
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.primaryMetric
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    /** True while the record is being written, so a second tap on Save is ignored. */
    val isSaving: Boolean = false,
    /** Becomes true once the record passed validation and was saved; the screen then closes. */
    val isSaved: Boolean = false,
    /** The record as it was written to the database (used for the confirmation banner). */
    val savedRecord: HealthRecord? = null,
    /** Today's totals before this entry, and the daily goals - used by the animated illustrations. */
    val todayTotals: DailyTotals = DailyTotals(),
    val goals: DailyGoals = DailyGoals(),
) {
    val isEditing: Boolean get() = recordId != null
    val sleepMinutes: Int get() = RecordValidator.sleepMinutes(bedtime, wakeTime)

    private fun entered(text: String) = text.toIntOrNull() ?: 0

    /** Steps today including this entry, as a share of the daily goal (footprint trail). */
    val stepsProgress: Float get() = (todayTotals.steps + entered(stepsText)).toFloat() / goals.steps

    /** Water today including this entry (shown in the caption under the amount). */
    val waterAfterEntryMl: Int get() = todayTotals.waterMl + entered(waterText)

    /** Bottle level: the bottle is 1 L and shows this drink only, so it starts empty. */
    val waterProgress: Float get() = entered(waterText) / BOTTLE_ML.toFloat()

    /** Size of this calorie entry for the flame: 800 kcal or more is a full flame. */
    val calorieIntensity: Float get() = entered(caloriesText) / 800f
}

private const val BOTTLE_ML = 1_000

/**
 * ViewModel for the Add / Edit Record form (MVVM). The screen reports every user action here;
 * the ViewModel updates [uiState], validates on Save, and writes to the Room database
 * through [repository] (insert for a new record, update for an existing one).
 *
 * The record id comes from the navigation route ("record/edit/{recordId}") via [SavedStateHandle];
 * it is absent when adding a new record.
 */
class RecordFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: HealthRecordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordFormUiState())
    val uiState: StateFlow<RecordFormUiState> = _uiState.asStateFlow()

    /** When editing, the record's original time is kept (except for sleep, which uses wake-up time). */
    private var originalTime: LocalTime? = null

    init {
        // Today's totals (live from SQLite) feed the footprint trail and water caption.
        viewModelScope.launch {
            repository.observeTotals(LocalDate.now()).collect { totals ->
                _uiState.update { it.copy(todayTotals = totals) }
            }
        }

        val id = savedStateHandle.get<Long>(RECORD_ID_ARG)
        if (id != null) {
            _uiState.update { it.copy(recordId = id) }
            viewModelScope.launch {
                repository.getRecord(id)?.let { loadForEditing(it) }
            }
        }
    }

    private fun loadForEditing(record: HealthRecord) {
        originalTime = record.time
        val metric = record.primaryMetric()
        _uiState.update { it.copy(
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
        ) }
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
        if (state.isSaving) return
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

        val record = buildRecord(state)
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                if (state.isEditing) repository.update(record) else repository.add(record)
                _uiState.update { it.copy(isSaving = false, isSaved = true, savedRecord = record) }
            } catch (e: Exception) {
                // e.g. the database could not be written; keep the form open so nothing is lost.
                _uiState.update { it.copy(isSaving = false, valueError = "Couldn't save the record. Please try again.") }
            }
        }
    }

    /**
     * Turns the form into a [HealthRecord] for the selected metric only (one row per entry);
     * the other metric columns are 0. A new id of 0 lets SQLite generate the id.
     */
    private fun buildRecord(state: RecordFormUiState): HealthRecord {
        val metric = state.metric
        val time = when {
            metric == MetricType.SLEEP -> state.wakeTime
            else -> originalTime ?: LocalTime.now().withSecond(0).withNano(0)
        }
        return HealthRecord(
            id = state.recordId ?: 0L,
            date = state.date,
            time = time,
            steps = if (metric == MetricType.STEPS) state.stepsText.toIntOrNull() ?: 0 else 0,
            calories = if (metric == MetricType.CALORIES) state.caloriesText.toIntOrNull() ?: 0 else 0,
            waterMl = if (metric == MetricType.WATER) state.waterText.toIntOrNull() ?: 0 else 0,
            sleepMinutes = if (metric == MetricType.SLEEP) state.sleepMinutes else 0,
            note = state.note.trim().ifBlank { null },
        )
    }

    /** Keeps digits only and limits the length, so the field can never hold letters or symbols. */
    private fun digitsOnly(text: String) = text.filter(Char::isDigit).take(6)
}
