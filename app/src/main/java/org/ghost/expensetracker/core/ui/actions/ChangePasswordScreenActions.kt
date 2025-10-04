package org.ghost.expensetracker.core.ui.actions

/**
 * Actions encapsulates all the lambda functions that the UI can trigger.
 * This keeps the content composable's signature clean.
 */
data class ChangePasswordScreenActions(
    val onOldPasswordChange: (String) -> Unit,
    val onNewPasswordChange: (String) -> Unit,
    val onConfirmPasswordChange: (String) -> Unit,
    val onChangePasswordClick: () -> Unit,
    val onMessageShown: () -> Unit, // To reset state after snackbar is shown
)