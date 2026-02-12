package com.thehand.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thehand.android.data.model.TrustedHand
import com.thehand.android.data.repository.TrustedHandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrustedHandsUiState(
    val trustedHands: List<TrustedHand> = emptyList(),
    val canAddMore: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class TrustedHandsViewModel @Inject constructor(
    private val trustedHandRepository: TrustedHandRepository
) : ViewModel() {

    val uiState: StateFlow<TrustedHandsUiState> = trustedHandRepository.getTrustedHands()
        .map { hands ->
            TrustedHandsUiState(
                trustedHands = hands,
                canAddMore = hands.size < 3,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrustedHandsUiState()
        )

    fun addTrustedHand(name: String, identifier: String) {
        viewModelScope.launch {
            if (trustedHandRepository.getTrustedHandCount() < 3) {
                trustedHandRepository.addTrustedHand(name, identifier)
            }
        }
    }

    fun removeTrustedHand(hand: TrustedHand) {
        viewModelScope.launch {
            trustedHandRepository.removeTrustedHand(hand)
        }
    }
}
