package org.ghost.expensetracker.ui.screens.secondary

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.actions.AccountsScreenActions
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.core.ui.states.AccountsUiState
import org.ghost.expensetracker.data.viewModels.secondary.AccountsViewModel
import org.ghost.expensetracker.ui.components.AddItemTopBar
import org.ghost.expensetracker.ui.components.ConfirmDeleteDialog
import org.ghost.expensetracker.ui.components.DragHandle
import org.ghost.expensetracker.ui.components.DraggableAccountItem
import org.ghost.expensetracker.ui.components.ErrorSnackBar
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountsViewModel = hiltViewModel(),
    onNavigateToAccount: (Account) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = AccountsScreenActions(
        onMove = viewModel::move,
        onDelete = viewModel::deleteAccount,
        onEdit = viewModel::updateAccount,
        onAdd = {}
    )

    AccountsScreenContent(
        modifier = modifier,
        profileId = viewModel.profileOwnerId,
        uiState = uiState,
        actions = actions,
        onNavigateBack = onNavigateBack,
        onNavigateToAccount = onNavigateToAccount
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreenContent(
    modifier: Modifier = Modifier,
    profileId: Long,
    uiState: AccountsUiState,
    actions: AccountsScreenActions,
    onNavigateBack: () -> Unit,
    onNavigateToAccount: (Account) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var deletingAccount: Account? by remember { mutableStateOf(null) }
    var editingAccount: Account? by remember { mutableStateOf(null) }
    var isCreateDialogVisible by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        actions.onMove(from.index - 1, to.index - 1)
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(
                uiState.error,
                withDismissAction = true,
                duration = SnackbarDuration.Long,
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.accounts)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }

            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            ) { snackbarData ->
                ErrorSnackBar(snackbarData = snackbarData)

            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AddItemTopBar(
                    title = stringResource(R.string.add_accounts),
                    onAddNewClick = { isCreateDialogVisible = true },
                )
            }
            items(items = uiState.accounts, key = { it.id }) { account ->
                ReorderableItem(reorderableLazyListState, key = account.id) {
                    val interactionSource = remember { MutableInteractionSource() }
                    DraggableAccountItem(
                        modifier = Modifier.fillMaxWidth(),
                        card = account,
                        onClick = onNavigateToAccount,
                        onEditClick = {
                            editingAccount = account
                        },
                        onDeleteClick = {
                            deletingAccount = account
                        },
                        dragHandler = {
                            DragHandle(
                                modifier = Modifier.draggableHandle(

                                    onDragStarted = {

                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                    },
                                    onDragStopped = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                    },
                                ),
                                interactionSource = interactionSource
                            )
                        }
                    )
                }
            }
        }
    }
    if (deletingAccount != null) {
        ConfirmDeleteDialog(
            onDismissRequest = { deletingAccount = null },
            onConfirm = {
                actions.onDelete(deletingAccount!!)
                deletingAccount = null
            },
            title = stringResource(R.string.delete_account),
            text = stringResource(R.string.delete_account_message),
            icon = Icons.Outlined.Warning,
        )
    }
    if (editingAccount != null) {
        EditAccountDialog(
            account = editingAccount!!,
            onDismiss = { editingAccount = null },
            onUpdate = {
                actions.onEdit(it)
                editingAccount = null
            }
        )
    }
    if (isCreateDialogVisible) {
        CreateAccountDialog(
            onDismiss = { isCreateDialogVisible = false },
            onCreate = { name, description, currency, balance ->
                isCreateDialogVisible = false
                actions.onAdd(
                    Account(
                        id = 0,
                        profileOwnerId = profileId,
                        name = name,
                        description = description,
                        currency = currency,
                        balance = balance,
                        isDefault = false,
                        displayOrder = 0
                    )
                )
            }
        )
    }

}

