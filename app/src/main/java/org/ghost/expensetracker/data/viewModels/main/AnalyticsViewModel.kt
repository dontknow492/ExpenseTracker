package org.ghost.expensetracker.data.viewModels.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.enums.ExpenseGroupBy
import org.ghost.expensetracker.core.enums.ExpenseType
import org.ghost.expensetracker.core.utils.DateTimeUtils
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.models.ExpenseChartData
import org.ghost.expensetracker.data.useCase.category.GetCategoryUseCase
import org.ghost.expensetracker.data.useCase.chart.GetExpenseChartDataUseCase
import org.ghost.expensetracker.data.useCase.profile.GetAccountUseCase
import org.ghost.expensetracker.data.useCase.profile.GetCardUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

enum class TimeFilter {
    DAY,
    WEEK,
    MONTH,
    YEAR;

    override fun toString(): String {
        return when (this) {
            DAY -> "Day"
            WEEK -> "Week"
            MONTH -> "Month"
            YEAR -> "Year"
        }
    }

    companion object {
        fun fromString(value: String): TimeFilter {
            return when (value) {
                "Day" -> DAY
                "Week" -> WEEK
                "Month" -> MONTH
                "Year" -> YEAR
                else -> throw IllegalArgumentException("Invalid TimeFilter value: $value")
            }
        }

        fun fromStringOrDefault(value: String, default: TimeFilter): TimeFilter {
            return when (value) {
                "Day" -> DAY
                "Week" -> WEEK
                "Month" -> MONTH
                "Year" -> YEAR
                else -> default
            }
        }
    }


}

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val isIncomeChartLoading: Boolean = true,
    val isSpendChartLoading: Boolean = true,
    val error: String? = null,
    val isIncomeChartError: Boolean = false,
    val isSpendChartError: Boolean = false,

    //category
    val category: Category? = null,
    val categoryExpenseType: ExpenseType = ExpenseType.RECIVE,
    val categoryError: String? = null,
    val isCategoryLoading: Boolean = false,
    val isCategoryChartError: Boolean = false,

    //card
    val cards: Card? = null,
    val cardsError: String? = null,
    val isCardLoading: Boolean = false,
    val isCardChartError: Boolean = false,

    //account
    val account: Account? = null,
    val accountExpenseType: ExpenseType = ExpenseType.RECIVE,
    val accountsError: String? = null,
    val isAccountLoading: Boolean = false,
    val isAccountChartError: Boolean = false,


    //filter
    val incomeTimeFilter: TimeFilter,
    val expenseTimeFilter: TimeFilter,
    val categoryTimeFilter: TimeFilter,
    val cardTimeFilter: TimeFilter,
    val accountTimeFilter: TimeFilter,

    val categoryExpenseData: List<ExpenseChartData> = emptyList(), // Assuming your use case returns List<ChartData>
    val cardExpenseData: List<ExpenseChartData> = emptyList(),
    val accountExpenseData: List<ExpenseChartData> = emptyList(),
    val spendData: List<ExpenseChartData> = emptyList(),
    val incomeData: List<ExpenseChartData> = emptyList()
    // Add other chart data or UI properties here
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getExpenseChartDataUseCase: GetExpenseChartDataUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val getCardUseCase: GetCardUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val savedStateHandle: SavedStateHandle,
    @param: ApplicationContext private val context: android.content.Context
) : ViewModel() {
    private val _profileOwnerId: Long = checkNotNull(savedStateHandle.get<Long>("profileOwnerId"))
    val profileOwnerId: Long = _profileOwnerId

    private val _expenseTimeFilter = MutableStateFlow(TimeFilter.WEEK)
    private val _incomeTimeFilter = MutableStateFlow(TimeFilter.WEEK)
    private val _categoryTimeFilter = MutableStateFlow(TimeFilter.WEEK)
    private val _categoryExpenseType = MutableStateFlow(ExpenseType.RECIVE)
    private val _cardTimeFilter = MutableStateFlow(TimeFilter.WEEK)
    private val _accountTimeFilter = MutableStateFlow(TimeFilter.WEEK)
    private val _accountExpenseType = MutableStateFlow(ExpenseType.RECIVE)



    // Private MutableStateFlow that we will update
    private val _uiState = MutableStateFlow(
        AnalyticsUiState(
            isLoading = true,
            isIncomeChartLoading = true,
            isSpendChartLoading = true,
            error = null,
            isIncomeChartError = false,
            isSpendChartError = false,
            incomeTimeFilter = _incomeTimeFilter.value,
            expenseTimeFilter = _expenseTimeFilter.value,
            categoryTimeFilter = _categoryTimeFilter.value,
            cardTimeFilter = _cardTimeFilter.value,
            accountTimeFilter = _accountTimeFilter.value,
            categoryExpenseType = _categoryExpenseType.value,
            accountExpenseType = _accountExpenseType.value,
        )
    )

    // Public StateFlow that the UI will observe
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadCategoryExpenseData()
        loadExpenseExpenseData()
        loadIncomeExpenseData()
        loadCardExpenseData()
        loadAccountExpenseData()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadCategoryExpenseData() {
        viewModelScope.launch {
            combine(_categoryExpenseType, _categoryTimeFilter) { type, timeFilter ->
                type to timeFilter
            }.flatMapLatest { (type, timeFilter) ->
                val dateRange = DateTimeUtils.mapTimeFilterToDateRange(timeFilter)
                getExpenseChartDataUseCase(
                    profileOwnerId = _profileOwnerId,
                    groupBy = ExpenseGroupBy.CATEGORY,
                    filters = ExpenseFilters(
                        isSend = type == ExpenseType.SEND,
                        minDate = dateRange.first,
                        maxDate = dateRange.second
                    )
                )
                    .onStart {
                        // 1. Set loading to true and clear any previous error state
                        _uiState.update { it.copy(isCardLoading = true, isCategoryChartError = false) }
                    }
                    .catch { exception ->
                        _uiState.update {
                            it.copy(
                                isCardLoading = false,
                                isCategoryChartError = true,
                                error = exception.stackTraceToString()
                            )
                        }
                        // 2. Emit an empty list on error to allow the flow to continue
                        emit(emptyList())
                    }
            }.collect { newCategoryData ->
                // 3. Update data and, most importantly, set loading to false
                _uiState.update { currentState ->
                    currentState.copy(
                        isCardLoading = false,
                        categoryExpenseData = newCategoryData,
                        categoryTimeFilter = _categoryTimeFilter.value,
                        categoryExpenseType = _categoryExpenseType.value
                    )
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadCardExpenseData() {
        viewModelScope.launch {
            _cardTimeFilter.flatMapLatest { timeFilter ->
                val dateRange = DateTimeUtils.mapTimeFilterToDateRange(timeFilter)
                getExpenseChartDataUseCase(
                    profileOwnerId = _profileOwnerId,
                    groupBy = ExpenseGroupBy.CARD,
                    filters = ExpenseFilters(
                        isSend = true,
                        minDate = dateRange.first,
                        maxDate = dateRange.second
                    )
                ).onStart {
                    // Set loading state at the beginning of the flow
                    _uiState.update { it.copy(isCardLoading = true) }
                }.catch { exception ->
                    // Handle any errors
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.stackTraceToString(),
                            isCardChartError = true
                        )
                    }
                }
            }
                .collect { newCategoryData ->
                    // Update state with the new data
                    _uiState.update { currentState ->
                        currentState.copy(
                            isCardLoading = false,
                            cardExpenseData = newCategoryData,
                            cardTimeFilter = _cardTimeFilter.value,
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadAccountExpenseData() {
        viewModelScope.launch {
            combine(_accountExpenseType, _accountTimeFilter) { type, timeFilter ->
                type to timeFilter
            }.flatMapLatest { (type, timeFilter) ->
                val dateRange = DateTimeUtils.mapTimeFilterToDateRange(timeFilter)
                getExpenseChartDataUseCase(
                    profileOwnerId = _profileOwnerId,
                    groupBy = ExpenseGroupBy.ACCOUNT,
                    filters = ExpenseFilters(
                        isSend = type == ExpenseType.SEND,
                        minDate = dateRange.first,
                        maxDate = dateRange.second
                    )
                ).onStart {
                    _uiState.update { it.copy(isAccountLoading = true) }
                }.catch {
                    _uiState.update { it.copy(isAccountLoading = false, error = it.error, isAccountChartError = true) }
                }
            }.collect { newAccountData ->
                _uiState.update {
                    it.copy(
                        isAccountLoading = false,
                        accountExpenseData = newAccountData,
                        accountTimeFilter = _accountTimeFilter.value,
                        accountExpenseType = _accountExpenseType.value
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadExpenseExpenseData() {
        viewModelScope.launch {
            _expenseTimeFilter.flatMapLatest { timeFilter ->
                getExpenseChartDataUseCase(
                    profileOwnerId = _profileOwnerId,
                    groupBy = DateTimeUtils.mapTimeFilterToExpenseGroupBy(timeFilter),
                    filters = ExpenseFilters(
                        isSend = true // Filtering for expence
                    )
                ).onStart {
                    // This runs when the inner flow starts
                    _uiState.update {
                        it.copy(
                            isSpendChartLoading = true,
                            isSpendChartError = false,
                            error = null
                        )
                    }
                }.catch { exception ->
                    // This runs if the inner flow throws an error
                    _uiState.update {
                        it.copy(
                            isSpendChartLoading = false,
                            error = exception.message,
                            isSpendChartError = true
                        )
                    }
                }
            }.collect { expenseData -> // <-- FIX: Added .collect to trigger the flow
                // This runs whenever new incomeData is emitted
                _uiState.update { currentState ->
                    currentState.copy(
                        isSpendChartLoading = false,
                        spendData = expenseData,
                        expenseTimeFilter = _expenseTimeFilter.value
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadIncomeExpenseData() {
        viewModelScope.launch {
            _incomeTimeFilter.flatMapLatest { timeFilter ->
                getExpenseChartDataUseCase(
                    profileOwnerId = _profileOwnerId,
                    groupBy = DateTimeUtils.mapTimeFilterToExpenseGroupBy(timeFilter),
                    filters = ExpenseFilters(
                        isSend = false // Filtering for income
                    )
                ).onStart {
                    // This runs when the inner flow starts
                    _uiState.update {
                        it.copy(
                            isIncomeChartLoading = true,
                            isIncomeChartError = false,
                            error = null
                        )
                    }
                }.catch { exception ->
                    // This runs if the inner flow throws an error
                    _uiState.update {
                        it.copy(
                            isIncomeChartLoading = false,
                            error = exception.message,
                            isIncomeChartError = true
                        )
                    }
                }
            }.collect { incomeData -> // <-- FIX: Added .collect to trigger the flow
                // This runs whenever new incomeData is emitted
                _uiState.update { currentState ->
                    currentState.copy(
                        isIncomeChartLoading = false,
                        incomeData = incomeData,
                        incomeTimeFilter = _incomeTimeFilter.value
                    )
                }
            }
        }
    }


    fun onIncomeTimeFilterChange(timeFilter: TimeFilter) {
        _incomeTimeFilter.update {
            timeFilter
        }
    }

    fun onExpenseTimeFilterChange(timeFilter: TimeFilter) {
        _expenseTimeFilter.update {
            timeFilter
        }
    }

    fun onCategoryTimeFilterChange(timeFilter: TimeFilter) {
        _categoryTimeFilter.update {
            timeFilter
        }
    }

    fun onCardTimeFilterChange(timeFilter: TimeFilter) {
        _cardTimeFilter.update {
            timeFilter
        }
    }

    fun onAccountTimeFilterChange(timeFilter: TimeFilter) {
        _accountTimeFilter.update {
            timeFilter
        }
    }

    fun onCategoryExpenseTypeChange(expenseType: ExpenseType) {
        _categoryExpenseType.update {
            expenseType
        }
    }

    fun onAccountExpenseTypeChange(expenseType: ExpenseType) {
        _accountExpenseType.update {
            expenseType
        }
    }


    fun updateCategory(name: String) {
        viewModelScope.launch {
            // Use firstOrNull to get a single result and then stop collecting.
            val category = getCategoryUseCase(profileOwnerId, name).firstOrNull()

            if (category == null) {
                _uiState.update {
                    it.copy(
                        categoryError = context.getString(R.string.category_not_found),
                        category = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        categoryError = null,
                        category = category
                    )
                }
            }
        }
    }

    fun updateCard(company: String, lastFourDigits: Int) {
        viewModelScope.launch {
            val card = getCardUseCase(profileOwnerId, company, lastFourDigits).firstOrNull()
            if (card == null) {
                _uiState.update {
                    it.copy(
                        cardsError = context.getString(R.string.card_not_found),
                        cards = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        cardsError = null,
                        cards = card
                    )
                }
            }
        }
    }

    fun updateAccount(name: String) {
        viewModelScope.launch {
            val account = getAccountUseCase(profileOwnerId, name).firstOrNull()
            if (account == null) {
                _uiState.update {
                    it.copy(
                        accountsError = context.getString(R.string.account_not_found),
                        account = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        accountsError = null,
                        account = account
                    )
                }
            }
        }
    }


}