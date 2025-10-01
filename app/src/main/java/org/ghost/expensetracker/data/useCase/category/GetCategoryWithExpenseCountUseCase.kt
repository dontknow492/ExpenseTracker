package org.ghost.expensetracker.data.useCase.category

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.models.CategoryWithExpenseCount
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class GetCategoryWithExpenseCountUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(
        profileOwnerId: Long,
        minDate: Long?,
        maxDate: Long?
    ): Flow<List<CategoryWithExpenseCount>> {
//        if(minDate > maxDate){
//
//        }
        return profileRepository.getCategoriesWithExpenseCount(
            profileOwnerId = profileOwnerId,
            minDate = minDate,
            maxDate = maxDate
        )
    }
}