package org.ghost.expensetracker.data.models

import androidx.compose.ui.graphics.painter.Painter

// A data class to hold social media link information
data class SocialLink(
    val icon: Painter,
    val url: String,
    val contentDescription: String
)