package org.ghost.expensetracker.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.enums.CardSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.core.utils.DateTimeUtils
import org.ghost.expensetracker.core.utils.ExpiryDateVisualTransformation
import org.ghost.expensetracker.core.utils.isValidHex
import org.ghost.expensetracker.core.utils.toColor
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.viewModels.main.CardsUiState
import org.ghost.expensetracker.data.viewModels.main.CardsViewModel
import org.ghost.expensetracker.ui.components.AddItemTopBar
import org.ghost.expensetracker.ui.components.ConfirmDeleteDialog
import org.ghost.expensetracker.ui.components.DragHandle
import org.ghost.expensetracker.ui.components.DraggableCardItem
import org.ghost.expensetracker.ui.navigation.AppRoute
import org.ghost.expensetracker.ui.navigation.ExpenseTrackerNavigationBar
import org.ghost.expensetracker.ui.navigation.MainRoute
import org.ghost.expensetracker.ui.screens.secondary.EmptyScreen
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

data class CardsScreenActions(
    val onNavigationItemClick: (AppRoute) -> Unit,
    val onAddCardClick: (Long) -> Unit,
    val onCardClick: (Card) -> Unit,
)

data class CardsScreenContentActions(
    val onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    val onDelete: (card: Card) -> Unit,
    val onEdit: (card: Card) -> Unit,
    val onSortByChange: (sortBy: CardSortBy) -> Unit,
    val onSortOrderChange: (sortOrder: SortOrder) -> Unit,
    val onQueryChange: (query: String) -> Unit,
)

@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    viewModel: CardsViewModel = hiltViewModel(),
    actions: CardsScreenActions,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val contentActions = remember {
        CardsScreenContentActions(
            onMove = viewModel::move,
            onDelete = viewModel::deleteCard,
            onEdit = viewModel::updateCard,
            onSortByChange = viewModel::onSortByChanged,
            onSortOrderChange = viewModel::onSortOrderChanged,
            onQueryChange = viewModel::onQueryChanged,
        )
    }

    CardsScreenContent(
        modifier = modifier,
        profileId = 1,
        uiState = uiState,
        contentActions = contentActions,
        actions = actions,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreenContent(
    modifier: Modifier = Modifier,
    profileId: Long,
    uiState: CardsUiState,
    contentActions: CardsScreenContentActions,
    actions: CardsScreenActions,
) {
    var deletingCard: Card? by remember { mutableStateOf(null) }
    var editingCard: Card? by remember { mutableStateOf(null) }
    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        contentActions.onMove(from.index - 1, to.index - 1)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.cards)) },
            )
        },
        bottomBar = {
            BottomAppBar {
                ExpenseTrackerNavigationBar(
                    selectedItem = MainRoute.Cards(profileId),
                    onNavigationItemClick = actions.onNavigationItemClick,
                    profileOwnerId = profileId,
                )
            }
        }
    ) { innerPadding ->
        if (uiState.cards.isEmpty()){
            EmptyScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                model = R.drawable.broken_card,
                contentDescription = "No cards found",
                text = stringResource(R.string.empty_card_list_message),
                button = {
                    Button( onClick = { actions.onAddCardClick(profileId) } ){
                        Text(stringResource(R.string.add_card))
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_card)
                        )
                    }
                }
            )
        }
        else{
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TextField(
                        value = uiState.query,
                        onValueChange = contentActions.onQueryChange,
                        shape = CircleShape,
                        label = { Text("Search") },
                        placeholder = { Text("John, etc") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.card),
                                contentDescription = "card"
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "clear"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )

                }
                item {
                    AddItemTopBar(
                        title = "Add Debit\nCredit Card",
                        onAddNewClick = { actions.onAddCardClick(profileId) },
                    )
                }

                items(items = uiState.cards, key = { it.id }) { card ->
                    ReorderableItem(reorderableLazyListState, key = card.id) {
                        val interactionSource = remember { MutableInteractionSource() }
                        DraggableCardItem(
                            modifier = Modifier.fillMaxWidth(),
                            card = card,
                            onClick = actions.onCardClick,
                            onEditClick = {
                                editingCard = card
                            },
                            onDeleteClick = {
                                deletingCard = card
                            },
                            dragHandler = {
                                if (uiState.sortBy == CardSortBy.DISPLAY_ORDER) {
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
                            },
                        )
                    }

                }

            }
        }

    }
    if (deletingCard != null) {
        ConfirmDeleteDialog(
            onDismissRequest = { deletingCard = null },
            onConfirm = {
                contentActions.onDelete(deletingCard!!)
                deletingCard = null
            },
            title = stringResource(R.string.delete_card),
            text = stringResource(R.string.delete_card_message),
            icon = Icons.Outlined.Warning,
        )
    }
    if (editingCard != null) {
        EditCardDialog(
            card = editingCard!!,
            onDismiss = { editingCard = null },
            onEdit = {
                contentActions.onEdit(it)
                editingCard = null
            }
        )
    }
}


