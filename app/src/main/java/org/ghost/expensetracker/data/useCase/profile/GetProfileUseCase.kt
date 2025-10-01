package org.ghost.expensetracker.data.useCase.profile

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(id: Long): Flow<Profile?> {
        return profileRepository.getProfileById(id)
    }
}


