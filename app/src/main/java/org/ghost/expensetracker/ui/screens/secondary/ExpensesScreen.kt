package org.ghost.expensetracker.ui.screens.secondary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.core.ui.actions.ExpensesScreenActions
import org.ghost.expensetracker.core.ui.states.ExpensesFilterData
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.data.viewModels.secondary.ExpensesViewModel
import org.ghost.expensetracker.ui.components.CategoriesContent
import org.ghost.expensetracker.ui.components.ErrorSnackBar
import org.ghost.expensetracker.ui.components.ExpenseItem
import org.ghost.expensetracker.ui.components.SortListScreen


@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = hiltViewModel(),
    onNavigateBackClick: () -> Unit,
    onExpenseCardClick: (Expense) -> Unit,
) {
    val expensesPagingFLow: LazyPagingItems<Expense> =
        viewModel.expensesFlow.collectAsLazyPagingItems()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val errorEvent by viewModel.errorEvents.collectAsStateWithLifecycle(null)

    val filterData by viewModel.filterData.collectAsStateWithLifecycle()

    val actions = remember {
        ExpensesScreenActions(
            onNavigateBackClick = onNavigateBackClick,
            onExpenseCardClick = onExpenseCardClick,
            onExpenseLongClick = viewModel::toggleSelection,
            onUpdateQuery = viewModel::updateQuery,
            onUpdateAccountId = viewModel::updateAccountId,
            onUpdateCategory = viewModel::updateCategory,
            onUpdateDateRange = viewModel::updateDateRange,
            onMinAmountInputChange = viewModel::onMinAmountInputChange,
            onMaxAmountInputChange = viewModel::onMaxAmountInputChange,
            onUpdateExpenseFilters = viewModel::updateExpenseFilters,
            onUpdateSortBy = viewModel::updateSortBy,
            onUpdateSortOrder = viewModel::updateSortOrder,
            onDeleteExpense = viewModel::deleteExpense,
            onSelectAll = viewModel::selectAll,
            onDeselectAll = viewModel::deselectAll,
            onInvertSelection = viewModel::invertSelection,
            onClearFilters = viewModel::clearFilters,
            onDeleteSelected = viewModel::deleteSelected
        )
    }

    ExpensesScreenContent(
        modifier = modifier,
        expenses = expensesPagingFLow,
        filterData = filterData,
        selectedIds = selectedIds,
        actions = actions,
        errorString = errorEvent,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpensesScreenContent(
    modifier: Modifier = Modifier,
    expenses: LazyPagingItems<Expense>,
    selectedIds: Set<Long>,
    actions: ExpensesScreenActions,
    errorString: String? = null,
    filterData: ExpensesFilterData,
) {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    var isDateDialogVisible by remember { mutableStateOf(false) }
    val onDismissRequest = { isBottomSheetVisible = false }
    val snackbarHostState = remember { SnackbarHostState() }
    val isNavigationBarVisible = remember(selectedIds) {
        selectedIds.isNotEmpty()
    }

    LaunchedEffect(errorString) {
        errorString?.let {
            snackbarHostState.showSnackbar(
                it,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                ErrorSnackBar(snackbarData = snackbarData)
            }
        },
        topBar = {
            ExpensesTopAppBar(
                searchQuery = filterData.filters.query ?: "",
                onQueryChange = actions.onUpdateQuery,
                onBackClick = actions.onNavigateBackClick,
                onFilterClick = { isBottomSheetVisible = true }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                isNavigationBarVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                ExpensesNavigationBar(
                    onSelectAll = actions.onSelectAll,
                    onDeselectAll = actions.onDeselectAll,
                    onInvertSelection = actions.onInvertSelection,
                    onClearFilters = actions.onClearFilters,
                    onDelete = actions.onDeleteSelected
                )
            }

        }
    ) { innerPadding ->
        ExpensesList(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            expenses = expenses,
            selectedIds = selectedIds,
            onExpenseClick = actions.onExpenseCardClick,
            onExpenseLongClick = actions.onExpenseLongClick
        )

        if (isBottomSheetVisible) {
            ExpenseBottomSheet(
                bottomSheetData = filterData,
                onDismissRequest = onDismissRequest,
                onCategoryClick = actions.onUpdateCategory,
                onSortByChange = actions.onUpdateSortBy,
                onSortOrderChange = actions.onUpdateSortOrder,
                onDateRangeClick = { isDateDialogVisible = true },
                onMinAmountInputChange = actions.onMinAmountInputChange,
                onMaxAmountInputChange = actions.onMaxAmountInputChange,
            )
        }
        if (isDateDialogVisible) {
            DatePickerContent(
                onDismiss = { isDateDialogVisible = false },
                onDateRangeSelected = actions.onUpdateDateRange,
                minDate = filterData.filters.minDate,
                maxDate = filterData.filters.maxDate
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpensesTopAppBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    TopAppBar(
        modifier = modifier,
        title = {
            // The search bar is placed in the title slot
            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search expenses...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    // Show a clear button if the search query is not empty
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                },
                singleLine = true,
                // Hide the default background and indicator
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Hide the keyboard when the user submits the search
                        focusManager.clearFocus()
                    }
                )
            )
        },
        // Icon for navigating back
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        // Icon for additional actions, like opening a filter menu
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    painterResource(R.drawable.rounded_filter_list_24),
                    contentDescription = "Filter Expenses"
                )
            }
        }
    )
}

