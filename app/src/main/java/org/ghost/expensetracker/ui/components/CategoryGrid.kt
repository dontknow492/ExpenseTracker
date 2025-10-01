package org.ghost.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.ui.screens.main.ErrorCard

@Composable
fun CategoriesContent(
    modifier: Modifier = Modifier,
    selectedCategoryId: Long?,
    allowAddCategory: Boolean,
    categoriesState: UiState<List<Category>>,
    isCategoryError: Boolean = false,
    onAddNewClick: () -> Unit,
    onCategoryClick: (Category) -> Unit // Added for interactivity
) {
    when (categoriesState) {
        is UiState.Loading -> {
            // Center the indicator
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            ErrorCard(message = categoriesState.message)
        }

        is UiState.Success -> {
            val categories = categoriesState.data
            Column(
                modifier = modifier,
                // Add spacing between the title and the grid++
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.categories),
                    style = MaterialTheme.typography.titleLarge,
                )
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(3),
                    modifier = Modifier.height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (allowAddCategory) {
                        item {
                            OutlinedCard(
                                modifier = Modifier.clickable { onAddNewClick() }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.add_new_category),
                                    )
                                    Text(
                                        stringResource(R.string.add_new)
                                    )
                                }
                            }
                        }
                    }

                    items(items = categories, key = { it.id }) { category ->
                        SimpleCategoryItem(
                            category = category,
                            onClick = { onCategoryClick(category) },
                            isSelected = category.id == (selectedCategoryId ?: -999L),
                            isError = isCategoryError
                        )
                    }
                }
            }
        }
    }
}