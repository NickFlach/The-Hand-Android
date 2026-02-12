package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.Entry
import com.thehand.android.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class MonthGroup(
    val yearMonth: YearMonth,
    val displayName: String,
    val entries: List<Entry>,
    val count: Int
)

data class ArchiveUiState(
    val monthGroups: List<MonthGroup> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    val uiState: StateFlow<ArchiveUiState> = entryRepository.getAllEntries()
        .map { entries ->
            val grouped = entries
                .groupBy { entry ->
                    val instant = entry.createdAt
                    val zonedDateTime = instant.atZone(java.time.ZoneId.systemDefault())
                    YearMonth.of(zonedDateTime.year, zonedDateTime.month)
                }
                .map { (yearMonth, monthEntries) ->
                    MonthGroup(
                        yearMonth = yearMonth,
                        displayName = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        entries = monthEntries,
                        count = monthEntries.size
                    )
                }
                .sortedByDescending { it.yearMonth }

            ArchiveUiState(
                monthGroups = grouped,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ArchiveUiState()
        )
}
