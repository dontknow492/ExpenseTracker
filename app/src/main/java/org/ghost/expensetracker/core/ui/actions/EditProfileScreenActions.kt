package org.ghost.expensetracker.core.ui.actions

import android.net.Uri

data class EditProfileScreenActions(
    val onFirstNameChange: (String) -> Unit,
    val onLastNameChange: (String) -> Unit,
    val onEmailChange: (String) -> Unit,
    val onAvatarUriChange: (Uri?) -> Unit,
    val onAvatarUrlChange: (String?) -> Unit,
    val onUpdateProfileClick: () -> Unit,
)