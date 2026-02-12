package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.Thread
import com.thehand.android.data.repository.ThreadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThreadsUiState(
    val activeThreads: List<Thread> = emptyList(),
    val closedThreads: List<Thread> = emptyList(),
    val showCreateDialog: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class ThreadsViewModel @Inject constructor(
    private val threadRepository: ThreadRepository
) : ViewModel() {

    private val _showCreateDialog = MutableStateFlow(false)
    private val _newThreadName = MutableStateFlow("")
    private val _newThreadDescription = MutableStateFlow("")

    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()
    val newThreadName: StateFlow<String> = _newThreadName.asStateFlow()
    val newThreadDescription: StateFlow<String> = _newThreadDescription.asStateFlow()

    val uiState: StateFlow<ThreadsUiState> = combine(
        threadRepository.getActiveThreads(),
        threadRepository.getClosedThreads(),
        _showCreateDialog
    ) { active, closed, showDialog ->
        ThreadsUiState(
            activeThreads = active,
            closedThreads = closed,
            showCreateDialog = showDialog,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThreadsUiState()
    )

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
        _newThreadName.value = ""
        _newThreadDescription.value = ""
    }

    fun updateThreadName(name: String) {
        _newThreadName.value = name
    }

    fun updateThreadDescription(description: String) {
        _newThreadDescription.value = description
    }

    fun createThread() {
        viewModelScope.launch {
            if (_newThreadName.value.isNotBlank()) {
                threadRepository.createThread(
                    name = _newThreadName.value,
                    description = _newThreadDescription.value
                )
                hideCreateDialog()
            }
        }
    }

    fun closeThread(thread: Thread) {
        viewModelScope.launch {
            threadRepository.closeThread(thread)
        }
    }

    fun reopenThread(thread: Thread) {
        viewModelScope.launch {
            threadRepository.reopenThread(thread)
        }
    }

    fun deleteThread(thread: Thread) {
        viewModelScope.launch {
            threadRepository.deleteThread(thread)
        }
    }
}
