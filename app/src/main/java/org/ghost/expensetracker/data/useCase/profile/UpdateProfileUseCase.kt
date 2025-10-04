package org.ghost.expensetracker.data.useCase.profile

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.repository.AccountRepository
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val accountRepository: AccountRepository,
    @param: ApplicationContext private val context: Context
) {
    suspend operator fun invoke(
        profile: Profile,
        email: String,
        oldPassword: String,
        plainTextPassword: String
    ): Boolean {
        val existing = profileRepository.checkProfileExists(email, oldPassword)
        require(existing) {
            throw InvalidCredentialsException("Invalid email or password.")
        }
        return profileRepository.updateProfile(profile, email, plainTextPassword)
    }

    suspend operator fun invoke(profile: Profile) {
        val existing = profileRepository.getProfileById(profile.id).firstOrNull()
        if (existing == null) {
            throw IllegalStateException("A profile with this email does not exists: ${profile.email}.")
        }
        profileRepository.updateProfile(profile)
    }
}