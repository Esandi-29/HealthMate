package com.esandi.healthmate.feature.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmate.data.model.DailyGoals
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.repository.HealthRecordRepository
import com.esandi.healthmate.navigation.HealthMateDestination.Companion.RECORD_ID_ARG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** What the Record Details screen shows. [record] is null if the id does not exist. */
data class RecordDetailsUiState(
    val record: HealthRecord? = null,
    val goals: DailyGoals = DailyGoals(),
    /** True until the first database result arrives, so "Record not found" does not flash. */
    val isLoading: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    /** True once the record has been deleted; the screen then closes and offers Undo. */
    val isDeleted: Boolean = false,
)

/**
 * ViewModel for Record Details (MVVM). Reads the record id from the route
 * ("record/details/{recordId}"), observes that row in the database and deletes it on confirm.
 *
 * Because the record is observed (Flow), coming back from Edit shows the new values at once.
 */
class RecordDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: HealthRecordRepository,
) : ViewModel() {

    private val recordId: Long = savedStateHandle.get<Long>(RECORD_ID_ARG) ?: 0L

    private val _uiState = MutableStateFlow(RecordDetailsUiState(isLoading = true))
    val uiState: StateFlow<RecordDetailsUiState> = _uiState.asStateFlow()

    /** Set while deleting, so the row disappearing from the database is not shown as "not found". */
    private var deleting = false

    init {
        viewModelScope.launch {
            repository.observeRecord(recordId).collect { record ->
                if (!deleting) _uiState.update { it.copy(record = record, isLoading = false) }
            }
        }
    }

    fun onDeleteClick() = _uiState.update { it.copy(showDeleteConfirmation = true) }

    fun onDeleteDismiss() = _uiState.update { it.copy(showDeleteConfirmation = false) }

    fun onDeleteConfirm() {
        val record = _uiState.value.record ?: return
        deleting = true
        _uiState.update { it.copy(showDeleteConfirmation = false) }
        viewModelScope.launch {
            repository.delete(record)
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}
