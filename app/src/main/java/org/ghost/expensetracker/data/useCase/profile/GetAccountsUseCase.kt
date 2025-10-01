package org.ghost.expensetracker.data.useCase.profile

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(profileOwnerId: Long): Flow<List<Account>> {
        return accountRepository.getAccountsForProfile(profileOwnerId)
    }

}


