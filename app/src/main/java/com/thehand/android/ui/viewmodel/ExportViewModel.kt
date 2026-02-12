package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.EntryType
import com.thehand.android.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ExportUiState(
    val isExporting: Boolean = false,
    val exportComplete: Boolean = false,
    val exportedContent: String = "",
    val format: ExportFormat = ExportFormat.TEXT
)

enum class ExportFormat {
    TEXT,
    JSON
}

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    fun exportAsText() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, format = ExportFormat.TEXT)

            entryRepository.getAllEntries().first().let { entries ->
                val content = buildString {
                    appendLine("THE HAND")
                    appendLine("Private Ledger Export")
                    appendLine("=" .repeat(50))
                    appendLine()
                    appendLine("Total Entries: ${entries.size}")
                    appendLine("Exported: ${DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(java.time.LocalDateTime.now())}")
                    appendLine()
                    appendLine("=" .repeat(50))
                    appendLine()

                    entries.forEach { entry ->
                        appendLine()
                        appendLine("-" .repeat(50))
                        appendLine("Type: ${entry.type.name}")
                        appendLine("Date: ${DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(entry.createdAt.atZone(java.time.ZoneId.systemDefault()))}")
                        appendLine()
                        appendLine("Who or what was affected?")
                        appendLine(entry.whoWhat)
                        appendLine()

                        if (entry.whatCost.isNotBlank()) {
                            appendLine("What did it cost you?")
                            appendLine(entry.whatCost)
                            appendLine()
                        }

                        if (entry.whatDifferently.isNotBlank()) {
                            appendLine("What would you do differently?")
                            appendLine(entry.whatDifferently)
                            appendLine()
                        }

                        if (entry.isLocked) {
                            appendLine("[Locked]")
                        }
                        appendLine("-" .repeat(50))
                    }

                    appendLine()
                    appendLine()
                    appendLine("=" .repeat(50))
                    appendLine("End of Export")
                }

                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportComplete = true,
                    exportedContent = content
                )
            }
        }
    }

    fun exportAsJson() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, format = ExportFormat.JSON)

            entryRepository.getAllEntries().first().let { entries ->
                val jsonRoot = JSONObject().apply {
                    put("exported_at", java.time.Instant.now().toString())
                    put("version", "1.0.0")
                    put("total_entries", entries.size)

                    val entriesArray = JSONArray()
                    entries.forEach { entry ->
                        val entryJson = JSONObject().apply {
                            put("id", entry.id)
                            put("type", entry.type.name)
                            put("who_what", entry.whoWhat)
                            put("what_cost", entry.whatCost)
                            put("what_differently", entry.whatDifferently)
                            put("created_at", entry.createdAt.toString())
                            put("updated_at", entry.updatedAt.toString())
                            put("is_locked", entry.isLocked)
                            entry.threadId?.let { put("thread_id", it) }
                        }
                        entriesArray.put(entryJson)
                    }
                    put("entries", entriesArray)
                }

                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportComplete = true,
                    exportedContent = jsonRoot.toString(2)
                )
            }
        }
    }

    fun resetExport() {
        _uiState.value = ExportUiState()
    }
}
