package org.ghost.expensetracker.core.exceptions

/**
 * Thrown when the password does not meet the required format (e.g., length).
 */
class InvalidPasswordFormatException(message: String) : IllegalArgumentException(message)