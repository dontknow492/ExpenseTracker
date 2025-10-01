package org.ghost.expensetracker.core.exceptions

/**
 * Thrown when the user's first or last name is blank.
 */
class InvalidNameException(message: String) : IllegalArgumentException(message)