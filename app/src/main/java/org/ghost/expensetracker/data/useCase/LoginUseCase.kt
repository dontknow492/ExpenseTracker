package org.ghost.expensetracker.data.useCase

import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.core.exceptions.InvalidEmailFormatException
import org.ghost.expensetracker.core.exceptions.InvalidPasswordFormatException
import org.ghost.expensetracker.core.utils.sha256
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    /**
     * The single business action of authenticating a user. It handles
     * credential validation and returns the user's profile on success.
     * @return The official Kotlin Result wrapper containing the Profile on success.
     */
    suspend operator fun invoke(email: String, plainTextPassword: String): Result<Profile> =
        runCatching {
            // --- 1. Validate Inputs ---
            require(email.isNotBlank()) {
                throw InvalidEmailFormatException("Email cannot be blank")
            }
            require(email.isNotBlank() && plainTextPassword.isNotBlank()) {
                throw InvalidPasswordFormatException("Password cannot be blank")
            }

            // --- 2. Secure the Password ---
            val passwordHash = plainTextPassword.sha256() // Or your chosen hashing algorithm

            // --- 3. Find the Profile ---
            val profile = profileRepository
                .getProfileByEmailAndPasswordHash(email, passwordHash)
                .firstOrNull()

            // --- 4. Return the profile or throw an exception ---
            // If the profile is null, it means no user was found with that email/password combo.
            // The 'runCatching' block will automatically turn this exception into a Result.failure.
            profile ?: throw InvalidCredentialsException()
        }
}