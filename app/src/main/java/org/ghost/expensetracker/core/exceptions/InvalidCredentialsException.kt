package org.ghost.expensetracker.core.exceptions

// Custom exception for this specific failure
class InvalidCredentialsException(message: String = "The email or password you entered is incorrect.") :
    Exception(message)