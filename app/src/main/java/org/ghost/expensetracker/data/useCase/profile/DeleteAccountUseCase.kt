package org.ghost.expensetracker.data.useCase.profile

import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Boolean {
        return accountRepository.deleteAccountById(account.id)
    }

}