//**
//* A Material 3 dialog for editing the details of a Card.
//*
//* This dialog manages its own state for input fields and performs validation.
//* The `onEdit` callback is invoked with the updated card object only when
//* the form is valid and the user taps "Save".
//*
//* @param card The initial card data to be edited.
//* @param onDismiss Request to close the dialog without saving.
//* @param onEdit Callback with the updated card data when the user saves.
//*/
//@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCardDialog(
    modifier: Modifier = Modifier,
    card: Card,
    onDismiss: () -> Unit,
    onEdit: (Card) -> Unit
) {
    // --- State Management for Editable Fields ---
    var holderName by remember { mutableStateOf(card.holderName) }
    var type by remember { mutableStateOf(card.type) }
    var cardCompany by remember { mutableStateOf(card.cardCompany) }
    var lastFourDigits by remember { mutableStateOf(card.cardLastFourDigits.toString()) }
    var expirationDate by remember { mutableStateOf(DateTimeUtils.toMMYY(card.expirationDate)) }
    var hexColor by remember { mutableStateOf(card.hexColor ?: "") }

    // --- Validation State ---
    val isHolderNameValid by remember { derivedStateOf { holderName.isNotBlank() } }
    val areDigitsValid by remember { derivedStateOf { lastFourDigits.length == 4 && lastFourDigits.all { it.isDigit() } } }
    val isExpiryValid by remember { derivedStateOf { expirationDate.length == 5 } } // Simple MM/YY check
    val isHexColorValid by remember { derivedStateOf { hexColor.isValidHex() } }

    val isFormValid by remember {
        derivedStateOf {
            isHolderNameValid && areDigitsValid && isExpiryValid && isHexColorValid
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // --- Title ---
                Text(
                    text = "Edit Card",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- Scrollable Input Fields ---
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false) // Takes space it needs, but not more
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = holderName,
                        onValueChange = { holderName = it },
                        label = { Text("Holder Name") },
                        isError = !isHolderNameValid,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Card Type (e.g., Debit)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cardCompany,
                        onValueChange = { cardCompany = it },
                        label = { Text("Card Company (e.g., Visa)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = lastFourDigits,
                        onValueChange = {
                            if (it.length <= 4) lastFourDigits = it.filter { c -> c.isDigit() }
                        },
                        label = { Text("Last Four Digits") },
                        isError = !areDigitsValid,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = expirationDate,
                        onValueChange = { if (it.length <= 5) expirationDate = it },
                        label = { Text("Expiration Date (MM/YY)") },
                        isError = !isExpiryValid,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = ExpiryDateVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    // --- Hex Color Input with Preview ---
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hexColor,
                            onValueChange = { hexColor = it },
                            label = { Text("Hex Color") },
                            leadingIcon = { Text("#") },
                            isError = !isHexColorValid,
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(hexColor.toColor(), CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                    }
                }

                // --- Action Buttons ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updatedCard = card.copy(
                                holderName = holderName.trim(),
                                type = type.trim(),
                                cardCompany = cardCompany.trim(),
                                cardLastFourDigits = lastFourDigits.toInt(),
                                expirationDate = DateTimeUtils.fromMMYY(expirationDate),
                                hexColor = "#${hexColor.removePrefix("#")}"
                            )
                            onEdit(updatedCard)
                        },
                        enabled = isFormValid
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// --- Helper Functions and Classes ---


// --- Preview ---

@Preview(showBackground = true)
@Composable
private fun EditCardDialogPreview() {
    MaterialTheme {
        EditCardDialog(
            card = Card(
                id = 1,
                profileOwnerId = 1,
                balance = 1234.56,
                currency = "USD",
                holderName = "Jane Doe",
                type = "Debit",
                cardCompany = "Mastercard",
                cardLastFourDigits = 1234,
                expirationDate = 1767139200000L, // Approx Dec 2025
                hexColor = "#4A90E2",
                isDefault = true,
                displayOrder = 0
            ),
            onDismiss = {},
            onEdit = {}
        )
    }
}


