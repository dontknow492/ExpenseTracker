package org.ghost.expensetracker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ghost.expensetracker.core.enums.CardSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.dao.AccountDao
import org.ghost.expensetracker.data.database.dao.CardDao
import org.ghost.expensetracker.data.database.entity.AccountEntity
import org.ghost.expensetracker.data.database.entity.CardEntity
import org.ghost.expensetracker.data.mappers.toDomainModel
import org.ghost.expensetracker.data.mappers.toEntity
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val cardDao: CardDao
) {

    suspend fun createAccount(account: Account): Long {
        return accountDao.insert(account.toEntity())
    }

    suspend fun updateAccount(account: Account): Boolean {
        return accountDao.update(account.toEntity()) > 0
    }

    suspend fun updateAccounts(accounts: List<Account>) {
        accountDao.updateAccounts(accounts.map { it.toEntity() })
    }

    suspend fun deleteAccountById(accountId: Long): Boolean {
        return accountDao.deleteAccountById(accountId) > 0
    }

    fun getAccountById(id: Long): Flow<Account?> {
        // 1. Call the DAO to get the raw Flow of the database entity.
        val accountEntityFlow: Flow<AccountEntity?> = accountDao.getAccountById(id)

        // 2. Use the .map operator to transform it.
        // For each AccountEntity that comes out of the flow, the mapper
        // function is applied, and the result is a new Flow of the UI model.
        return accountEntityFlow.map { accountEntity ->
            accountEntity?.toDomainModel()
        }
    }

    fun getAccountByProfileAndName(profileId: Long, name: String): Flow<Account?> {
        return accountDao.getAccountByProfileAndName(profileId, name).map { accountEntity ->
            accountEntity?.toDomainModel()
        }
    }

    suspend fun getAccountsCount(): Int {
        return accountDao.getAccountsCount()
    }

    fun getAccountsForProfile(profileOwnerId: Long): Flow<List<Account>> {
        return accountDao
            .getAllAccountsForProfile(profileOwnerId)
            .map { accountEntities ->
                accountEntities.map { accountEntity ->
                    accountEntity.toDomainModel()
                }
            }
    }

    suspend fun addCard(card: Card): Long {
        return cardDao.insert(card.toEntity())
    }

    suspend fun updateCard(card: Card) {
        cardDao.update(card.toEntity())
    }

    suspend fun updateCards(cards: List<Card>) {
        cardDao.updateCards(cards.map { it.toEntity() })
    }

    suspend fun deleteCardById(cardId: Long): Boolean {
        return cardDao.deleteCardById(cardId) > 0
    }

    fun getCardById(id: Long): Flow<Card?> {
        // 1. Call the DAO to get the raw Flow of the database entity.
        val cardEntityFlow: Flow<CardEntity?> = cardDao.getCardById(id)
        return cardEntityFlow.map { cardEntity ->
            cardEntity?.toDomainModel()
        }
    }

    fun getCardByProfileAndCompanyAndLastFourDigits(
        profileId: Long,
        company: String,
        lastFourDigits: Int
    ): Flow<Card?> {
        return cardDao.getCardByProfileAndCompanyAndLastFourDigits(
            profileId,
            company,
            lastFourDigits
        ).map { cardEntity ->
            cardEntity?.toDomainModel()
        }
    }

    suspend fun getCardsCount(): Int {
        return cardDao.getCardsCount()
    }

    fun filterCards(
        profileOwnerId: Long,
        name: String? = null,
        type: String? = null,
        sortBy: CardSortBy = CardSortBy.ADDED_AT,
        sortOrder: SortOrder = SortOrder.ASCENDING
    ): Flow<List<Card>> {
        return cardDao
            .filterCards(
                profileOwnerId = profileOwnerId,
                name = name,
                type = type,
                sortBy = sortBy,
                sortOrder = sortOrder
            )
            .map { cardEntities ->
                cardEntities.map { cardEntity ->
                    cardEntity.toDomainModel()
                }
            }

    }
}