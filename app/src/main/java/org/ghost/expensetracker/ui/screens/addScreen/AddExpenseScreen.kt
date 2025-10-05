package org.ghost.expensetracker.ui.screens.addScreen

//import androidx.compose.material3.ExposedDropdownMenuBoxScope.menuAnchor
//import androidx.hilt.navigation.compose.hiltViewModel
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.core.ui.actions.AddExpenseScreenActions
import org.ghost.expensetracker.core.ui.states.AddExpenseUiState
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.viewModels.addScreen.AddExpenseViewModel
import org.ghost.expensetracker.ui.components.CategoriesContent
import org.ghost.expensetracker.ui.components.ErrorSnackBar
import org.ghost.expensetracker.ui.components.WideAccountItem
import org.ghost.expensetracker.ui.components.WideCardItem
import org.ghost.expensetracker.ui.screens.main.ErrorCard


@Composable
fun AddExpenseScreen(
    modifier: Modifier = Modifier,
    viewModel: AddExpenseViewModel = hiltViewModel(),
    onExpenseSaved: () -> Unit, // Navigation callback,
    onNavigateBackClick: () -> Unit,
    onAddNewCategory: (Long) -> Unit,
    onAddNewAccount: (Long) -> Unit,
    onAddNewCard: (Long) -> Unit,
) {
    val context: Context = LocalContext.current
    val profileOwnerId: Long = viewModel.profileOwnerId
    val uiState: AddExpenseUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val accountsState: UiState<List<Account>> by viewModel.accountsState.collectAsStateWithLifecycle()
    val cardsState: UiState<List<Card>> by viewModel.cardsState.collectAsStateWithLifecycle()
    val categoriesState: UiState<List<Category>> by viewModel.categoriesState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isExpenseSaved) {
        if (uiState.isExpenseSaved) {
            Toast.makeText(context, context.getString(R.string.expense_saved), Toast.LENGTH_SHORT)
                .show()
            onExpenseSaved()
        }
    }
    val actions = remember {
        AddExpenseScreenActions(
            onNavigateBackClick = onNavigateBackClick,
            onIsSendChange = viewModel::onIsSendChange,
            onCategoryChange = viewModel::onCategoryChange,
            onAccountChange = viewModel::onAccountChange,
            onCardChange = viewModel::onCardChange,
            onAmountChange = viewModel::onAmountChange,
            onCurrencyChange = viewModel::onCurrencyChange,
            onTitleChange = viewModel::onTitleChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onSourceDueChange = viewModel::onSourceDueChange,
            onIconIdChange = viewModel::onIconIdChange,
            onImageUriChange = viewModel::onImageUriChange,
            addExpense = viewModel::addExpense,
            onAddNewCategory = { onAddNewCategory(profileOwnerId) },
            onAddNewAccount = { onAddNewAccount(profileOwnerId) },
            onAddNewCard = { onAddNewCard(profileOwnerId) }
        )
    }

    AddExpenseContent(
        modifier = modifier,
        uiState = uiState,
        accountsState = accountsState,
        cardsState = cardsState,
        categoriesState = categoriesState,
        actions = actions
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseContent(
    modifier: Modifier = Modifier,
    uiState: AddExpenseUiState,
    accountsState: UiState<List<Account>>,
    cardsState: UiState<List<Card>>,
    categoriesState: UiState<List<Category>>,
    actions: AddExpenseScreenActions,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }


    val isFloatingButtonEnable = remember(
        uiState.isTitleError,
        uiState.isAmountError,
        key3 = uiState.isCategoryError,
    ) {
        !(uiState.isTitleError || uiState.isAmountError || uiState.isCategoryError)
    }

    // Show error snackbar
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
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                ErrorSnackBar(snackbarData = snackbarData)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.add_expense),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = actions.onNavigateBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isFloatingButtonEnable) {
                FloatingActionButton(
                    onClick = {
                        isBottomSheetVisible = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save expense"
                    )
                }
            } else {
                FloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save expense"
                    )
                }
            }

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TransactionTypeSelector(
                isSend = uiState.isSend,
                onIsSendChange = actions.onIsSendChange,
            )
            AmountContent(
                amount = uiState.amount,
                onAmountChange = actions.onAmountChange,
                isError = uiState.isAmountError,
                focusRequester = focusRequester
            )
            TitleContent(
                value = uiState.title,
                onValueChange = actions.onTitleChange,
                isError = uiState.isTitleError,
            )
            CategoriesContent(
                selectedCategoryId = uiState.category?.id,
                allowAddCategory = true,
                categoriesState = categoriesState,
                onCategoryClick = actions.onCategoryChange,
                onAddNewClick = actions.onAddNewCategory,
                isCategoryError = uiState.isCategoryError
            )
            MoreDetailsContent(
                modifier = Modifier.weight(1f),
                description = uiState.description ?: "",
                onDescriptionChange = actions.onDescriptionChange,
                isError = false,
            )
        }
        if (isBottomSheetVisible) {
            ExpenseBottomSheet(
                account = uiState.account,
                card = uiState.card,
                accountsState = accountsState,
                cardsState = cardsState,
                onAccountClick = actions.onAccountChange,
                onCardClick = actions.onCardChange,
                onSaveRequest = actions.addExpense,
                onDismissRequest = { isBottomSheetVisible = false },
                onAddNewAccount = actions.onAddNewAccount,
                onAddNewCard = actions.onAddNewCard,
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseBottomSheet(
    account: Account?,
    card: Card?,
    accountsState: UiState<List<Account>>,
    cardsState: UiState<List<Card>>,
    onAccountClick: (Account) -> Unit,
    onCardClick: (Card) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveRequest: () -> Unit,
    onAddNewAccount: () -> Unit,
    onAddNewCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SourceTypeSelector(
                isAccount = pagerState.currentPage == 0,
                onIsAccountSelected = { isAccount ->
                    when (isAccount) {
                        true -> scope.launch { pagerState.animateScrollToPage(0) }
                        false -> scope.launch { pagerState.animateScrollToPage(1) }
                    }
                }
            )
            HorizontalPager(
                state = pagerState
            ) {
                when (it) {
                    0 -> {
                        AccountContent(
                            currentAccount = account,
                            accountState = accountsState,
                            onClick = onAccountClick,
                            onAddNewClick = onAddNewAccount
                        )
                    }

                    1 -> {
                        CardContent(
                            currentCard = card,
                            cardState = cardsState,
                            onClick = onCardClick,
                            onAddNewClick = onAddNewCard
                        )
                    }
                }
            }
            Button(
                onClick = {
                    onSaveRequest()
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun CardContent(
    modifier: Modifier = Modifier,
    currentCard: Card?,
    cardState: UiState<List<Card>>,
    onClick: (Card) -> Unit,
    onAddNewClick: () -> Unit,
) {
    when (cardState) {
        is UiState.Loading -> {
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            ErrorCard(message = cardState.message)
        }

        is UiState.Success -> {

            val cards = cardState.data

            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AddNewButton(
                        text = stringResource(R.string.new_credit_or_debit_card),
                        contentDescription = stringResource(R.string.add_new_card),
                        modifier = Modifier,
                        onClick = onAddNewClick
                    )
                }
                item {
                    HorizontalDivider()
                }
                items(items = cards, key = { it.id }) { card ->
                    if (card.id == currentCard?.id) {
                        OutlinedCard(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            WideCardItem(
                                card = card,
                                onClick = onClick,
                            )
                        }
                    } else {
                        WideCardItem(
                            card = card,
                            onClick = onClick,
                        )
                    }

                }
            }


        }
    }
}

@Composable
private fun AccountContent(
    modifier: Modifier = Modifier,
    currentAccount: Account?,
    accountState: UiState<List<Account>>,
    onClick: (Account) -> Unit,
    onAddNewClick: () -> Unit,
) {
    when (accountState) {
        is UiState.Loading -> {
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            ErrorCard(message = accountState.message)
        }

        is UiState.Success -> {
            val accounts = accountState.data

            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AddNewButton(
                        text = stringResource(R.string.add_new_account),
                        contentDescription = stringResource(R.string.add_new_account),
                        modifier = Modifier,
                        onClick = onAddNewClick,
                    )
                }
                item {
                    HorizontalDivider()
                }
                items(items = accounts, key = { it.id }) { account ->
                    if (account.id == currentAccount?.id) {
                        OutlinedCard(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            WideAccountItem(
                                account = account,
                                onClick = onClick,
                            )
                        }
                    } else {
                        WideAccountItem(
                            account = account,
                            onClick = onClick,
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun AddNewButton(
    modifier: Modifier = Modifier,
    text: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun AmountContent(
    modifier: Modifier = Modifier,
    amount: String,
    isError: Boolean,
    focusRequester: FocusRequester,
    onAmountChange: (String) -> Unit,
) {
    // Amount input

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(stringResource(R.string.amount), style = MaterialTheme.typography.titleLarge)
        TextField(
            value = amount,
            onValueChange = onAmountChange,
            placeholder = { Text("0.00", style = MaterialTheme.typography.headlineMedium) },
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            isError = isError,
            textStyle = MaterialTheme.typography.headlineMedium
        )
    }


}

@Composable
private fun TitleContent(
    modifier: Modifier = Modifier,
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.title),
            style = MaterialTheme.typography.titleLarge,
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text("Enter a title...") },
            modifier = Modifier
                .fillMaxWidth(),
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
    }
}


@Composable
private fun MoreDetailsContent(
    modifier: Modifier = Modifier,
    description: String,
    isError: Boolean,
    onDescriptionChange: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.more_details),
            style = MaterialTheme.typography.titleLarge,
        )
        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text(stringResource(R.string.more_detail_description)) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { }
            )
        )
    }
}


// --- UI Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionTypeSelector(
    isSend: Boolean,
    onIsSendChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(stringResource(R.string.expenses), stringResource(R.string.income))
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onIsSendChange(index == 0) },
                selected = (index == 0) == isSend
            ) {
                Text(label)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourceTypeSelector(
    isAccount: Boolean,
    onIsAccountSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(stringResource(R.string.account), stringResource(R.string.card))
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onIsAccountSelected(index == 0) },
                selected = (index == 0) == isAccount
            ) {
                Text(label)
            }
        }
    }
}


@Composable
private fun ImageAttachment(
    imageUri: Uri?,
    onImageUriChange: (Uri?) -> Unit
) {
    // In a real app, this would launch an image picker
    // val launcher = rememberLauncherForActivityResult(...) { uri -> onImageUriChange(uri) }

    if (imageUri == null) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        ) {
            Icon(Icons.Default.AccountBox, contentDescription = null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.add_receipt_image))
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = stringResource(R.string.receipt_image),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { onImageUriChange(null) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.remove_image),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}