package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.EntryType
import com.thehand.android.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class PatternData(
    val built: Int = 0,
    val helped: Int = 0,
    val learned: Int = 0,
    val total: Int = 0
)

data class PatternsUiState(
    val weeklyPattern: PatternData = PatternData(),
    val monthlyPattern: PatternData = PatternData(),
    val isLoading: Boolean = true
)

@HiltViewModel
class PatternsViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatternsUiState())
    val uiState: StateFlow<PatternsUiState> = _uiState.asStateFlow()

    init {
        loadPatterns()
    }

    private fun loadPatterns() {
        viewModelScope.launch {
            val now = Instant.now()

            // Weekly (last 7 days)
            val weekStart = now.minus(7, ChronoUnit.DAYS)
            val weeklyData = entryRepository.getPatternData(weekStart, now)
            val weeklyPattern = PatternData(
                built = weeklyData[EntryType.BUILT] ?: 0,
                helped = weeklyData[EntryType.HELPED] ?: 0,
                learned = weeklyData[EntryType.LEARNED] ?: 0,
                total = weeklyData.values.sum()
            )

            // Monthly (last 30 days)
            val monthStart = now.minus(30, ChronoUnit.DAYS)
            val monthlyData = entryRepository.getPatternData(monthStart, now)
            val monthlyPattern = PatternData(
                built = monthlyData[EntryType.BUILT] ?: 0,
                helped = monthlyData[EntryType.HELPED] ?: 0,
                learned = monthlyData[EntryType.LEARNED] ?: 0,
                total = monthlyData.values.sum()
            )

            _uiState.value = PatternsUiState(
                weeklyPattern = weeklyPattern,
                monthlyPattern = monthlyPattern,
                isLoading = false
            )
        }
    }
}
