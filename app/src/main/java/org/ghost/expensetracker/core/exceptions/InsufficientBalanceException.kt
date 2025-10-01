package org.ghost.expensetracker.core.exceptions

import java.io.IOException

/**
 * A custom exception thrown when a user tries to perform a transaction
 * but does not have enough balance in the source account or card.
 * Inherits from IOException as it often relates to an invalid state of data.
 */
class InsufficientBalanceException(message: String = "The account or card has insufficient balance.") :
    IOException(message)

