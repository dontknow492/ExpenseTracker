package org.ghost.expensetracker.data.useCase.category

import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(categoryId: Long): Boolean {
        return profileRepository.deleteCategoryById(categoryId)
    }

}