@Composable
private fun ExpensesNavigationBar(
    modifier: Modifier = Modifier,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onInvertSelection: () -> Unit,
    onClearFilters: () -> Unit,
    onDelete: () -> Unit,
) {
    NavigationBar(modifier = modifier) {
        // Select All Action
        NavigationBarItem(
            selected = false, // These are action items, not selectable destinations
            onClick = onSelectAll,
            icon = {
                Icon(
                    painterResource(R.drawable.rounded_select_all_24),
                    contentDescription = stringResource(R.string.select_all)
                )
            },
            label = { Text(stringResource(R.string.select_all)) }
        )

        // Deselect All Action
        NavigationBarItem(
            selected = false,
            onClick = onDeselectAll,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_deselect_24),
                    contentDescription = stringResource(R.string.deselect_all)
                )
            },
            label = { Text(stringResource(R.string.deselect)) }
        )

        // Invert Selection Action
        NavigationBarItem(
            selected = false,
            onClick = onInvertSelection,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.selection_symmetric_difference),
                    contentDescription = stringResource(R.string.invert_selection)
                )
            },
            label = { Text(stringResource(R.string.invert)) }
        )

        // Clear Filters Action
        NavigationBarItem(
            selected = false,
            onClick = onClearFilters,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_filter_list_off_24),
                    contentDescription = stringResource(R.string.clear_filters)
                )
            },
            label = { Text(stringResource(R.string.clear_filters)) }
        )

        // Delete Action
        NavigationBarItem(
            selected = false,
            onClick = onDelete,
            icon = {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete_selected_items)
                )
            },
            label = { Text(stringResource(R.string.delete)) }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetData: ExpensesFilterData,
    onDismissRequest: () -> Unit,
    onMinAmountInputChange: (String) -> Unit,
    onMaxAmountInputChange: (String) -> Unit,
    onCategoryClick: (Long) -> Unit,
    onSortByChange: (ExpenseSortBy) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onDateRangeClick: () -> Unit,
) {
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        PrimaryTabRow(
            pagerState.currentPage
        ) {
            Tab(
                selected = true,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("Filters") }
            )
            Tab(
                selected = true,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("Sorts") }
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(16.dp)
        ) { page ->
            when (page) {
                0 -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = bottomSheetData.minAmount,
                                onValueChange = onMinAmountInputChange,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier.weight(1f),
                                label = { Text("Min amount") },
                                placeholder = { Text("10.00") }
                            )
                            OutlinedTextField(
                                value = bottomSheetData.maxAmount,
                                onValueChange = onMaxAmountInputChange,
                                modifier = Modifier.weight(1f),
                                label = { Text("Max amount") },
                                placeholder = { Text("100.00 ") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            IconButton(
                                onClick = {
                                    onDateRangeClick()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = stringResource(R.string.date_range)
                                )
                            }
                        }

                        CategoriesContent(
                            selectedCategoryId = bottomSheetData.filters.categoryId,
                            categoriesState = bottomSheetData.categories,
                            allowAddCategory = false,
                            onAddNewClick = {},
                            onCategoryClick = {
                                onCategoryClick(it.id)
                            }
                        )
                    }
                }

                1 -> {
                    SortListScreen(
                        sortBy = bottomSheetData.sortBy,
                        sortOrder = bottomSheetData.sortOrder,
                        onValueChange = { sortBy, sortOrder ->
                            onSortByChange(sortBy)
                            onSortOrderChange(sortOrder)
                        }
                    )
                }
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerContent(
    modifier: Modifier = Modifier,
    minDate: Long? = null,
    maxDate: Long? = null,
    onDismiss: () -> Unit,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = minDate,
        initialSelectedEndDateMillis = maxDate
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}


@Composable
private fun ExpensesList(
    modifier: Modifier,
    expenses: LazyPagingItems<Expense>,
    selectedIds: Set<Long>,
    onExpenseClick: (Expense) -> Unit,
    onExpenseLongClick: (Expense) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = expenses.itemCount,
            key = { index -> expenses[index]?.id ?: index }) { index ->
            val expense = expenses.get(index)
            expense?.let {
                val isItemSelected = it.id in selectedIds
                ExpenseItem(
                    modifier = Modifier,
                    expense = it,
                    isSelected = isItemSelected,
                    onClick = { expense ->
                        if (isItemSelected) {
                            onExpenseLongClick(expense)
                        } else {
                            onExpenseClick(expense)
                        }
                    },
                    onLongClick = onExpenseLongClick,
                )


            }
        }
    }
}