package org.ghost.expensetracker.data.useCase.profile

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class GetCardUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(profileOwnerId: Long, company: String, lastFourDigits: Int): Flow<Card?> {
        return accountRepository.getCardByProfileAndCompanyAndLastFourDigits(
            profileOwnerId,
            company,
            lastFourDigits
        )
    }

}