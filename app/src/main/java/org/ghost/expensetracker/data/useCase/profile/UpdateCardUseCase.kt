package org.ghost.expensetracker.data.useCase.profile

import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class UpdateCardUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(card: Card) {
        accountRepository.updateCard(card)
    }

    suspend operator fun invoke(cards: List<Card>) {
        accountRepository.updateCards(cards)
    }
}