package org.ghost.expensetracker.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel // this is fine
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.ui.states.LoginUiState
import org.ghost.expensetracker.data.viewModels.auth.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (Long) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val loginUiState = viewModel.uiState.collectAsStateWithLifecycle()
    val loginUiStateValue = loginUiState.value

    LaunchedEffect(loginUiStateValue.isLoginSuccess) {
        if (loginUiStateValue.loggedInProfile != null) {
            Log.d("LoginScreen", "Login successful: ${loginUiStateValue.loggedInProfile}")
            Toast.makeText(
                context,
                context.getString(R.string.login_successful),
                Toast.LENGTH_SHORT
            ).show()
            onLoginSuccess(loginUiStateValue.loggedInProfile!!.id)
        }


    }

    LoginScreenContent(
        loginUiStateValue,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClicked = viewModel::onLoginClicked,
        onNavigateToRegister = onNavigateToRegister,
        onForgotPassword = onForgotPassword
    )
}


@Composable
private fun LoginScreenContent(
    loginUiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val isError = loginUiState.error != null
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                LoginRegisterBanner(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.login_welcome_back),
                    description = stringResource(R.string.login_banner_description)
                )
                Spacer(modifier = Modifier.height(5.dp))
                OutlinedTextField(
                    value = loginUiState.email,
                    onValueChange = onEmailChange,
                    label = { Text(stringResource(R.string.email)) },
                    placeholder = { Text(stringResource(R.string.placeholder_email)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(R.string.email)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = loginUiState.isEmailError,
                    enabled = !loginUiState.isLoading,
                )

                OutlinedTextField(
                    value = loginUiState.password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.password)) },
                    placeholder = { Text(stringResource(R.string.placeholder_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = loginUiState.isPasswordError,
                    enabled = !loginUiState.isLoading,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(R.string.password)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPasswordVisible) R.drawable.rounded_visibility_24 else R.drawable.rounded_visibility_off_24
                                ),
                                contentDescription = if (isPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                                    R.string.show_password
                                )
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                    )

                )

                if (isError) {
                    Text(
                        text = loginUiState.error
                            ?: stringResource(R.string.error_something_went_wrong),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = onLoginClicked,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loginUiState.isLoading // Disable button when loading
                ) {
                    Text(
                        stringResource(R.string.login),
                        style = MaterialTheme.typography.titleMedium
                    )

                }

                RegisterAndForgetColumn(
                    onNavigateToRegister = onNavigateToRegister,
                    onForgotPassword = onForgotPassword,
                )

                Spacer(modifier = Modifier.weight(1f))

            }
        }
    }

}


@Composable
private fun RegisterAndForgetColumn(
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.do_not_have_account))
            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.register), textDecoration = TextDecoration.Underline)
            }
        }
        TextButton(onClick = onForgotPassword) {
            Text(
                stringResource(R.string.forgot_password),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
fun LoginRegisterBanner(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.login_banner),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

