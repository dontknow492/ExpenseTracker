package org.ghost.expensetracker.ui.screens.addScreen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.enums.CardType
import org.ghost.expensetracker.core.utils.ExpiryDateVisualTransformation
import org.ghost.expensetracker.data.viewModels.addScreen.AddCardViewModel
import org.ghost.expensetracker.ui.components.ColorPickerDialog
import org.ghost.expensetracker.ui.components.ErrorSnackBar
import org.ghost.expensetracker.ui.theme.Seed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    modifier: Modifier = Modifier,
    viewModel: AddCardViewModel = hiltViewModel(),
    onNavigateBackClick: () -> Unit,
    onCardSaved: () -> Unit,
) {
    val cardState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val datePickerState = rememberDatePickerState()
    var isDatePickerVisible by remember { mutableStateOf(false) }
    var isColorPickerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(cardState.isCardSaved, cardState.error) {
        if (cardState.isCardSaved) {
            Toast.makeText(context, "Card Saved", Toast.LENGTH_SHORT).show()
            onCardSaved()
        }
        cardState.error?.let {
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
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBackClick,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                }
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::saveCard,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AddCardTopBar()
            OutlinedTextField(
                value = cardState.holderName,
                onValueChange = viewModel::onHolderNameChange,
                label = { Text(stringResource(R.string.card_holder_name_label)) },
                placeholder = { Text(stringResource(R.string.card_name_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                isError = !cardState.isHolderNameValid,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_abc_24),
                        contentDescription = stringResource(R.string.card_holder_name_label),
                        modifier = Modifier.size(34.dp)
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = cardState.cardLastFourDigits,
                    onValueChange = viewModel::onLastFourDigitsChange,
                    label = { Text(stringResource(R.string.card_lfd_label), maxLines = 1) },
                    placeholder = { Text(stringResource(R.string.card_lfd_placeholder)) },
                    modifier = Modifier.weight(1f),
                    isError = !cardState.isCardLastFourDigitsValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_123_24),
                            contentDescription = stringResource(R.string.card_lfd_label),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                )
                OutlinedTextField(
                    value = cardState.expirationDate.toString(),
                    onValueChange = viewModel::onExpirationDateChange,
                    label = { Text(stringResource(R.string.expiration_date), maxLines = 1) },
                    placeholder = { Text(stringResource(R.string.mm_yy)) },
                    modifier = Modifier.weight(1f),
                    isError = !cardState.isExpirationDateValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = ExpiryDateVisualTransformation(),
                    leadingIcon = {
                        IconButton(
                            onClick = { isDatePickerVisible = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.expiration_date)
                            )
                        }
                    },
                    singleLine = true,

                    )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = cardState.cardCompany,
                    onValueChange = viewModel::onCardCompanyChange,
                    label = { Text(stringResource(R.string.card_company), maxLines = 1) },
                    placeholder = { Text("Visa") },
                    modifier = Modifier.weight(1f),
                    isError = !cardState.isCardCompanyValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.freeFocus()
                            viewModel.saveCard()
                        }
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.card),
                            contentDescription = stringResource(R.string.card_company)
                        )
                    }
                )

                DropDownBox(
                    modifier = Modifier
                        .weight(1f),
                    value = cardState.cardType.uppercase(),
                    items = CardType.entries.map { it.type },
                    onItemSelected = viewModel::onCardTypeChange
                )
                OutlinedCard(
                    modifier = Modifier
                        .clickable { isColorPickerVisible = true }
                        .weight(0.3f)
                        .height(48.dp)
                        .align(Alignment.CenterVertically),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = cardState.color ?: Seed
                    )
                ) {}
            }

            HorizontalDivider()

        }
    }

    if (isDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { isDatePickerVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onExpirationDateChangeCalender(datePickerState.selectedDateMillis)
                        isDatePickerVisible = false
                    }
                ) {
                    Text("Confirm")
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }

    if (isColorPickerVisible) {
        ColorPickerDialog(
            color = cardState.color ?: Seed,
            onDismissRequest = { isColorPickerVisible = false },
            onColorSelected = viewModel::onColorChange
        )
    }

}

@Composable
private fun AddCardTopBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_name),
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Text(
            text = stringResource(R.string.add_card_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.add_card_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun DropDownBox(
    modifier: Modifier = Modifier,
    value: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = ""
    )
    Row(modifier = modifier) {
        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .clickable { expanded = !expanded },
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.rotate(angle)
                )
            }

        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.weight(1f)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }

        }
    }
}





