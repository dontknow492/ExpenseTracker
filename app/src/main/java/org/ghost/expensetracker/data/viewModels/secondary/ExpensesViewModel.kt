package org.ghost.expensetracker.data.viewModels.secondary

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.core.ui.states.ExpensesFilterData
import org.ghost.expensetracker.core.utils.DateTimeUtils
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.mappers.toDomainModel
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.data.useCase.category.GetCategoriesUseCase
import org.ghost.expensetracker.data.useCase.chart.GetExpenseChartDataUseCase
import org.ghost.expensetracker.data.useCase.expense.DeleteExpensesUseCase
import org.ghost.expensetracker.data.useCase.expense.FilterExpensesIdsUseCase
import org.ghost.expensetracker.data.useCase.expense.FilterExpensesUseCase
import org.ghost.expensetracker.core.enums.TimeFilter
import java.io.IOException
import javax.inject.Inject
import org.ghost.expensetracker.core.utils.combine as myCombine


@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val filterExpensesUseCase: FilterExpensesUseCase,
    private val getExpenseChartDataUseCase: GetExpenseChartDataUseCase,
    private val deleteExpensesUseCase: DeleteExpensesUseCase,
    private val filtersExpensesIdsUseCase: FilterExpensesIdsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 1. A private flow to send one-time error events
    private val _errorEvents = MutableSharedFlow<String>()

    // 2. A public flow for the UI to collect
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()


    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])
    val profileOwnerId: Long = _profileOwnerId


    // filters state
    private val _expenseFiltersState: MutableStateFlow<ExpenseFilters> = MutableStateFlow(
        ExpenseFilters(
            query = savedStateHandle["query"],
            accountId = savedStateHandle["accountId"],
            categoryId = savedStateHandle["categoryId"],
            minDate = savedStateHandle["minDate"],
            maxDate = savedStateHandle["maxDate"],
            maxAmount = savedStateHandle["maxAmount"],
            minAmount = savedStateHandle["minAmount"],
            cardId = savedStateHandle["cardId"]
        )
    )
    val expenseFiltersState: StateFlow<ExpenseFilters> = _expenseFiltersState.asStateFlow()

    private val _sortBy = MutableStateFlow(ExpenseSortBy.DATE)
    val sortBy: StateFlow<ExpenseSortBy> = _sortBy.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()


    private val _minAmountInput = MutableStateFlow("")
    val minAmountInput: StateFlow<String> = _minAmountInput.asStateFlow()

    private val _maxAmountInput = MutableStateFlow("")
    val maxAmountInput: StateFlow<String> = _maxAmountInput.asStateFlow()


    // expenses state
    @OptIn(ExperimentalCoroutinesApi::class)
    val expensesFlow: Flow<PagingData<Expense>> =
        combine(
            _expenseFiltersState,
            _sortBy,
            _sortOrder
        ) { filters, sortByValue, sortOrderValue ->
            // 1. Combine all trigger states into a single object (like a Triple)
            Triple(filters, sortByValue, sortOrderValue)
        }.flatMapLatest { (filters, sortByValue, sortOrderValue) ->
            // 2. flatMapLatest switches to a new Flow whenever the combined state changes
            filterExpensesUseCase(
                profileOwnerId = _profileOwnerId,
                filters = filters,
                sortBy = sortByValue,
                sortOrder = sortOrderValue
            ) // This should return a Flow<PagingData<ExpenseEntity>>
        }.map { pagingData -> // 3. This map is applied to each PagingData emission
            pagingData.map { entity ->
                entity.toDomainModel()
            }
        }.cachedIn(viewModelScope) // 4. Cache the final flow at the end

    @OptIn(ExperimentalCoroutinesApi::class)
    val allExpenseIds = combine(
        _expenseFiltersState,
        _sortBy,
        _sortOrder
    ) { filters, sortByValue, sortOrderValue ->
        Triple(filters, sortByValue, sortOrderValue)
    }.flatMapLatest { (filters, by, order) ->
        filtersExpensesIdsUseCase(
            profileOwnerId = _profileOwnerId,
            filters = filters,
            sortBy = by,
            sortOrder = order
        )
    }
        .onEach { ids ->
            // Add this line to see what the flow is actually emitting
            Log.d("ExpensesViewModel", "Flow produced new ID list: $ids")
        }
        .stateIn( // Use stateIn to convert the Flow to a StateFlow
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
    // 4. Cache the results in a CoroutineScope


    private val _chartGroupBy = MutableStateFlow(TimeFilter.WEEK)

    @OptIn(ExperimentalCoroutinesApi::class)
    val chartData = combine(
        _expenseFiltersState,
        _chartGroupBy
    ) { filters, groupBy ->
        // Create a Pair of the latest values to pass to flatMapLatest
        filters to groupBy
    }.flatMapLatest { (filters, groupBy) ->
        // flatMapLatest cancels the previous flow (database query)
        // and launches a new one when filters or groupBy change.
        getExpenseChartDataUseCase(
            profileOwnerId = _profileOwnerId,
            groupBy = DateTimeUtils.mapTimeFilterToExpenseGroupBy(groupBy),
            filters = filters
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Keeps data for 5s after UI is gone
        initialValue = emptyList() // The initial value while the first real data loads
    )


    private val _selectedIds = MutableStateFlow(emptySet<Long>())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()


    val categoriesState: StateFlow<UiState<List<Category>>> = getCategoriesUseCase(_profileOwnerId)
        .map<List<Category>, UiState<List<Category>>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(it.message ?: "An unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )


    val filterData: StateFlow<ExpensesFilterData> = myCombine(
        _expenseFiltersState,
        categoriesState,
        _sortBy,
        _sortOrder,
        _minAmountInput,
        _maxAmountInput
    ) { filters, categories, sortBy, sortOrder, min, max ->
        ExpensesFilterData(
            filters = filters,
            categories = categories,
            sortBy = sortBy,
            sortOrder = sortOrder,
            minAmount = min,
            maxAmount = max,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpensesFilterData(
            filters = ExpenseFilters(),
            categories = UiState.Loading,
            sortBy = ExpenseSortBy.DATE,
            sortOrder = SortOrder.DESCENDING,
            minAmount = "",
            maxAmount = ""
        )
    )


    /**
     * Toggles the selection state for a given expense ID.
     */
    fun toggleSelection(expense: Expense) {
        _selectedIds.update { currentIds ->
            if (expense.id in currentIds) {
                // If ID is already present, remove it to de-select
                currentIds - expense.id
            } else {
                // If ID is not present, add it to select
                currentIds + expense.id
            }
        }
    }

    /**
     * Clears all current selections.
     */
    fun clearSelection() {
        _selectedIds.value = emptySet()
    }


    fun updateQuery(query: String?) {
        _expenseFiltersState.update {
            it.copy(query = query.let { string ->
                if (string.isNullOrBlank()) null else string
            })
        }
    }

    fun updateAccountId(accountId: Long?) {
        _expenseFiltersState.update { currentFilters ->
            currentFilters.copy(accountId = accountId)
        }
    }

    /**
     *Updates the category filter. Pass null to clear it.
     */
    fun updateCategory(categoryId: Long?) {
        _expenseFiltersState.update { currentFilters ->
            currentFilters.copy(categoryId = categoryId)
        }
    }

    /**
     * Updates the date range filter.
     */
    fun updateDateRange(pair: Pair<Long?, Long?>) {
        _expenseFiltersState.update { currentFilters ->
            currentFilters.copy(minDate = pair.first, maxDate = pair.second)
        }
    }

    /**
     * Updates the amount range filter.
     */
    fun updateAmountRange(range: Pair<Double?, Double?>) {
        _expenseFiltersState.update { currentFilters ->
            currentFilters.copy(
                minAmount = range.first?.takeIf { it != 0.0 },
                maxAmount = range.second?.takeIf { it != 0.0 }
            )
        }
    }

    fun onMinAmountInputChange(newInput: String) {
        // Basic validation to allow only numbers and a single decimal point
        if (newInput.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _minAmountInput.value = newInput
            // Also update the actual filter state
            _expenseFiltersState.update {
                it.copy(minAmount = newInput.toDoubleOrNull()?.takeIf { num -> num > 0.0 })
            }
        }
    }

    fun onMaxAmountInputChange(newInput: String) {
        if (newInput.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _maxAmountInput.value = newInput
            _expenseFiltersState.update {
                it.copy(maxAmount = newInput.toDoubleOrNull()?.takeIf { num -> num > 0.0 })
            }
        }
    }

    /**
     * Resets all filters to their default state.
     */
    fun clearFilters() {
        _expenseFiltersState.update {
            ExpenseFilters() // Creates a new, empty filters object
        }
    }

    fun updateExpenseFilters(filters: ExpenseFilters) {
        _expenseFiltersState.update {
            filters
        }
    }

    fun updateSortBy(sortBy: ExpenseSortBy) {
        _sortBy.update {
            sortBy
        }
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        _sortOrder.update {
            sortOrder
        }
    }


    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                val idsToDelete = selectedIds.value.toList()
                if (idsToDelete.isEmpty()) return@launch

                val success = deleteExpensesUseCase(expense)
                if (success) {
                    // Handle success (e.g., show a success message)
                    clearSelection()
                } else {
                    // Handle a known failure
                    _errorEvents.emit("Failed to delete the item.")
                }
            } catch (e: IOException) {
                // 3. On an unexpected exception, emit an error message
                _errorEvents.emit("A database error occurred. Please try again.")
            } catch (e: Exception) {
                _errorEvents.emit("An unexpected error occurred.")
            }
        }
    }

    fun selectAll() {
        Log.d("ExpensesViewModel", "selectAll calleds: ${allExpenseIds.value}")
        _selectedIds.update { currentSelection ->
            currentSelection + allExpenseIds.value
        }
    }

    fun deselectAll() {
        // Replaces the selection with an empty set
        _selectedIds.value = emptySet()
    }

    fun invertSelection() {
        // Replaces the selection with the inverse:
        // all possible IDs MINUS the ones that are currently selected.
        _selectedIds.update { currentSelection ->
            val allIds = allExpenseIds.value.toSet()
            allIds - currentSelection // This performs a set difference
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            try {
                val idsToDelete = selectedIds.value.toList()
                if (idsToDelete.isEmpty()) return@launch

                val success = deleteExpensesUseCase(idsToDelete)
                if (success) {
                    // Handle success (e.g., show a success message)
                    clearSelection()
                } else {
                    // Handle a known failure
                    _errorEvents.emit("Failed to delete the selected items.")
                }
            } catch (e: IOException) {
                // 3. On an unexpected exception, emit an error message
                _errorEvents.emit("A database error occurred. Please try again.")
            } catch (e: Exception) {
                _errorEvents.emit("An unexpected error occurred.")
            }
        }
    }
}