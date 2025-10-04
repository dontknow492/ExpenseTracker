package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.ui.navigation.AppRoute

data class ProfileScreenActions(
    val onNavigationItemClick: (AppRoute) -> Unit,
    val onEditProfileClick: (Long) -> Unit,
    val onChangePasswordClick: (Long) -> Unit,
    val onAccountsClick: (Long) -> Unit,
    val onSettingsClick: (Long) -> Unit,
    val onAboutUsClick: (Long) -> Unit,
    val onLogoutClick: () -> Unit,
)