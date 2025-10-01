package org.ghost.expensetracker.data.useCase.category

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(profileOwnerId: Long): Flow<List<Category>> {
        return profileRepository.getAllCategoriesForProfile(profileOwnerId)
    }
}


class GetCategoryUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(categoryId: Long): Flow<Category?> {
        return profileRepository.getCategoryById(categoryId)
    }

    operator fun invoke(profileOwnerId: Long, name: String): Flow<Category?> {
        return profileRepository.getCategoryByNameAndProfile(name, profileOwnerId)
    }
}