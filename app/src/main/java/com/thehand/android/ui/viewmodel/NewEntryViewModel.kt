package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.EntryType
import com.thehand.android.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class NewEntryUiState(
    val type: EntryType = EntryType.BUILT,
    val whoWhat: String = "",
    val whatCost: String = "",
    val whatDifferently: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class NewEntryViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewEntryUiState())
    val uiState: StateFlow<NewEntryUiState> = _uiState.asStateFlow()

    fun updateType(type: EntryType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun updateWhoWhat(value: String) {
        _uiState.value = _uiState.value.copy(whoWhat = value)
    }

    fun updateWhatCost(value: String) {
        _uiState.value = _uiState.value.copy(whatCost = value)
    }

    fun updateWhatDifferently(value: String) {
        _uiState.value = _uiState.value.copy(whatDifferently = value)
    }

    fun saveEntry() {
        val state = _uiState.value
        if (state.whoWhat.isBlank() || state.isSaving) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)

            val entry = Entry(
                type = state.type,
                whoWhat = state.whoWhat,
                whatCost = state.whatCost,
                whatDifferently = state.whatDifferently,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                isLocked = false
            )

            entryRepository.createEntry(entry)

            _uiState.value = state.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }

    fun canSave(): Boolean {
        return _uiState.value.whoWhat.isNotBlank()
    }
}
