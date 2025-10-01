package org.ghost.expensetracker.data.mappers

import org.ghost.expensetracker.data.database.entity.ProfileEntity
import org.ghost.expensetracker.data.models.Profile

fun ProfileEntity.toDomainModel(): Profile {
    return Profile(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatarUri = avatarUri,
        avatarUrl = avatarUrl,
        email = email
    )
}

fun Profile.toEntity(
    email: String,
    passwordHash: String,
): ProfileEntity {
    return ProfileEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatarUri = avatarUri,
        avatarUrl = avatarUrl,
        email = email,
        passwordHash = passwordHash,
        creationTimestamp = System.currentTimeMillis(),
    )
}