package org.ghost.expensetracker.data.useCase.profile

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(profileOwnerId: Long, name: String): Flow<Account?> {
        return accountRepository.getAccountByProfileAndName(profileOwnerId, name)
    }
}


class UpdateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Boolean {
        return accountRepository.updateAccount(account)
    }

    suspend operator fun invoke(accounts: List<Account>) {
        accountRepository.updateAccounts(accounts)
    }
}

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Boolean {
        return accountRepository.deleteAccountById(account.id)
    }

}