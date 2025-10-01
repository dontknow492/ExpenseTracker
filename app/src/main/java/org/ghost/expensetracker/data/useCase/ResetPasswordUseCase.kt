package org.ghost.expensetracker.data.useCase

import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.FailedToUpdateDB
import org.ghost.expensetracker.core.exceptions.InvalidEmailFormatException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.exceptions.InvalidPasswordFormatException
import org.ghost.expensetracker.core.utils.isEmailValid
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        profile: Profile,
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> = runCatching {
        require(!(profile.firstName.isBlank() || profile.lastName.isBlank())) {
            throw InvalidNameException("First-${profile.firstName} and last-${profile.lastName} names cannot be empty.")
        }
        if (!isEmailValid(email)) {
            throw InvalidEmailFormatException("Email is not valid.")
        }
        require(newPassword.length >= 8 && newPassword.length <= 16) {
            throw InvalidPasswordFormatException("Password must be at 8 - 16 characters long.")
        }
        require(newPassword == confirmPassword) {
            throw InvalidPasswordFormatException("Passwords do not match.")
        }


        val existingProfile = profileRepository.getProfileByEmailAndName(
            email,
            profile.firstName,
            profile.lastName
        ).firstOrNull()
        require(existingProfile != null) {
            throw IllegalStateException("A profile with this email and name does not exist.")
        }


        val success = profileRepository.updateProfile(existingProfile, email, newPassword)
        require(success) {
            throw FailedToUpdateDB("Failed to update password.")
        }
    }
}