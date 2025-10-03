package org.ghost.expensetracker.core.mock


import org.ghost.expensetracker.data.database.entity.AccountEntity
import org.ghost.expensetracker.data.database.entity.CardEntity
import org.ghost.expensetracker.data.database.entity.CategoryEntity
import org.ghost.expensetracker.data.database.entity.ExpenseEntity
import org.ghost.expensetracker.data.database.entity.ProfileEntity
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random


/**
 * Creates a list of mock ProfileEntity objects.
 *
 * @param count The number of profiles to generate.
 * @return A list of mock ProfileEntity objects.
 */
fun createMockProfiles(count: Int): List<ProfileEntity> {
    val mockProfiles = mutableListOf<ProfileEntity>()
    val namePairs = listOf(
        Pair("Alice", "Johnson"),
        Pair("Bob", "Smith"),
        Pair("Charlie", "Williams"),
        Pair("Diana", "Brown"),
        Pair("Ethan", "Jones")
    )

    for (i in 0 until count) {
        val (firstName, lastName) = namePairs[i % namePairs.size]
        val randomNumber = Random.nextInt(100, 999)

        val profile = ProfileEntity(
            // id is 0 so Room can auto-generate it
            firstName = firstName,
            lastName = "$lastName$i", // Add index to ensure uniqueness
            avatarUri = null,
            avatarUrl = null,
            email = "${firstName.lowercase()}.${lastName.uppercase()}$i@example.com",
            passwordHash = "ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f", // Placeholder hash of 12345678
            creationTimestamp = System.currentTimeMillis() - Random.nextLong(10000, 86400000)
        )
        mockProfiles.add(profile)
    }
    return mockProfiles
}

/**
 * Creates a list of mock ExpenseEntity objects with dynamic, randomized foreign keys.
 *
 * @param count The number of mock expenses to generate.
 * @param profileRange A range of valid profile IDs to assign to expenses.
 * @param accountRange A range of valid account IDs to assign to expenses.
 * @param cardRange A range of valid card IDs to assign to expenses.
 * @param categoryRange A range of valid category IDs to assign to expenses.
 * @return A list of mock ExpenseEntity objects.
 */
fun createMockExpenses(
    count: Int,
    profileRange: LongRange,
    accountRange: LongRange,
    cardRange: LongRange,
    categoryRange: LongRange
): List<ExpenseEntity> {
    val mockExpenses = mutableListOf<ExpenseEntity>()
    val titles = listOf(
        "Groceries from Market", "Lunch with colleagues", "Gasoline fill-up",
        "Movie tickets", "Online subscription", "Coffee run", "Dinner out",
        "Public transport pass", "New book", "Pharmacy"
    )

    val oneYearInMillis = TimeUnit.DAYS.toMillis(365)
    val now = System.currentTimeMillis()

    for (i in 1..count) {
        // Randomly decide if the payment is by card or account for realism
        val isCardPayment = Random.nextBoolean() && !cardRange.isEmpty()

        val expense = ExpenseEntity(
            // id is 0 so Room can auto-generate it
            profileOwnerId = if (profileRange.isEmpty()) 1L else profileRange.random(),

            // Assign either an accountId or a cardId, but not both
            accountId = if (isCardPayment || accountRange.isEmpty()) null else accountRange.random(),
            cardId = if (isCardPayment) cardRange.random() else null,

            categoryId = if (categoryRange.isEmpty()) 1L else categoryRange.random(),
            sourceDueId = null,

            amount = Random.nextDouble(5.0, 500.0),
            currency = "USD",
            isSend = Random.nextBoolean(),
            title = "${titles.random()} #${i}",
            description = "Mock transaction for testing purposes.",
            date = now - Random.nextLong(0, oneYearInMillis),
            iconName = null,
            imageUri = null
        )
        mockExpenses.add(expense)
    }
    return mockExpenses
}


