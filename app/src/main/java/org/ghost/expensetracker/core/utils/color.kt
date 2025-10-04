package org.ghost.expensetracker.core.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color as AndroidColor
import androidx.core.graphics.toColorInt

fun Color.toHexCode(): String {
    return String.format("#%08X", this.toArgb())
}

