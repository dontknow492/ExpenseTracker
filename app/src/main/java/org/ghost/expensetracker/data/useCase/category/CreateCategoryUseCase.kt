package org.ghost.expensetracker.data.useCase.category

import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class CreateCategoryUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(category: Category): Result<Long> = runCatching {
        if (category.name.isBlank() || category.name.length > 24) {
            throw InvalidNameException("Category name must be between 1 and 24 characters.")
        }
        val existingCategory =
            profileRepository.getCategoryByNameAndProfile(category.name, category.profileOwnerId)
                .firstOrNull()
        if (existingCategory != null) {
            throw InvalidCredentialsException("Category with this name already exists for user.")
        }

        val displayOrder = profileRepository.getCategoryCountForProfile(category.profileOwnerId)

        profileRepository.createCategory(category.copy(displayOrder = displayOrder))
    }
}