/**
 * Creates a list of mock CategoryEntity objects for a specific profile.
 *
 * @param profileOwnerId The ID of the profile to associate these categories with.
 * @return A list of predefined CategoryEntity objects.
 */
fun createMockCategories(profileOwnerId: Long): List<CategoryEntity> {
    // A predefined list of common categories with names, colors, and icon identifiers.
    val categoryData = listOf(
        Triple("Food & Drinks", "#FF6347", "ic_food"),
        Triple("Shopping", "#4682B4", "ic_shopping"),
        Triple("Transport", "#32CD32", "ic_transport"),
        Triple("Bills & Fees", "#FFD700", "ic_bills"),
        Triple("Health", "#DC143C", "ic_health"),
        Triple("Entertainment", "#8A2BE2", "ic_entertainment"),
        Triple("Groceries", "#20B2AA", "ic_groceries")
    )

    val mockCategories = mutableListOf<CategoryEntity>()

    categoryData.forEachIndexed { index, (name, color, icon) ->
        val category = CategoryEntity(
            // id is 0 so Room can auto-generate it
            profileOwnerId = profileOwnerId,
            name = name,
            colorHex = color,
            iconName = icon,
            displayOrder = index // Sets display order sequentially (0, 1, 2, ...)
        )
        mockCategories.add(category)
    }

    return mockCategories
}


/**
 * Creates a list of mock AccountEntity objects for a specific profile.
 *
 * @param profileOwnerId The ID of the profile to associate these account with.
 * @return A list of predefined AccountEntity objects.
 */
fun createMockAccounts(profileOwnerId: Long): List<AccountEntity> {
    val mockAccounts = mutableListOf<AccountEntity>()

    // A predefined list of common account. One is marked as default.
    val accountData = listOf(
        Pair("Bank Account", true),
        Pair("Cash", false),
        Pair("Credit Card", false)
    )

    accountData.forEachIndexed { index, (name, isDefault) ->
        val account = AccountEntity(
            // id is 0 so Room can auto-generate it
            profileOwnerId = profileOwnerId,
            name = name,
            description = "A standard $name account for transactions.",
            currency = "USD",
            balance = Random.nextDouble(500.0, 5000.0), // Assigns a random starting balance
            creationTimestamp = System.currentTimeMillis() - Random.nextLong(1000, 100000),
            isDefault = isDefault,
            displayOrder = index // Sets display order sequentially (0, 1, 2)
        )
        mockAccounts.add(account)
    }
    return mockAccounts
}


/**
 * Creates a list of mock CardEntity objects for a specific profile.
 *
 * @param profileOwnerId The ID of the profile to associate these cards with.
 * @param holderName The name of the cardholder.
 * @return A list of predefined CardEntity objects.
 */
fun createMockCards(profileOwnerId: Long, holderName: String): List<CardEntity> {
    val mockCards = mutableListOf<CardEntity>()

    // A predefined list of common card types.
    val cardData = listOf(
        // (Card Company, Type, Hex Color, IsDefault)
        Triple("Visa", "Credit Card", "#1A1F71"),
        Triple("Mastercard", "Debit Card", "#EB001B"),
        Triple("American Express", "Credit Card", "#2671B9")
    )

    cardData.forEachIndexed { index, (company, type, color) ->
        // Generate an expiration date 2-4 years in the future.
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, Random.nextInt(2, 5))
        val expiration = calendar.timeInMillis

        val card = CardEntity(
            // id is 0 so Room can auto-generate it
            profileOwnerId = profileOwnerId,
            holderName = holderName,
            type = type,
            balance = Random.nextDouble(1000.0, 10000.0), // Represents limit or balance
            currency = "USD",
            cardCompany = company,
            cardLastFourDigits = Random.nextInt(1000, 9999), // Random last 4 digits
            expirationDate = expiration,
            addedAt = System.currentTimeMillis() - Random.nextLong(1000, 100000),
            isDefault = false,
            hexColor = color,
            displayOrder = index // Sets display order sequentially
        )
        mockCards.add(card)
    }
    return mockCards
}