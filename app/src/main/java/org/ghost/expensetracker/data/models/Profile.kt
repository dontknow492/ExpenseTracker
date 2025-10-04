package org.ghost.expensetracker.data.models

data class Profile(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val avatarFilePath: String?,
    val avatarUrl: String?,
)