package org.ghost.expensetracker.data.useCase.profile

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.core.exceptions.InvalidEmailFormatException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.exceptions.InvalidPasswordFormatException
import org.ghost.expensetracker.core.utils.getResourceEntryName
import org.ghost.expensetracker.core.utils.isEmailValid
import org.ghost.expensetracker.data.default.AccountDefaults
import org.ghost.expensetracker.data.default.CategoryDefaults
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.repository.AccountRepository
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class CreateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val accountRepository: AccountRepository,
    @param: ApplicationContext private val context: Context
) {
    /**
     * The single business action of creating a new user profile. It handles
     * validation, uniqueness checks, and password hashing before insertion.
     * @return The official Kotlin Result wrapper containing the new profile's ID on success.
     */
    suspend operator fun invoke(
        profile: Profile,
        email: String,
        plainTextPassword: String,
        confirmPassword: String
    ): Result<Long> = runCatching {
        // --- 1. Validate Inputs ---
        require(!(profile.firstName.isBlank() || profile.lastName.isBlank())) {
            throw InvalidNameException("First-${profile.firstName} and last-${profile.lastName} names cannot be empty.")
        }

        if (!isEmailValid(email)) {
            throw InvalidEmailFormatException("Invalid email format.")
        }
        require(plainTextPassword.length >= 8 && plainTextPassword.length <= 16) {
            throw InvalidPasswordFormatException("Password must be at 8 - 16 characters long.")
        }
        require(plainTextPassword == confirmPassword) {
            throw InvalidPasswordFormatException("Password, confirm password do not match.")
        }

        // --- 2. Check for Uniqueness ---
        val existingProfile = profileRepository.checkProfileExists(email, plainTextPassword)

        Log.d(
            "CreateProfileUseCase",
            "email: $email, password: $plainTextPassword, existingProfile: $existingProfile"
        )

        require(!existingProfile) {
            throw IllegalStateException("A profile with this email already exists ${existingProfile}.")
        }


        // --- 4. Create the Final Entity and Insert ---
        val profileOwnerId = profileRepository.createProfile(profile, email, plainTextPassword)
        val defaultAccount = AccountDefaults.defaultAccount
        accountRepository.createAccount(
            Account(
                id = 0,
                profileOwnerId = profileOwnerId,
                name = context.getString(defaultAccount.nameResId),
                description = context.getString(defaultAccount.descriptionResId),
                balance = defaultAccount.balance,
                currency = defaultAccount.currency,
                isDefault = defaultAccount.isDefault,
                displayOrder = 0,
            )
        )

        val defaultCategory = CategoryDefaults.defaultCategories

        defaultCategory.forEach { defaultCategory ->
            profileRepository.createCategory(
                Category(
                    id = 0,
                    profileOwnerId = profileOwnerId,
                    name = defaultCategory.name,
                    colorHex = CategoryDefaults.categoryColors.random().toString(),
                    iconName = getResourceEntryName(defaultCategory.iconId, context),
                    displayOrder = defaultCategory.order
                )
            )
        }


        return Result.success(profileOwnerId)

    }
}


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
        val existing = profileRepository.getProfileByEmailAndName(
            profile.email,
            profile.firstName,
            profile.lastName
        ).firstOrNull()
        if (existing != null) {
            throw IllegalStateException("A profile with this email already exists ${existing}.")
        }
        profileRepository.updateProfile(profile)
    }
}