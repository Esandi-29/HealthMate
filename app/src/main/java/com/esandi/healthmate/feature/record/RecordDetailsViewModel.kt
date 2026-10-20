package com.esandi.healthmate.feature.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.esandi.healthmate.data.model.DailyGoals
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.sample.SampleData
import com.esandi.healthmate.navigation.HealthMateDestination.Companion.RECORD_ID_ARG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** What the Record Details screen shows. [record] is null if the id does not exist. */
data class RecordDetailsUiState(
    val record: HealthRecord? = null,
    val goals: DailyGoals = DailyGoals(),
    val showDeleteConfirmation: Boolean = false,
    /** True once the record has been deleted; the screen then closes. */
    val isDeleted: Boolean = false,
)

/**
 * ViewModel for Record Details (MVVM). Reads the record id from the route
 * ("record/details/{recordId}") and handles the delete confirmation.
 * Phase 3 (task 3.2): load and delete through the Room repository instead of sample data.
 */
class RecordDetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val recordId: Long = savedStateHandle.get<Long>(RECORD_ID_ARG) ?: 0L

    private val _uiState = MutableStateFlow(
        RecordDetailsUiState(record = SampleData.records().find { it.id == recordId }),
    )
    val uiState: StateFlow<RecordDetailsUiState> = _uiState.asStateFlow()

    fun onDeleteClick() = _uiState.update { it.copy(showDeleteConfirmation = true) }

    fun onDeleteDismiss() = _uiState.update { it.copy(showDeleteConfirmation = false) }

    fun onDeleteConfirm() {
        // Phase 3 (task 3.2): repository.delete(record), with an Undo snackbar on the previous screen.
        _uiState.update { it.copy(showDeleteConfirmation = false, isDeleted = true) }
    }
}
