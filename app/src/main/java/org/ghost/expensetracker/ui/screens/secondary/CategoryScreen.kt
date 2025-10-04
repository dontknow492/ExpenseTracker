package org.ghost.expensetracker.ui.screens.secondary

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.core.ui.actions.CategoryScreenActions
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.CategoryWithExpenseCount
import org.ghost.expensetracker.data.viewModels.secondary.CategoryViewModel
import org.ghost.expensetracker.ui.components.AddItemTopBar
import org.ghost.expensetracker.ui.components.CategoryWithExpenseItem
import org.ghost.expensetracker.ui.components.ConfirmDeleteDialog
import org.ghost.expensetracker.ui.components.DragHandle
import org.ghost.expensetracker.ui.components.ErrorSnackBar
import org.ghost.expensetracker.ui.screens.main.ErrorCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateBackClick: () -> Unit,
    onAddCategoryClick: (Long) -> Unit,
    onCategoryClick: (Long, ExpenseFilters) -> Unit,
) {
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val errorState by viewModel.errorState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorState) {
        if (errorState != null) {
            snackbarHostState.showSnackbar(
                message = errorState!!,
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    val actions = remember {
        CategoryScreenActions(
            onAddCategoryClick = {
                onAddCategoryClick(viewModel.profileOwnerId)
            },
            onEditCategoryClick = {},
            onDeleteCategoryClick = viewModel::deleteCategory,
            onMoveCategory = viewModel::moveCategory,
            onNavigateBackClick = onNavigateBackClick,
            onCategoryClick = { categoryWithExpenseCount ->
                val filters = ExpenseFilters(
                    categoryId = categoryWithExpenseCount.category.id,
                )
                onCategoryClick(categoryWithExpenseCount.category.profileOwnerId, filters)
            },
        )

    }

    CategoryScreenContent(
        modifier = modifier,
        categoriesState = categoriesState,
        actions = actions,
        snackbarHostState = snackbarHostState,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryScreenContent(
    modifier: Modifier = Modifier,
    categoriesState: UiState<List<CategoryWithExpenseCount>>,
    actions: CategoryScreenActions,
    snackbarHostState: SnackbarHostState,
) {
    var deletingCategory: CategoryWithExpenseCount? by remember { mutableStateOf(null) }
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.categories),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = actions.onNavigateBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                ErrorSnackBar(snackbarData = snackbarData)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AddItemTopBar(
                title = "Add Budget\nCategory",
                onAddNewClick = actions.onAddCategoryClick
            )
            when (categoriesState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Error -> {
                    ErrorCard(message = categoriesState.message)
                }

                is UiState.Success -> {
                    val categories = categoriesState.data
                    if (categories.isEmpty()) {
                        Text(
                            text = "No categories found.",
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    } else {
                        CategoryListContent(
                            categories = categories,
                            onCategoryClick = actions.onCategoryClick,
                            onMoveCategory = actions.onMoveCategory,
                            onEditCategoryClick = actions.onEditCategoryClick,
                            onDeleteCategoryClick = {
                                deletingCategory = it
                            }
                        )
                    }
                }

            }
        }
    }
    if (deletingCategory != null) {
        ConfirmDeleteDialog(
            onDismissRequest = { deletingCategory = null },
            onConfirm = {
                actions.onDeleteCategoryClick(deletingCategory!!)
                deletingCategory = null
            },
            title = stringResource(R.string.delete_category),
            text = stringResource(R.string.delete_category_message),
            icon = Icons.Outlined.Warning,
        )
    }
}

@Composable
fun CategoryListContent(
    modifier: Modifier = Modifier,
    categories: List<CategoryWithExpenseCount>,
    onCategoryClick: (CategoryWithExpenseCount) -> Unit,
    onEditCategoryClick: (CategoryWithExpenseCount) -> Unit,
    onDeleteCategoryClick: (CategoryWithExpenseCount) -> Unit,
    onMoveCategory: (from: Int, to: Int) -> Unit,
) {
    // 1. This library's state holder
    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        onMoveCategory(from.index, to.index)
    }

    // 2. Use a standard LazyColumn
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = categories, key = { it.category.id }) { item ->
            // 4. Wrap your item in the library's ReorderableItem
            ReorderableItem(reorderableLazyListState, key = item.category.id) {
                // You can wrap your item in a Surface to apply elevation
                val interactionSource = remember { MutableInteractionSource() }
                CategoryWithExpenseItem(
                    modifier = Modifier,
                    categoryWithExpenseCount = item,
                    onClick = onCategoryClick,
                    onEditClick = onEditCategoryClick,
                    onDeleteClick = onDeleteCategoryClick,
                    dragHandle = {
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