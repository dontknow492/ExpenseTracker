package org.ghost.expensetracker.core.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.toHexCode(): String {
    return String.format("#%08X", this.toArgb())
}

