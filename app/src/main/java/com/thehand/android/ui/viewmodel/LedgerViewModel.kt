package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.EntryType
import com.thehand.android.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LedgerUiState(
    val entries: List<Entry> = emptyList(),
    val filter: EntryType? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _filter = MutableStateFlow<EntryType?>(null)

    val uiState: StateFlow<LedgerUiState> = combine(
        _filter,
        entryRepository.getAllEntries()
    ) { filter, allEntries ->
        val filtered = if (filter != null) {
            allEntries.filter { it.type == filter }
        } else {
            allEntries
        }

        LedgerUiState(
            entries = filtered.map { it.lockIfExpired() },
            filter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LedgerUiState(isLoading = true)
    )

    fun setFilter(type: EntryType?) {
        _filter.value = type
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            entryRepository.deleteEntry(entry)
        }
    }
}
