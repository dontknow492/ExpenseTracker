package org.ghost.expensetracker.data.useCase.category

import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class UpdateCategoriesUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(categories: List<Category>) {
        profileRepository.updateCategories(categories)
    }
}