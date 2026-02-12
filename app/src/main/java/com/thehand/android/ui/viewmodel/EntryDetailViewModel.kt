package com.thehand.android.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.Addendum
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.canEdit
import com.thehand.android.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryDetailUiState(
    val entry: Entry? = null,
    val addendums: List<Addendum> = emptyList(),
    val canEdit: Boolean = false,
    val isLocked: Boolean = true,
    val isLoading: Boolean = true,
    val showAddendumDialog: Boolean = false,
    val showDeleteDialog: Boolean = false
)

@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val entryId: Long = savedStateHandle.get<String>("entryId")?.toLongOrNull() ?: 0L

    private val _showAddendumDialog = MutableStateFlow(false)
    private val _showDeleteDialog = MutableStateFlow(false)
    private val _addendumText = MutableStateFlow("")

    val addendumText: StateFlow<String> = _addendumText.asStateFlow()

    val uiState: StateFlow<EntryDetailUiState> = combine(
        entryRepository.getEntryById(entryId),
        entryRepository.getAddendumsForEntry(entryId),
        _showAddendumDialog,
        _showDeleteDialog
    ) { entry, addendums, showAddendum, showDelete ->
        entry?.let {
            val locked = it.lockIfExpired()
            EntryDetailUiState(
                entry = locked,
                addendums = addendums,
                canEdit = locked.canEdit(),
                isLocked = locked.isLocked,
                isLoading = false,
                showAddendumDialog = showAddendum,
                showDeleteDialog = showDelete
            )
        } ?: EntryDetailUiState(isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EntryDetailUiState()
    )

    fun updateEntry(entry: Entry) {
        viewModelScope.launch {
            entryRepository.updateEntry(entry)
        }
    }

    fun deleteEntry(onDeleted: () -> Unit) {
        viewModelScope.launch {
            uiState.value.entry?.let {
                entryRepository.deleteEntry(it)
                onDeleted()
            }
        }
    }

    fun showAddendumDialog() {
        _showAddendumDialog.value = true
    }

    fun hideAddendumDialog() {
        _showAddendumDialog.value = false
        _addendumText.value = ""
    }

    fun updateAddendumText(text: String) {
        _addendumText.value = text
    }

    fun saveAddendum() {
        viewModelScope.launch {
            if (_addendumText.value.isNotBlank()) {
                entryRepository.addAddendum(entryId, _addendumText.value)
                hideAddendumDialog()
            }
        }
    }

    fun showDeleteDialog() {
        _showDeleteDialog.value = true
    }

    fun hideDeleteDialog() {
        _showDeleteDialog.value = false
    }
}
