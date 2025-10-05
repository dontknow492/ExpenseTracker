package org.ghost.expensetracker.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.states.ForgetPasswordUiState
import org.ghost.expensetracker.data.viewModels.auth.ForgetPasswordViewModel

@Composable
fun ForgetPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgetPasswordViewModel = hiltViewModel(),
    onPasswordResetSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val context = LocalContext.current
    val forgetPasswordUiState = viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(forgetPasswordUiState.value.isPasswordResetSuccess) {
        Log.d(
            "ForgetPasswordScreen",
            "isPasswordResetSuccess: ${forgetPasswordUiState.value.isPasswordResetSuccess}"
        )
        if (forgetPasswordUiState.value.isPasswordResetSuccess) {
            Toast.makeText(
                context,
                context.getString(R.string.password_reset_successfully),
                Toast.LENGTH_SHORT
            ).show()
            onPasswordResetSuccess
        }


    }
    ForgetPasswordScreenContent(
        modifier,
        forgetPasswordState = forgetPasswordUiState.value,
        onLoginClicked = onNavigateToLogin,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onForgetPasswordClicked = viewModel::onForgetPasswordClicked,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange
    )
}

@Composable
fun ForgetPasswordScreenContent(
    modifier: Modifier = Modifier,
    forgetPasswordState: ForgetPasswordUiState,
    onLoginClicked: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onForgetPasswordClicked: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                LoginRegisterBanner(
                    title = stringResource(id = R.string.reset_password),
                    description = stringResource(id = R.string.reset_password_banner_description)
                )

                OutlinedTextField(
                    value = forgetPasswordState.firstName,
                    onValueChange = onFirstNameChange,
                    label = { Text(stringResource(id = R.string.label_first_name)) },
                    placeholder = { Text(stringResource(id = R.string.placeholder_first_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    enabled = !forgetPasswordState.isLoading,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = stringResource(id = R.string.first_name)
                        )
                    },
                    isError = forgetPasswordState.isFirstNameError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )

                )
                OutlinedTextField(
                    value = forgetPasswordState.lastName,
                    onValueChange = onLastNameChange,
                    label = { Text(stringResource(id = R.string.label_last_name)) },
                    placeholder = { Text(stringResource(id = R.string.placeholder_last_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !forgetPasswordState.isLoading,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = stringResource(id = R.string.last_name)
                        )
                    },
                    isError = forgetPasswordState.isLastNameError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )

                )

                OutlinedTextField(
                    value = forgetPasswordState.email,
                    onValueChange = onEmailChange,
                    label = { Text(stringResource(id = R.string.label_email)) },
                    placeholder = { Text(stringResource(id = R.string.placeholder_email)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(id = R.string.email)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = forgetPasswordState.isEmailError,
                    enabled = !forgetPasswordState.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                HorizontalDivider()

                OutlinedTextField(
                    value = forgetPasswordState.password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(id = R.string.label_password)) },
                    placeholder = { Text(stringResource(id = R.string.placeholder_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = forgetPasswordState.isPasswordError,
                    enabled = !forgetPasswordState.isLoading,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(id = R.string.password)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPasswordVisible) R.drawable.rounded_visibility_24 else R.drawable.rounded_visibility_off_24
                                ),
                                contentDescription = if (isPasswordVisible) stringResource(id = R.string.hide_password) else stringResource(
                                    id = R.string.show_password
                                )
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )

                )

                OutlinedTextField(
                    value = forgetPasswordState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text(stringResource(id = R.string.label_confirm_password)) },
                    placeholder = { Text(stringResource(id = R.string.placeholder_confirm_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = forgetPasswordState.isConfirmPasswordError,
                    enabled = !forgetPasswordState.isLoading,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(id = R.string.password)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            isConfirmPasswordVisible = !isConfirmPasswordVisible
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isConfirmPasswordVisible) R.drawable.rounded_visibility_24 else R.drawable.rounded_visibility_off_24
                                ),
                                contentDescription = if (isConfirmPasswordVisible) stringResource(id = R.string.hide_password) else stringResource(
                                    id = R.string.show_password
                                )
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onForgetPasswordClicked() }
                    )

                )


                if (forgetPasswordState.error != null) {
                    Text(
                        text = forgetPasswordState.error
                            ?: stringResource(id = R.string.error_something_went_wrong),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = onForgetPasswordClicked,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !forgetPasswordState.isLoading // Disable button when loading
                ) {
                    Text(
                        stringResource(id = R.string.reset_password),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(id = R.string.text_already_have_account))
                    TextButton(onClick = onLoginClicked) {
                        Text(
                            stringResource(id = R.string.login),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

            }

        }
    }
}