@Composable
fun EditAccountDialog(
    modifier: Modifier = Modifier,
    account: Account,
    onDismiss: () -> Unit,
    onUpdate: (Account) -> Unit
) {
    // State for the text fields, initialized with the account's current data.
    // `rememberSaveable` ensures the state survives configuration changes.
    var name by rememberSaveable { mutableStateOf(account.name) }
    var description by rememberSaveable { mutableStateOf(account.description ?: "") }

    // State for validation. The name cannot be blank.
    var isNameError by rememberSaveable { mutableStateOf(false) }

    val validateName = {
        isNameError = name.isBlank()
    }

    // Determine if any data has actually changed.
    val hasChanges = name != account.name || description != (account.description ?: "")

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Edit, contentDescription = "Edit Icon") },
        title = { Text("Edit Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Name Text Field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        // Validate in real-time as the user types
                        validateName()
                    },
                    label = { Text("Account Name") },
                    singleLine = true,
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text(
                                text = "Name cannot be empty",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description Text Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    validateName()
                    if (!isNameError) {
                        val updatedAccount = account.copy(
                            name = name.trim(),
                            // Set description to null if blank, otherwise use the trimmed string
                            description = description.trim().takeIf { it.isNotEmpty() }
                        )
                        onUpdate(updatedAccount)
                    }
                },
                // The button is enabled only if the name is not blank AND there are changes.
                enabled = !isNameError && hasChanges
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun EditAccountDialogPreview() {
    MaterialTheme {
        EditAccountDialog(
            account = Account(
                id = 1,
                profileOwnerId = 1,
                name = "Savings Account",
                description = "For monthly savings and emergency fund.",
                currency = "USD",
                balance = 12500.75,
                isDefault = true,
                displayOrder = 1
            ),
            onDismiss = {},
            onUpdate = {}
        )
    }
}


@Composable
fun CreateAccountDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String?, currency: String, balance: Double) -> Unit
) {
    // State for the text fields
    var name by rememberSaveable { mutableStateOf("") }
    var initialBalance by rememberSaveable { mutableStateOf("0.0") }
    var currency by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    // State for validation errors
    var isNameError by rememberSaveable { mutableStateOf(false) }
    var isBalanceError by rememberSaveable { mutableStateOf(false) }
    var isCurrencyError by rememberSaveable { mutableStateOf(false) }

    // This function validates all fields and updates the error states
    fun validateFields() {
        isNameError = name.isBlank()
        isCurrencyError = currency.isBlank()
        isBalanceError = initialBalance.toDoubleOrNull() == null
    }

    // `LaunchedEffect` runs validation once when the dialog is first displayed
    LaunchedEffect(Unit) {
        validateFields()
    }

    val canCreate = !isNameError && !isBalanceError && !isCurrencyError

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Add, contentDescription = "Create Icon") },
        title = { Text("Create New Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Name Text Field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isNameError = it.isBlank()
                    },
                    label = { Text("Account Name") },
                    singleLine = true,
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text("Name cannot be empty", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Initial Balance Text Field
                OutlinedTextField(
                    value = initialBalance,
                    onValueChange = {
                        initialBalance = it
                        isBalanceError = it.toDoubleOrNull() == null
                    },
                    label = { Text("Initial Balance") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isBalanceError,
                    supportingText = {
                        if (isBalanceError) {
                            Text("Invalid number", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Currency Text Field
                // Note: For a better UX, consider replacing this with an ExposedDropdownMenuBox
                // to let users select from a list of valid currencies.
                OutlinedTextField(
                    value = currency,
                    onValueChange = {
                        currency = it.uppercase() // Currencies are typically uppercase
                        isCurrencyError = it.isBlank()
                    },
                    label = { Text("Currency (e.g., USD)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    isError = isCurrencyError,
                    supportingText = {
                        if (isCurrencyError) {
                            Text(
                                "Currency cannot be empty",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description Text Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Final validation check before creation
                    validateFields()
                    if (canCreate) {
                        onCreate(
                            name.trim(),
                            description.trim().takeIf { it.isNotEmpty() },
                            currency.trim(),
                            initialBalance.toDouble() // Safe to call, validated by canCreate
                        )
                    }
                },
                // The button is enabled only when all required fields are valid
                enabled = canCreate
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateAccountDialogPreview() {
    MaterialTheme {
        CreateAccountDialog(
            onDismiss = {},
            onCreate = { _, _, _, _ -> }
        )
    }
}