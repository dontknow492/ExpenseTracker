package org.ghost.expensetracker.data.useCase.profile

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.core.enums.CardSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class FilterCardsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(
        profileOwnerId: Long,
        name: String? = null,
        type: String? = null,
        sortBy: CardSortBy = CardSortBy.ADDED_AT,
        sortOrder: SortOrder = SortOrder.ASCENDING
    ): Flow<List<Card>> {
        return accountRepository.filterCards(
            profileOwnerId = profileOwnerId,
            name = name,
            type = type,
            sortBy = sortBy,
            sortOrder = sortOrder
        )
    }
}

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

class DeleteCardUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(card: Card): Boolean {
        return accountRepository.deleteCardById(card.id)
    }

}

