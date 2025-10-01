package org.ghost.expensetracker.core.utils

import androidx.compose.ui.graphics.Color
import java.security.MessageDigest
import java.util.regex.Pattern

/**
 * Computes the SHA-256 hash of the string.
 *
 * This extension function takes a string, converts it to a byte array,
 * and then computes its SHA-256 hash. The resulting hash is then
 * converted to a hexadecimal string representation.
 *
 * @return The SHA-256 hash of the string as a hexadecimal string.
 */
fun String.sha256(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashedPassword = messageDigest
        .digest(this.toByteArray())
        .joinToString("") {
            "%02x".format(it)
        }
    return hashedPassword
}

fun isEmailValid(email: String): Boolean {
    return Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    ).matcher(email).matches()
}

fun String.isValidHex(): Boolean = this.matches(Regex("^#?([0-9a-fA-F]{6})$"))

fun String.toColor(): Color {
    val cleanHex = this.removePrefix("#")
    return if (cleanHex.length == 6) {
        try {
            Color(android.graphics.Color.parseColor("#$cleanHex"))
        } catch (e: Exception) {
            Color.Gray
        }
    } else {
        Color.Gray
    }
}