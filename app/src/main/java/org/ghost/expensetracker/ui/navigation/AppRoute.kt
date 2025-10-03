package org.ghost.expensetracker.ui.navigation

import kotlinx.serialization.Serializable

/**
 * A top-level sealed interface representing any possible navigation destination in the app.
 * This allows grouping different navigation flows (like Auth vs. Main App).
 */
@Serializable
sealed interface AppRoute


@Serializable
object OnBoardingRoute : AppRoute

@Serializable
sealed interface AuthRoute : AppRoute {
    @Serializable
    data object Login : AuthRoute

    @Serializable
    data object Register : AuthRoute

    @Serializable
    data object ForgotPassword : AuthRoute
}

@Serializable
sealed interface MainRoute : AppRoute {
    @Serializable
    data class Home(val profileOwnerId: Long) : MainRoute

    @Serializable
    data class Analytics(val profileOwnerId: Long) : MainRoute

    @Serializable
    data class Cards(val profileOwnerId: Long) : MainRoute

    @Serializable
    data class Profile(val profileOwnerId: Long) : MainRoute

}

@Serializable
sealed interface SecondaryRoute : AppRoute {
    @Serializable
    data class Category(val profileOwnerId: Long) : SecondaryRoute

    @Serializable
    data class ChangePassword(val profileOwnerId: Long) : SecondaryRoute

    @Serializable
    data class EditProfile(val profileOwnerId: Long) : SecondaryRoute

    @Serializable
    data class Accounts(val profileOwnerId: Long) : SecondaryRoute

    @Serializable
    data class Expenses(
        val profileOwnerId: Long,
        val query: String? = null,
        val accountId: Long? = null,
        val cardId: Long? = null,
        val categoryId: Long? = null,
        val minDate: Long? = null,
        val maxDate: Long? = null,
        val minAmount: Double? = null,
        val maxAmount: Double? = null,
    ) : SecondaryRoute

    @Serializable
    object AboutUs : SecondaryRoute

    @Serializable
    object Settings : SecondaryRoute

}

@Serializable
sealed interface AddRoute : AppRoute {
    @Serializable
    data class AddCard(val profileOwnerId: Long) : AddRoute

    @Serializable
    data class AddExpense(val profileOwnerId: Long, val isSend: Boolean) : AddRoute

    @Serializable
    data class AddCategory(val profileOwnerId: Long) : AddRoute

}