package org.ghost.expensetracker.ui.screens.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.core.ui.actions.HomeScreenActions
import org.ghost.expensetracker.core.utils.CurrencyUtils
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.viewModels.main.HomeScreenViewModel
import org.ghost.expensetracker.ui.components.ExpenseItem
import org.ghost.expensetracker.ui.components.SimpleCardItem
import org.ghost.expensetracker.ui.navigation.ExpenseTrackerNavigationBar
import org.ghost.expensetracker.ui.navigation.MainRoute
import org.ghost.expensetracker.ui.navigation.SecondaryRoute
import org.ghost.expensetracker.ui.screens.secondary.EmptyScreen
import org.ghost.expensetracker.ui.screens.secondary.ErrorItem

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    actions: HomeScreenActions,
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val accountsState by viewModel.accountsState.collectAsStateWithLifecycle()
    val cardsState by viewModel.cardsState.collectAsStateWithLifecycle()
    val expensesLazyPagingItems = viewModel.expensesPagerFlow.collectAsLazyPagingItems()
    val expenseFilters by viewModel.expenseFilters.collectAsStateWithLifecycle()

    HomeScreenContent(
        profileId = viewModel.profileId,
        modifier = modifier,
        profileState = profileState,
        accountsState = accountsState,
        cardsState = cardsState,
        expensesLazyPagingItems = expensesLazyPagingItems,
        expenseFilters = expenseFilters,
        actions = actions,
    )

}

@Composable
private fun HomeScreenContent(
    profileId: Long,
    modifier: Modifier = Modifier,
    profileState: UiState<Profile>,
    accountsState: UiState<List<Account>>,
    cardsState: UiState<List<Card>>,
    expensesLazyPagingItems: LazyPagingItems<Expense>,
    expenseFilters: ExpenseFilters,
    actions: HomeScreenActions,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar {
                ExpenseTrackerNavigationBar(
                    selectedItem = MainRoute.Home(profileId),
                    onNavigationItemClick = actions.onNavigationItemClick,
                    profileOwnerId = profileId,
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HomeProfileScreen(
                modifier = Modifier,
                profileState = profileState,
                notificationCount = 0,
                onNotificationClick = actions.onNotificationClick
            )
            HomeAccountScreen(
                modifier = Modifier,
                accountsState = accountsState
            )
            HomeCardsScreen(
                modifier = Modifier,
                cardsState = cardsState,
                onAddNewCardClick = { actions.onAddNewCardClick(profileId) },
                onCardClick = actions.onCardClick
            )
            SendReceiveContent(
                onSendClick = { actions.onSendClick(profileId) },
                onRequestClick = { actions.onRequestClick(profileId) },
                onSeeAllCategoriesClick = {
                    actions.onNavigationItemClick(
                        SecondaryRoute.Category(
                            profileId
                        )
                    )
                }
            )
            HorizontalDivider()

            HomeRecentActivityContent(
                modifier = Modifier,
                expensesLazyPagingItems = expensesLazyPagingItems,
                onExpenseClick = actions.onExpenseClick,
                onSeeAllClick = { actions.onSeeAllExpenseClick(profileId, expenseFilters) },
            )

        }
    }
}

@Composable
fun ErrorCard(modifier: Modifier = Modifier, message: String) {
    SelectionContainer {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}

@Composable
private fun HomeProfileScreen(
    modifier: Modifier = Modifier,
    profileState: UiState<Profile>,
    notificationCount: Int,
    onNotificationClick: () -> Unit
) {
    when (profileState) {
        is UiState.Loading -> {
            // Show a loading indicator or placeholder
            CircularProgressIndicator()
        }

        is UiState.Error -> {
            // Show an error message
            ErrorCard(
                modifier = modifier,
                message = profileState.message
            )
        }

        is UiState.Success -> {
            val profile = profileState.data
            SimpleProfileContent(
                profile = profile,
                notificationCount = notificationCount,
                onNotificationClick = onNotificationClick
            )
        }
    }
}

@Composable
fun SimpleProfileContent(
    modifier: Modifier = Modifier,
    profile: Profile,
    notificationCount: Int,
    onNotificationClick: () -> Unit
) {
    val badgeText = remember(notificationCount) {
        if (notificationCount > 99) "99+" else notificationCount.toString()
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.hi) + ", ${profile.firstName}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.size(58.dp)
        ) {
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge {
                            Text(
                                text = badgeText,
                                modifier = Modifier.semantics {
                                    contentDescription = "New notifications"
                                }
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notification)
                )
            }
        }

    }
}


@Composable
private fun HomeAccountScreen(
    modifier: Modifier = Modifier,
    accountsState: UiState<List<Account>>,
) {
    when (accountsState) {
        is UiState.Loading -> {
            // Show a loading indicator or placeholder
            CircularProgressIndicator()
        }

        is UiState.Error -> {
            ErrorCard(
                modifier = modifier,
                message = accountsState.message
            )
        }

        is UiState.Success -> {
            val accounts = accountsState.data
            val state = rememberPagerState { accounts.size }
            HomeAccountContent(
                modifier = modifier,
                accounts = accounts,
                state = state
            )
        }
    }
}

@Composable
fun HomeAccountContent(
    modifier: Modifier = Modifier,
    accounts: List<Account>,
    state: PagerState
) {
    HorizontalPager(
        modifier = modifier,
        state = state
    ) { page ->
        AccountBalanceItem(
            account = accounts[page]
        )
    }
}

