package org.ghost.expensetracker.core.exceptions

/**
 * A custom exception thrown when an expense object is in an invalid state,
 * such as having both an accountId and a cardId, or neither.
 */
class InvalidSourceOfFundsException(message: String) : IllegalArgumentException(message)