package org.ghost.expensetracker.ui.screens.secondary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.ghost.expensetracker.R
import org.ghost.expensetracker.data.viewModels.secondary.EditProfileUiState
import org.ghost.expensetracker.data.viewModels.secondary.EditProfileViewModel
import org.ghost.expensetracker.ui.components.ErrorSnackBar


data class EditProfileScreenActions(
    val onFirstNameChange: (String) -> Unit,
    val onLastNameChange: (String) -> Unit,
    val onEmailChange: (String) -> Unit,
    val onAvatarUriChange: (String?) -> Unit,
    val onAvatarUrlChange: (String?) -> Unit,
    val onUpdateProfileClick: () -> Unit,
)

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isProfileSaved) {
        if (uiState.isProfileSaved) {
            Toast.makeText(
                context,
                context.getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT
            ).show()
            onNavigateBack()
        }
    }


    EditProfileScreenContent(
        modifier = modifier,
        uiState = uiState,
        actions = EditProfileScreenActions(
            onFirstNameChange = viewModel::onFirstNameChange,
            onLastNameChange = viewModel::onLastNameChange,
            onAvatarUriChange = viewModel::onAvatarUriChange,
            onUpdateProfileClick = viewModel::updateProfile,
            onAvatarUrlChange = viewModel::onAvatarUrlChange,
            onEmailChange = viewModel::onEmailChange,
        ),
        onNavigateBack = onNavigateBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(
    modifier: Modifier = Modifier,
    uiState: EditProfileUiState,
    actions: EditProfileScreenActions,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(
                it,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                ErrorSnackBar(snackbarData = snackbarData)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()), // Make the form scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Avatar Section ---
            Box(
                contentAlignment = Alignment.BottomEnd,
//                modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
            ) {
                AsyncImage(
                    model = uiState.avatarUri ?: uiState.avatarUrl,
                    contentDescription = "Profile Avatar",
                    placeholder = painterResource(id = R.drawable.person_placeholder), // Add a placeholder drawable
                    error = painterResource(id = R.drawable.person_placeholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Avatar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // --- Form Fields ---
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = actions.onFirstNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("First Name") },
                isError = uiState.isFirstNameError,
                supportingText = { if (uiState.isFirstNameError) Text("First name cannot be empty") },
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = actions.onLastNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Last Name") },
                isError = uiState.isLastNameError,
                supportingText = { if (uiState.isLastNameError) Text("Last name cannot be empty") },
                singleLine = true
            )

            // --- Save Button ---
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = actions.onUpdateProfileClick,
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
                    Text("Save Changes")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}