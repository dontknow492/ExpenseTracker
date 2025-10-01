package org.ghost.expensetracker.data.viewModels.addScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.exceptions.CardAlreadyExistsException
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.ui.states.AddCardUiState
import org.ghost.expensetracker.core.utils.getSafeDefaultCurrencyCode
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.useCase.profile.AddCardUseCase
import javax.inject.Inject


@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])

    private val _uiState = MutableStateFlow(AddCardUiState())
    val uiState: StateFlow<AddCardUiState> = _uiState.asStateFlow()

    // --- Functions to handle user input ---
    fun onHolderNameChange(name: String) {
        _uiState.update { it.copy(holderName = name, error = null, isHolderNameValid = true) }
    }

    fun onLastFourDigitsChange(digits: String) {
        // Allow only up to 4 digits
        if (digits.length <= 4 && digits.all { it.isDigit() }) {
            _uiState.update {
                it.copy(
                    cardLastFourDigits = digits,
                    error = null,
                    isCardLastFourDigitsValid = true
                )
            }
        }
    }

    fun onCardCompanyChange(company: String) {
        _uiState.update { it.copy(cardCompany = company, isCardCompanyValid = true) }
    }

    fun onColorChange(hexColor: String) {
        _uiState.update { it.copy(hexColor = hexColor) }
    }

    fun onExpirationDateChange(date: String) {
        _uiState.update { it.copy(expirationDate = date) }
    }

    fun onCardTypeChange(type: String) {
        _uiState.update { it.copy(cardType = type) }
    }

    fun saveCard() {
        val currentState = _uiState.value.copy(
            isLoading = true,
            error = null,
            isHolderNameValid = true,
            isCardLastFourDigitsValid = true,
            isCardCompanyValid = true
        )

        viewModelScope.launch {
            // --- 3. Create Card Object ---
            // In a real app, you'd get the profileOwnerId from your repository
            val newCard = Card(
                id = 0, // 0 tells Room to auto-generate
                profileOwnerId = _profileOwnerId,
                balance = 0.0, // New cards typically start with 0 balance
                currency = getSafeDefaultCurrencyCode(), // Or get from user settings
                holderName = currentState.holderName.trim(),
                type = currentState.cardType, // Or let user choose
                cardCompany = currentState.cardCompany,
                cardLastFourDigits = currentState.cardLastFourDigits.toInt(),
                expirationDate = null, // todo
                hexColor = currentState.hexColor,
                isDefault = false, // Or based on user preference,
                displayOrder = 0 // Or based on user preference
            )

            // --- 4. Call Use Case and Handle Result ---
            val result: Result<Long> = addCardUseCase(newCard)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isCardSaved = true) }
            }.onFailure { throwable ->
                when (throwable) {
                    is InvalidNameException -> {
                        _uiState.update {
                            it.copy(
                                isHolderNameValid = false,
                                error = throwable.message
                            )
                        }
                    }

                    is InvalidCredentialsException -> {
                        _uiState.update {
                            it.copy(
                                isCardLastFourDigitsValid = false,
                                error = throwable.message
                            )
                        }
                    }

                    is IllegalStateException -> {
                        _uiState.update {
                            it.copy(
                                isCardCompanyValid = false,
                                error = throwable.message
                            )
                        }
                    }

                    is CardAlreadyExistsException -> {
                        _uiState.update {
                            it.copy(
                                error = throwable.message
                            )
                        }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(
                                error = throwable.message ?: "An unknown error occurred"
                            )
                        }
                    }
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "An unknown error occurred"
                    )
                }
            }
        }
    }

    fun addAddCard(card: Card) {
        viewModelScope.launch {
            addCardUseCase.invoke(card)

        }
    }
}