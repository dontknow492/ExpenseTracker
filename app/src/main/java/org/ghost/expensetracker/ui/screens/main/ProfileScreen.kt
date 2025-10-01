package org.ghost.expensetracker.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.ghost.expensetracker.R
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.viewModels.main.ProfileViewModel
import org.ghost.expensetracker.ui.navigation.AppRoute
import org.ghost.expensetracker.ui.navigation.ExpenseTrackerNavigationBar
import org.ghost.expensetracker.ui.navigation.MainRoute

data class ProfileScreenActions(
    val onNavigationItemClick: (AppRoute) -> Unit,
    val onEditProfileClick: (Long) -> Unit,
    val onChangePasswordClick: (Long) -> Unit,
    val onAccountsClick: (Long) -> Unit,
    val onSettingsClick: (Long) -> Unit,
    val onAboutUsClick: (Long) -> Unit,
    val onLogoutClick: () -> Unit,
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    actions: ProfileScreenActions,
) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()

    if (profile != null) {
        ProfileScreenContent(
            modifier = modifier,
            profileId = viewModel.profileOwnerId,
            actions = actions.copy(
                onLogoutClick = {
                    viewModel.onLogout()
                    actions.onLogoutClick()
                }
            ),
            profile = profile!!
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


@Preview
@Composable
fun ProfileScreenContentPreview() {
    val profile = Profile(
        id = 1L,
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        avatarUri = null,
        avatarUrl = null
    )
    ProfileScreenContent(
        profileId = 1L,
        actions = ProfileScreenActions({}, {}, {}, {}, {}, {}, {}),
        profile = profile
    )
}

@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    profileId: Long,
    actions: ProfileScreenActions,
    profile: Profile,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar {
                ExpenseTrackerNavigationBar(
                    selectedItem = MainRoute.Profile(profileId),
                    onNavigationItemClick = actions.onNavigationItemClick,
                    profileOwnerId = profileId,
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = profile.avatarUri ?: profile.avatarUrl,
                    contentDescription = "Profile Avatar",
                    placeholder = painterResource(id = R.drawable.person_placeholder), // Add a placeholder drawable
                    error = painterResource(id = R.drawable.person_placeholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${profile.firstName} ${profile.lastName}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            HorizontalDivider()
            SettingButton(
                onClick = { actions.onEditProfileClick(profileId) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = "Edit Profile",
            )
            SettingButton(
                onClick = { actions.onChangePasswordClick(profileId) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Change Password",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = "Change Password",
            )
            SettingButton(
                onClick = { actions.onAccountsClick(profileId) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Accounts",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = "Accounts",
            )
            SettingButton(
                onClick = { actions.onSettingsClick(profileId) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = "Settings",
            )
            SettingButton(
                onClick = { actions.onAboutUsClick(profileId) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About us",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = "About us",
            )
            HorizontalDivider()
            Button(
                onClick = { actions.onLogoutClick() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Logout")
            }
        }
    }
}


@Composable
fun SettingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: String,
    description: String? = null,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon()
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}