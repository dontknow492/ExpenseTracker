package org.ghost.expensetracker.data.useCase.category

import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(category: Category) {
        profileRepository.updateCategory(category)
    }
}

