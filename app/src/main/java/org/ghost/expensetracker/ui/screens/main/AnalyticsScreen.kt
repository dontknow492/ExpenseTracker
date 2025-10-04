package org.ghost.expensetracker.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.enums.ExpenseType
import org.ghost.expensetracker.core.utils.CurrencyUtils
import org.ghost.expensetracker.core.utils.getSafeDefaultCurrencyCode
import org.ghost.expensetracker.data.default.CategoryDefaults
import org.ghost.expensetracker.data.models.ExpenseChartData
import org.ghost.expensetracker.core.ui.states.AnalyticsUiState
import org.ghost.expensetracker.data.viewModels.main.AnalyticsViewModel
import org.ghost.expensetracker.core.enums.TimeFilter
import org.ghost.expensetracker.core.ui.actions.AnalyticsScreenActions
import org.ghost.expensetracker.ui.components.ChartItem
import org.ghost.expensetracker.ui.components.EnumDropDownButton
import org.ghost.expensetracker.ui.components.ErrorSnackBar
import org.ghost.expensetracker.ui.components.GraphItem
import org.ghost.expensetracker.ui.components.GraphItemState
import org.ghost.expensetracker.ui.components.LineChart
import org.ghost.expensetracker.ui.components.PieChart
import org.ghost.expensetracker.ui.navigation.AppRoute
import org.ghost.expensetracker.ui.navigation.ExpenseTrackerNavigationBar
import org.ghost.expensetracker.ui.navigation.MainRoute
import org.ghost.expensetracker.ui.navigation.SecondaryRoute
import org.ghost.expensetracker.ui.screens.secondary.EmptyScreen


