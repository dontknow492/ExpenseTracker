package org.ghost.expensetracker.ui.screens.addScreen

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.states.AddUpdateCategoryUiState
import org.ghost.expensetracker.data.default.CategoryDefaults
import org.ghost.expensetracker.data.viewModels.addScreen.AddCategoryViewModel


@Composable
fun AddCategoryDialog(
    modifier: Modifier = Modifier,
    viewModel: AddCategoryViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context: Context = LocalContext.current


    LaunchedEffect(uiState.isCategorySaved) {
        if (uiState.isCategorySaved) {
            Toast.makeText(
                context,
                context.getString(R.string.category_created_successfully), Toast.LENGTH_SHORT
            ).show()
            onDismissRequest()
        }
    }

    AddUpdateCategoryContent(
        modifier = modifier,
        uiState = uiState,
        onDismissRequest = onDismissRequest,
        onNameChanged = viewModel::onNameChange,
        onColorSelected = viewModel::onColorChange,
        onIconSelected = viewModel::onIconIdChange,
        onSaveCategory = viewModel::saveCategory,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUpdateCategoryContent(
    modifier: Modifier = Modifier,
    uiState: AddUpdateCategoryUiState,
    onNameChanged: (String) -> Unit,
    onColorSelected: (Color) -> Unit,
    onIconSelected: (Int) -> Unit,
    onSaveCategory: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Title ---
                Text(
                    text = stringResource(R.string.add_new_category),
                    style = MaterialTheme.typography.titleLarge
                )

                // --- Name Text Field ---
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.category_name)) },
                    singleLine = true,
                    isError = uiState.isNameError,
                    supportingText = {
                        if (uiState.isNameError) {
                            Text(stringResource(R.string.name_desclamier))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSaveCategory() }
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_abc_24),
                            contentDescription = stringResource(R.string.category_name),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                )

                // --- Color Picker ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Color", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = !uiState.isLoading
                    ) {
                        items(items = CategoryDefaults.categoryColors) { color ->
                            ColorItem(
                                color = color,
                                isSelected = uiState.color == color,
                                onClick = { onColorSelected(color) }
                            )
                        }
                    }
                }

                // --- Icon Picker ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Icon", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(48.dp),
                        modifier = Modifier.heightIn(max = 180.dp),
                        userScrollEnabled = !uiState.isLoading
                    ) {
                        items(items = CategoryDefaults.categoryIcons, key = { it }) { iconId ->
                            IconItem(
                                iconId = iconId,
                                isSelected = uiState.iconId == iconId,
                                onClick = { onIconSelected(iconId) }
                            )
                        }
                    }
                }

                // --- Loading Indicator or Action Buttons ---
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = onSaveCategory,
                            // Disable button if name is empty
                            enabled = uiState.name.isNotBlank()
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                } else Modifier
            )
    )
}

@Composable
private fun IconItem(
    @DrawableRes iconId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = null, // Decorative
            tint = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}