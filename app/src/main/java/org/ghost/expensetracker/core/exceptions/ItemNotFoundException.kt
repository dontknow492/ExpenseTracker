package org.ghost.expensetracker.core.exceptions

/**
 * A generic custom exception for when a specific item (like an Account, Card, or Due)
 * cannot be found in the database.
 */
class ItemNotFoundException(message: String) : Exception(message)