@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onNavigationItemClick: (AppRoute) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profileOwnerId = viewModel.profileOwnerId

    LaunchedEffect(uiState.category, uiState.account) {
        if (uiState.category != null) {
            onNavigationItemClick(
                SecondaryRoute.Expenses(
                    profileOwnerId,
                    categoryId = uiState.category!!.id
                )
            )
            // ✅ Consume the event!
            viewModel.onNavigationHandled()
        }

        if (uiState.account != null) {
            onNavigationItemClick(
                SecondaryRoute.Expenses(
                    profileOwnerId,
                    accountId = uiState.account!!.id
                )
            )
            // ✅ Consume the event!
            viewModel.onNavigationHandled()
        }
    }

    val contentActions = AnalyticsScreenActions(
        onIncomeFilterChange = viewModel::onIncomeTimeFilterChange,
        onExpenseFilterChange = viewModel::onExpenseTimeFilterChange,
        onCategoryFilterChange = viewModel::onCategoryTimeFilterChange,
        onCategoryTypeFilterChange = viewModel::onCategoryExpenseTypeChange,
        onAccountFilterChange = viewModel::onAccountTimeFilterChange,
        onAccountTypeFilterChange = viewModel::onAccountExpenseTypeChange,
        onCategoryItemClick = {
            viewModel.updateCategory(it)
        },
        onAccountItemClick = {
            viewModel.updateAccount(it)
        },
        onCardFilterChange = {
            viewModel.onCardTimeFilterChange(TimeFilter.fromStringOrDefault(it, TimeFilter.WEEK))
        },
        onCardItemClick = {},
    )

    AnalyticsScreenContent(
        modifier = modifier,
        profileId = profileOwnerId,
        onNavigationItemClick = onNavigationItemClick,
        uiState = uiState,
        contentActions = contentActions,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreenContent(
    modifier: Modifier = Modifier,
    profileId: Long,
    uiState: AnalyticsUiState,
    onNavigationItemClick: (AppRoute) -> Unit,
    contentActions: AnalyticsScreenActions,
) {

    val isEmpty by remember(
        uiState.incomeData,
        uiState.spendData,
        uiState.categoryExpenseData,
        uiState.accountExpenseData
    ) {
        derivedStateOf {
            uiState.incomeData.isEmpty() &&
                    uiState.spendData.isEmpty() &&
                    uiState.categoryExpenseData.isEmpty() &&
                    uiState.accountExpenseData.isEmpty()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }


    val incomeState = remember(
        uiState.incomeData,
        uiState.incomeTimeFilter,
        uiState.isIncomeChartError,
        uiState.isIncomeChartLoading
    ) {
        // This defines a lambda function
        val highestIncome = {
            val amount = uiState.incomeData.maxOfOrNull { it.totalAmount } ?: 0.0
            CurrencyUtils.formattedAmount(amount, getSafeDefaultCurrencyCode())
        }

        GraphItemState(
            title = "Income",
            // The fix is to CALL the function to get its String result
            amountString = highestIncome(),
            filter = uiState.incomeTimeFilter,
            filters = TimeFilter.entries,
            isError = uiState.isIncomeChartError,
            isLoading = uiState.isIncomeChartLoading
        )
    }

    val spendState = remember(
        uiState.spendData,
        uiState.expenseTimeFilter,
        uiState.isSpendChartError,
        uiState.isSpendChartLoading
    ) {
        val highestExpense = {
            val amount = uiState.spendData.maxOfOrNull { it.totalAmount } ?: 0.0
            CurrencyUtils.formattedAmount(amount, getSafeDefaultCurrencyCode())
        }
        GraphItemState(
            title = "Spend",
            amountString = highestExpense(),
            filter = uiState.expenseTimeFilter,
            filters = TimeFilter.entries,
            isError = uiState.isSpendChartError,
            isLoading = uiState.isSpendChartLoading
        )
    }


    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                it,
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Analytics",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            )

        },
        bottomBar = {
            BottomAppBar {
                ExpenseTrackerNavigationBar(
                    selectedItem = MainRoute.Analytics(profileId),
                    onNavigationItemClick = onNavigationItemClick,
                    profileOwnerId = profileId,
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (isEmpty) {
                true -> item {
                    EmptyScreen(
                        modifier = Modifier.fillParentMaxWidth(),
                        model = R.drawable.broken_bar_chart,
                        text = "Your expense history is empty, So no analysis is available",
                        button = {
                            Button(onClick = {}) {
                                Text("Refresh")
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh"
                                )
                            }

                        }
                    )
                }

                false -> {
                    item {
                        if (uiState.spendData.isNotEmpty()) {
                            GraphItem(
                                state = spendState,
                                onFilterChange = contentActions.onExpenseFilterChange,
                            ) {
                                LineChart(
                                    modifier = Modifier
                                        .height(220.dp),
                                    expense = uiState.spendData
                                )
                            }
                        }
                    }

                    item {
                        if (uiState.incomeData.isNotEmpty()) {
                            GraphItem(
                                state = incomeState,
                                onFilterChange = contentActions.onIncomeFilterChange,
                            ) {
                                LineChart(
                                    modifier = Modifier
                                        .height(220.dp),
                                    expense = uiState.incomeData
                                )
                            }
                        }
                    }


                    item {
                        HorizontalDivider()
                    }

                    item {
                        FilterItem(
                            title = stringResource(R.string.categories),
                            filter = uiState.categoryTimeFilter,
                            typeFilter = uiState.categoryExpenseType,
                            onFilterChange = contentActions.onCategoryFilterChange,
                            onTypeFilterChange = contentActions.onCategoryTypeFilterChange
                        )

                    }
                    item {
                        PieGraphItem(
                            data = uiState.categoryExpenseData,
                            onItemClick = contentActions.onCategoryItemClick
                        )
                    }

                    item {
                        HorizontalDivider()
                    }

                    item {
                        FilterItem(
                            title = stringResource(R.string.account),
                            filter = uiState.accountTimeFilter,
                            typeFilter = uiState.accountExpenseType,
                            onFilterChange = contentActions.onAccountFilterChange,
                            onTypeFilterChange = contentActions.onAccountTypeFilterChange
                        )

                    }
                    item {
                        PieGraphItem(
                            data = uiState.accountExpenseData,
                            onItemClick = contentActions.onAccountItemClick
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun PieGraphItem(
    modifier: Modifier = Modifier,
    data: List<ExpenseChartData>,
    onItemClick: (String) -> Unit
) {
    if (data.isEmpty()){
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = R.drawable.broken_pie_chart,
                contentDescription = "No data"
            )
            Text(text = "No data to plot chart", style = MaterialTheme.typography.titleLarge)
        }

        return
    }
    // This `remember` block is efficient and correct.
    val pieChartData = remember(data) {
        val colors: List<Color> = CategoryDefaults.categoryColors
        if (colors.isEmpty()) return@remember emptyList()

        data.mapIndexed { index, chartData ->
            ChartItem(
                label = chartData.label ?: "",
                amount = chartData.totalAmount,
                color = colors[index % colors.size]
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 2. Use BoxWithConstraints to get available space and create a responsive layout.
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Define a breakpoint to switch between Row and Column.
                val isWideScreen = this.maxWidth > 400.dp

                if (isWideScreen) {
                    // --- WIDE LAYOUT: Chart and Legend side-by-side ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PieChart(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f), // Fills height and stays square
                            data = pieChartData
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        // 3. Make the legend scrollable using LazyColumn.
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pieChartData.forEach { item ->
                                SimplePieChartItem(
                                    item = item,
                                    onItemClick = onItemClick,
                                )
                            }
                        }
                    }
                } else {
                    // --- NARROW LAYOUT: Chart on top of Legend ---
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PieChart(
                            modifier = Modifier
                                .padding(horizontal = 40.dp, vertical = 0.dp)
                                .fillMaxWidth(1f) // Takes 70% of the width
                                .aspectRatio(1f),
                            data = pieChartData,
                            backgroundColor = CardDefaults.cardColors().containerColor
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pieChartData.forEach { item ->
                                SimplePieChartItem(
                                    item = item,
                                    onItemClick = onItemClick,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SimplePieChartItem(
    modifier: Modifier = Modifier,
    item: ChartItem,
    onItemClick: (String) -> Unit,
) {
    val amountText = remember(item.amount) {
        val formattedBalance = CurrencyUtils.formattedAmount(
            item.amount,
            getSafeDefaultCurrencyCode()
        )
        formattedBalance
    }
    Row(
        modifier = modifier.clickable { onItemClick(item.label) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(item.color)
        ) {}

        Text(
            text = item.label,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = amountText,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}


@Composable
private fun FilterItem(
    modifier: Modifier = Modifier,
    title: String,
    filter: TimeFilter,
    typeFilter: ExpenseType,
    onFilterChange: (TimeFilter) -> Unit,
    onTypeFilterChange: (ExpenseType) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f)
        )
        EnumDropDownButton(
            filter = filter,
            filters = TimeFilter.entries.toList(),
            onFilterChange = onFilterChange
        )
        EnumDropDownButton(
            filter = typeFilter,
            filters = ExpenseType.entries.toList(),
            onFilterChange = onTypeFilterChange
        )
    }

}








