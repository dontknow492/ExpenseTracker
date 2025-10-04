package org.ghost.expensetracker.ui.screens.secondary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.actions.ChangePasswordScreenActions
import org.ghost.expensetracker.core.ui.states.ChangePasswordUiState
import org.ghost.expensetracker.data.viewModels.secondary.ChangePasswordViewModel
import org.ghost.expensetracker.ui.components.ErrorSnackBar

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPasswordChanged: () -> Unit // Callback for successful password change (e.g., navigate to profile)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // This effect listens for changes in the uiState to show messages or navigate.
    LaunchedEffect(uiState.isPasswordChanged, uiState.errorMessage) {
        if (uiState.isPasswordChanged) {
            snackbarHostState.showSnackbar("Password changed successfully!")
            onPasswordChanged() // Navigate or perform other actions
            viewModel.onMessageShown() // Reset the state flag
        }
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown() // Reset the error message
        }
    }

    ChangePasswordScreenContent(
        modifier = modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        actions = ChangePasswordScreenActions(
            onOldPasswordChange = viewModel::onOldPasswordChange,
            onNewPasswordChange = viewModel::onNewPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onChangePasswordClick = viewModel::changePassword,
            onMessageShown = viewModel::onMessageShown
        ),
        onNavigateBack = onNavigateBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreenContent(
    modifier: Modifier = Modifier,
    uiState: ChangePasswordUiState,
    snackbarHostState: SnackbarHostState,
    actions: ChangePasswordScreenActions,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                ErrorSnackBar(
                    snackbarData = snackbarData,
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // Make the form scrollable
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Old Password
            var oldPasswordVisibility by rememberSaveable { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.oldPassword,
                onValueChange = actions.onOldPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Old Password") },
                isError = uiState.isOldPasswordError,
                supportingText = { if (uiState.isOldPasswordError) Text("Old password is required") },
                visualTransformation = if (oldPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (oldPasswordVisibility)
                        painterResource(R.drawable.rounded_visibility_24)
                    else painterResource(R.drawable.rounded_visibility_off_24)
                    IconButton(onClick = { oldPasswordVisibility = !oldPasswordVisibility }) {
                        Icon(painter = image, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true
            )

            // New Password
            var newPasswordVisibility by rememberSaveable { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.newPassword,
                onValueChange = actions.onNewPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("New Password") },
                isError = uiState.isNewPasswordError,
                supportingText = {
                    if (uiState.isNewPasswordError) Text(
                        uiState.errorMessage ?: "New password must be at least 6 characters"
                    )
                },
                visualTransformation = if (newPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (newPasswordVisibility)
                        painterResource(R.drawable.rounded_visibility_24)
                    else painterResource(R.drawable.rounded_visibility_off_24)
                    IconButton(onClick = { newPasswordVisibility = !newPasswordVisibility }) {
                        Icon(painter = image, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true
            )

            // Confirm New Password
            var confirmPasswordVisibility by rememberSaveable { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = actions.onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Confirm New Password") },
                isError = uiState.isConfirmPasswordError,
                supportingText = { if (uiState.isConfirmPasswordError) Text("Passwords do not match") },
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (confirmPasswordVisibility)
                        painterResource(R.drawable.rounded_visibility_24)
                    else painterResource(R.drawable.rounded_visibility_off_24)
                    IconButton(onClick = {
                        confirmPasswordVisibility = !confirmPasswordVisibility
                    }) {
                        Icon(painter = image, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Change Password Button
            Button(
                onClick = actions.onChangePasswordClick,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Change Password")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Previews ---
@Preview(showBackground = true)
@Composable
private fun ChangePasswordScreenPreview() {
    ChangePasswordScreenContent(
        uiState = ChangePasswordUiState(
            email = "test@example.com",
            isLoading = false,
            errorMessage = null
        ),
        snackbarHostState = remember { SnackbarHostState() },
        actions = ChangePasswordScreenActions({}, {}, {}, {}, {}),
        onNavigateBack = {}
    )

}

@Preview(showBackground = true)
@Composable
private fun ChangePasswordScreenErrorPreview() {
    ChangePasswordScreenContent(
        uiState = ChangePasswordUiState(
            email = "test@example.com",
            oldPassword = "wrong",
            newPassword = "new",
            confirmPassword = "mismatch",
            isOldPasswordError = true,
            isNewPasswordError = false,
            isConfirmPasswordError = true,
            errorMessage = "Passwords do not match.",
            isLoading = false
        ),
        snackbarHostState = remember { SnackbarHostState() },
        actions = ChangePasswordScreenActions({}, {}, {}, {}, {}),
        onNavigateBack = {}
    )

}