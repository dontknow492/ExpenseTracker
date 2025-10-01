package org.ghost.expensetracker.data.viewModels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import org.ghost.expensetracker.core.exceptions.ProfileNotFoundException
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.mappers.toDomainModel
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.useCase.expense.FilterExpensesUseCase
import org.ghost.expensetracker.data.useCase.profile.FilterCardsUseCase
import org.ghost.expensetracker.data.useCase.profile.GetAccountsUseCase
import org.ghost.expensetracker.data.useCase.profile.GetProfileUseCase
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getProfileUseCase: GetProfileUseCase,
    getAccountsUseCase: GetAccountsUseCase,
    filterCardsUseCase: FilterCardsUseCase,
    private val filterExpensesUseCase: FilterExpensesUseCase, // Keep as private val
) : ViewModel() {

    private val _profileId: Long = checkNotNull(savedStateHandle["profileOwnerId"])
    val profileId: Long = _profileId

    private val _expenseFilter = MutableStateFlow(ExpenseFilters())
    val expenseFilter = _expenseFilter.asStateFlow()

    // -- Profile State --
    val profileState: StateFlow<UiState<Profile>> = getProfileUseCase(profileId)
        // 1. Validate the profile: throw a specific exception if it's null
        .map { profile -> profile ?: throw ProfileNotFoundException() }
        // 2. Map the guaranteed non-null profile to the Success state
        .map<Profile, UiState<Profile>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        // 3. Catch all throwables, including ProfileNotFoundException
        .catch { emit(UiState.Error(it.message ?: "An unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    // -- Accounts State --
    val accountsState: StateFlow<UiState<List<Account>>> = getAccountsUseCase(profileId)
        .map<List<Account>, UiState<List<Account>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(it.message ?: "An unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )


    // -- Cards State (no filters) --
    val cardsState: StateFlow<UiState<List<Card>>> = filterCardsUseCase(
        profileOwnerId = profileId
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

    // -- Expenses State (Paging) --
    private val _expenseFilters = MutableStateFlow(ExpenseFilters())
    val expenseFilters = _expenseFilters.asStateFlow()

    // Paging data is exposed as a Flow and cached in the ViewModel scope
    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesPagerFlow: Flow<PagingData<Expense>> = _expenseFilters
        .flatMapLatest { filters ->
            filterExpensesUseCase(
                profileOwnerId = profileId,
                filters = filters
            )// Get the Flow<PagingData> from the Pager
        }
        .map { pagingData ->
            pagingData.map { expenseEntity ->
                expenseEntity.toDomainModel()
            }
        }
        .cachedIn(viewModelScope)


    fun setExpenseFilters(filters: ExpenseFilters) {
        _expenseFilters.value = filters
    }
}