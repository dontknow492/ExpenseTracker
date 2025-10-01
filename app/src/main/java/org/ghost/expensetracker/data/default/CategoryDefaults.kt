package org.ghost.expensetracker.data.default

import androidx.compose.ui.graphics.Color
import org.ghost.expensetracker.R

data class DefaultCategory(
    val name: String,
    val iconId: Int,
    val order: Int,
    // You could add a default color here too
)

object CategoryDefaults {

    val defaultCategories = listOf(
        DefaultCategory("Home", R.drawable.rounded_home_app_logo_24, 0),
        DefaultCategory("Groceries", R.drawable.rounded_grocery_24, 1),
        DefaultCategory("Shopping", R.drawable.outline_shopping_cart_24, 2),
        DefaultCategory("Transport", R.drawable.rounded_local_gas_station_24, 3),
        DefaultCategory("Bills", R.drawable.rounded_receipt_long_24, 4),
        DefaultCategory("Food", R.drawable.rounded_fastfood_24, 5),
        DefaultCategory("Entertainment", R.drawable.rounded_movie_24, 6)
    )
    val categoryIcons = listOf(
        R.drawable.outline_shopping_cart_24,
        R.drawable.rounded_card_membership_24,
        R.drawable.rounded_fastfood_24,
        R.drawable.rounded_fitness_center_24,
        R.drawable.rounded_flight_24,
        R.drawable.rounded_home_app_logo_24,
        R.drawable.rounded_local_gas_station_24,
        R.drawable.rounded_grocery_24,
        R.drawable.rounded_movie_24,
        R.drawable.rounded_receipt_long_24,
        R.drawable.rounded_school_24,
        R.drawable.rounded_subscriptions_24,
        R.drawable.rounded_travel_24,
        R.drawable.rounded_workspace_premium_24,
    )

    val categoryColors = listOf(
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4),
        Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39),
        Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF795548)
    )
}