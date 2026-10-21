package com.esandi.healthmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.repository.HealthRecordRepository
import kotlinx.coroutines.launch

/**
 * App-level ViewModel used by the app shell ([HealthMateApp]).
 *
 * The "Record deleted - Undo" message is shown after the Details screen has already closed,
 * so its action cannot belong to that screen's ViewModel (which no longer exists by then).
 * This ViewModel lives as long as the app's main screen, so Undo still works.
 */
class HealthMateAppViewModel(
    private val repository: HealthRecordRepository,
) : ViewModel() {

    fun undoDelete(record: HealthRecord) {
        viewModelScope.launch { repository.restore(record) }
    }
}
