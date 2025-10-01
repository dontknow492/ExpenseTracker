package org.ghost.expensetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

/**
 * A reusable Material 3 dialog for confirming a destructive action, such as deletion.
 *
 * @param modifier The modifier to be applied to the dialog.
 * @param onDismissRequest Called when the user tries to dismiss the dialog by clicking outside
 * or pressing the back button.
 * @param onConfirm Called when the user clicks the confirm button. The caller is responsible
 * for hiding the dialog and performing the delete action.
 * @param title The title of the dialog, summarizing the action.
 * @param text The main text of the dialog, providing more details about the consequences.
 * @param icon The icon to be displayed at the top of the dialog. Defaults to a warning icon.
 */
@Composable
fun ConfirmDeleteDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Confirm Deletion",
    text: String = "Are you sure you want to delete this? This action cannot be undone.",
    icon: ImageVector = Icons.Outlined.Warning
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = "Warning Icon",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = "Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.error,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
private fun ConfirmDeleteDialogPreview() {
    // Wrap in your app's theme or a default MaterialTheme for accurate preview
    MaterialTheme {
        Surface {
            ConfirmDeleteDialog(
                onDismissRequest = { /* Preview action */ },
                onConfirm = { /* Preview action */ },
                title = "Delete Note?",
                text = "The note 'Meeting Highlights' will be permanently removed."
            )
        }
    }
}