private val digitRegex = Regex("\\d")

@Composable
fun AccountBalanceItem(modifier: Modifier = Modifier, account: Account) {
    var isBalanceVisible by remember { mutableStateOf(true) }
//    val balanceText by
    val balanceText by remember(isBalanceVisible, account.currency, account.balance) {
        derivedStateOf {
            // 1. Format the balance into a currency string first.
            // Example output: "₹5,000.00"
            val formattedBalance = CurrencyUtils.formattedAmount(account.balance, account.currency)

            // 2. Apply the visibility logic to the formatted string.
            if (isBalanceVisible) {
                formattedBalance
            } else {
                "✶✶✶✶" // Example output: "₹*,***.**"
            }
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = account.name + " Balance",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = balanceText,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
            )
        }
        IconButton(
            onClick = { isBalanceVisible = !isBalanceVisible },
        ) {
            if (isBalanceVisible) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.rounded_visibility_24),
                    contentDescription = stringResource(R.string.show_balance)
                )
            } else {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.rounded_visibility_off_24),
                    contentDescription = stringResource(R.string.hide_balance)
                )
            }
        }
    }

}

@Composable
fun HomeCardsScreen(
    modifier: Modifier = Modifier,
    cardsState: UiState<List<Card>>,
    onAddNewCardClick: () -> Unit,
    onCardClick: (Card) -> Unit
) {
    when (cardsState) {
        is UiState.Loading -> {
            // Show a loading indicator or placeholder
            CircularProgressIndicator()
        }

        is UiState.Error -> {
            ErrorCard(
                modifier = modifier,
                message = cardsState.message
            )
        }

        is UiState.Success -> {
            val cards = cardsState.data
            CardsContent(
                modifier = modifier,
                cards = cards,
                onAddNewCardClick = onAddNewCardClick,
                onCardClick = onCardClick
            )
        }
    }
}

@Composable
fun CardsContent(
    modifier: Modifier = Modifier,
    cards: List<Card>,
    cardSize: DpSize = DpSize(105.dp, 65.dp),
    onAddNewCardClick: () -> Unit,
    onCardClick: (Card) -> Unit
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.cards),
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Spacer(modifier = Modifier.width(16.dp))
        }

        item {
            IconButton(
                onClick = onAddNewCardClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.5f))
            ) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.add_new_card),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        items(items = cards, key = { it.id }) { card ->
            SimpleCardItem(
                modifier = Modifier.size(cardSize),
                card = card,
                onClick = onCardClick
            )
        }
    }
}

@Composable
fun SendReceiveContent(
    modifier: Modifier = Modifier,
    onSendClick: () -> Unit,
    onRequestClick: () -> Unit,
    onSeeAllCategoriesClick: () -> Unit,
) {
    val buttonHeight: Dp = 52.dp
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onSendClick,
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight)
        ) {
            Icon(

                painter = painterResource(R.drawable.rounded_call_received_24),
                contentDescription = stringResource(R.string.send)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.send),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Button(
            onClick = onRequestClick,
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_call_made_24),
                contentDescription = stringResource(R.string.request),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.request),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        IconButton(
            onClick = onSeeAllCategoriesClick,
            modifier = Modifier
                .size(buttonHeight)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        ) {
            Icon(
                painterResource(R.drawable.rounded_background_dot_large_24),
                contentDescription = stringResource(R.string.categories)
            )
        }
    }
}


@Composable
fun HomeRecentActivityContent(
    modifier: Modifier = Modifier,
    expensesLazyPagingItems: LazyPagingItems<Expense>,
    onExpenseClick: (Expense) -> Unit,
    onSeeAllClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.recent_activity))
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onSeeAllClick,
            ) {
                Text("See all")
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
        HomeExpenseListItem(
            modifier = Modifier,
            expensesPagingItems = expensesLazyPagingItems,
            onExpenseClick = onExpenseClick
        )
    }
}

@Composable
fun HomeExpenseListItem(
    modifier: Modifier = Modifier,
    expensesPagingItems: LazyPagingItems<Expense>,
    onExpenseClick: (Expense) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (val refreshState = expensesPagingItems.loadState.refresh){
            is LoadState.Error -> {
                val e = expensesPagingItems.loadState.refresh as LoadState.Error
                item {
                    ErrorItem(
                        modifier = modifier,
                        message = e.error.message ?: "Unknown error occurred",
                        showRefreshButton = true,
                        onRefreshClick = { expensesPagingItems.retry() }
                    )
                }
            }
            LoadState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            is LoadState.NotLoading -> {
                if(expensesPagingItems.itemCount == 0){
                    item {
                        EmptyScreen(
                            modifier = modifier.fillMaxWidth().height(350.dp),
                            model = R.drawable.dog,
                            text = "No recent activity",
                            button = null,
                        )
                    }
                }else{
                    items(
                        minOf(expensesPagingItems.itemCount, 5),
                        key = { expensesPagingItems[it]?.id ?: it }) {
                        val expense = expensesPagingItems[it]
                        if (expense != null) {
                            ExpenseItem(
                                modifier = modifier,
                                expense = expense,
                                onClick = onExpenseClick,
                                isSelected = false,
                                onLongClick = {}
                            )
                        }
                    }
                }

            }
        }

    }
}