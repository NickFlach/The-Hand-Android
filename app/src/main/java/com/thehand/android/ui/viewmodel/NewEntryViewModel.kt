package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.EntryType
import com.thehand.android.data.model.Thread
import com.thehand.android.data.repository.EntryRepository
import com.thehand.android.data.repository.ThreadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class NewEntryUiState(
    val type: EntryType = EntryType.BUILT,
    val whoWhat: String = "",
    val whatCost: String = "",
    val whatDifferently: String = "",
    val threadId: Long? = null,
    val availableThreads: List<Thread> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class NewEntryViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val threadRepository: ThreadRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(NewEntryUiState())

    val uiState: StateFlow<NewEntryUiState> = combine(
        _formState,
        threadRepository.getActiveThreads()
    ) { form, threads ->
        form.copy(availableThreads = threads)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewEntryUiState()
    )

    fun updateType(type: EntryType) {
        _formState.value = _formState.value.copy(type = type)
    }

    fun updateWhoWhat(value: String) {
        _formState.value = _formState.value.copy(whoWhat = value)
    }

    fun updateWhatCost(value: String) {
        _formState.value = _formState.value.copy(whatCost = value)
    }

    fun updateWhatDifferently(value: String) {
        _formState.value = _formState.value.copy(whatDifferently = value)
    }

    fun updateThreadId(threadId: Long?) {
        _formState.value = _formState.value.copy(threadId = threadId)
    }

    fun saveEntry() {
        val state = _formState.value
        if (state.whoWhat.isBlank() || state.isSaving) return

        viewModelScope.launch {
            _formState.value = state.copy(isSaving = true)

            val entry = Entry(
                type = state.type,
                whoWhat = state.whoWhat,
                whatCost = state.whatCost,
                whatDifferently = state.whatDifferently,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                isLocked = false,
                threadId = state.threadId
            )

            entryRepository.createEntry(entry)

            _formState.value = state.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }

    fun canSave(): Boolean {
        return uiState.value.whoWhat.isNotBlank()
    }
}
