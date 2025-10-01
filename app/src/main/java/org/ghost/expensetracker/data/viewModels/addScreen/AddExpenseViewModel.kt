package org.ghost.expensetracker.data.viewModels.addScreen

import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.exceptions.InsufficientBalanceException
import org.ghost.expensetracker.core.exceptions.InvalidAmountException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.exceptions.InvalidSourceOfFundsException
import org.ghost.expensetracker.core.exceptions.ItemNotFoundException
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.core.ui.states.AddExpenseUiState
import org.ghost.expensetracker.core.utils.getResourceEntryName
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.data.useCase.AddExpenseUseCase
import org.ghost.expensetracker.data.useCase.category.GetCategoriesUseCase
import org.ghost.expensetracker.data.useCase.profile.FilterCardsUseCase
import org.ghost.expensetracker.data.useCase.profile.GetAccountsUseCase
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val filterCardsUseCase: FilterCardsUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val savedStateHandle: SavedStateHandle,
    @param: ApplicationContext private val context: Context,
) : ViewModel() {

    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])
    val profileOwnerId = _profileOwnerId
    private val _isSending: Boolean = savedStateHandle["isSend"] ?: true
    private val _uiState = MutableStateFlow(AddExpenseUiState(isSend = _isSending))
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    val accountsState: StateFlow<UiState<List<Account>>> = getAccountsUseCase(_profileOwnerId)
        .map<List<Account>, UiState<List<Account>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(it.message ?: "An unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

//    val

    val cardsState: StateFlow<UiState<List<Card>>> = filterCardsUseCase(
        profileOwnerId = _profileOwnerId
        // name and sortOrder now use their default values from the use case
    )
        .map<List<Card>, UiState<List<Card>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(it.message ?: "An unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    val categoriesState: StateFlow<UiState<List<Category>>> = getCategoriesUseCase(_profileOwnerId)
        .map<List<Category>, UiState<List<Category>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(it.message ?: "An unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )


    init {
        viewModelScope.launch {
            // Set default account
            launch {
                accountsState.filterIsInstance<UiState.Success<List<Account>>>()
                    .firstOrNull { it.data.isNotEmpty() }
                    ?.let { successState ->
                        if (successState.data.isNotEmpty() && (_uiState.value.account == null && _uiState.value.card == null)) {
                            _uiState.update { it.copy(account = successState.data.first()) }
                        }
                    }
            }

            launch {
                // Set default category
                categoriesState.filterIsInstance<UiState.Success<List<Category>>>()
                    .firstOrNull { it.data.isNotEmpty() }
                    ?.let { successState ->
                        _uiState.update { it.copy(category = successState.data.first()) }
                    }
            }

            launch {
                // Set default card
                cardsState.filterIsInstance<UiState.Success<List<Card>>>()
                    .firstOrNull { it.data.isNotEmpty() }
                    ?.let { successState ->
                        // Suggested fix
                        if (successState.data.isNotEmpty() && (_uiState.value.account == null && _uiState.value.card == null)) {
                            _uiState.update { it.copy(card = successState.data.first()) }
                        }
                    }
            }
        }


    }

    fun onIsSendChange(isSend: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSend = isSend
        )
    }

    fun onCategoryChange(category: Category) {
        _uiState.value = _uiState.value.copy(
            category = category,
            isCategoryError = false
        )
    }

    fun onAccountChange(account: Account) {
        _uiState.value = _uiState.value.copy(
            account = account,
            isAccountError = false
        )
    }

    fun onCardChange(card: Card) {
        _uiState.value = _uiState.value.copy(
            card = card,
            isCardError = false,
        )
    }

    fun onAmountChange(amount: String) {
        _uiState.value = _uiState.value.copy(
            amount = amount,
            isAmountError = false
        )
    }

    fun onCurrencyChange(currency: String) {
        _uiState.value = _uiState.value.copy(
            currency = currency,
            isCurrencyError = false,
        )
    }

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            isTitleError = false
        )
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(
            description = description,
        )
    }

    fun onSourceDueChange(sourceDueId: Long?) {
        _uiState.value = _uiState.value.copy(
            sourceDueId = sourceDueId,
        )
    }

    fun onIconIdChange(@DrawableRes iconId: Int?) {
        _uiState.value = _uiState.value.copy(
            iconId = iconId,
        )
    }

    fun onImageUriChange(imageUri: Uri?) {
        _uiState.value = _uiState.value.copy(
            imageUri = imageUri,
        )
    }

    fun addExpense() {
        val currentState = _uiState.value.copy(
            isLoading = true,
            error = null,
            isExpenseSaved = false
        )
        if (currentState.category == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Category is required.",
                    isCategoryError = true
                )
            }
            return
        }

        val amountDouble = currentState.amount.toDoubleOrNull()
        if (amountDouble == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Invalid amount format (eg. 100.00).",
                    isAmountError = true
                )
            }
            return
        }

        val expense = Expense(
            id = 0,
            profileOwnerId = _profileOwnerId,
            date = System.currentTimeMillis(),
            accountId = currentState.account?.id,
            categoryId = currentState.category.id,
            cardId = currentState.card?.id,
            amount = amountDouble,
            currency = currentState.currency,
            isSend = currentState.isSend,
            title = currentState.title,
            description = currentState.description,
            sourceDueId = currentState.sourceDueId,
            iconName = currentState.iconId.let {
                if (it != null) getResourceEntryName(it, context) else null
            },
            imageUri = currentState.imageUri.toString()
        )
        viewModelScope.launch {
            val result = addExpenseUseCase(expense)
            result.onSuccess {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isExpenseSaved = true
                )
            }.onFailure { throwable ->
                when (throwable) {
                    is InvalidSourceOfFundsException -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = throwable.message,
                            isSourceError = true
                        )
                    }

                    is InvalidAmountException -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = throwable.message,
                            isAmountError = true
                        )
                    }

                    is ItemNotFoundException -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = throwable.message,
                            isAccountError = true,
                        )
                    }

                    is InsufficientBalanceException -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = throwable.message,
                            isAmountError = true,
                        )
                    }

                    is InvalidNameException -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = throwable.message,
                            isTitleError = true
                        )
                    }

                    else -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = throwable.message
                        )
                    }

                }
            }
        }
    }
}