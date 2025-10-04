package org.ghost.expensetracker.data.useCase.profile

import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(card: Card): Boolean {
        return accountRepository.deleteCardById(card.id)
    }

}