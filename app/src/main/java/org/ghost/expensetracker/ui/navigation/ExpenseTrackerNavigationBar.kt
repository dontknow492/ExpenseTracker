package org.ghost.expensetracker.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.ghost.expensetracker.R


data class NavigationBarDataItem(
    val title: String,
    val route: MainRoute,
    @param: DrawableRes val iconId: Int,
    @param: DrawableRes val selectedIconId: Int?,
)

@Composable
fun ExpenseTrackerNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: MainRoute,
    profileOwnerId: Long,
    onNavigationItemClick: (MainRoute) -> Unit,
) {
    val navigationBarData = remember(profileOwnerId) {
        listOf(
            NavigationBarDataItem(
                title = "Home",
                route = MainRoute.Home(profileOwnerId),
                iconId = R.drawable.rounded_home_app_logo_24,
                selectedIconId = R.drawable.home_filled
            ),
            NavigationBarDataItem(
                title = "Analytics",
                route = MainRoute.Analytics(profileOwnerId),
                iconId = R.drawable.graph,
                selectedIconId = R.drawable.graph_filled
            ),
            NavigationBarDataItem(
                title = "Cards",
                route = MainRoute.Cards(profileOwnerId),
                iconId = R.drawable.card,
                selectedIconId = R.drawable.card_filled
            ),
            NavigationBarDataItem(
                title = "Profile",
                route = MainRoute.Profile(profileOwnerId),
                iconId = R.drawable.profile_circle_svgrepo_com,
                selectedIconId = R.drawable.profile_circle
            )
        )
    }

    NavigationBar(
        modifier = modifier
    ) {
        navigationBarData.forEach { item ->
            NavigationBarItem(
                selected = item.route == selectedItem,
                onClick = { onNavigationItemClick(item.route) },
                icon = {
                    if (item.route == selectedItem) {
                        Icon(
                            painter = painterResource(id = item.selectedIconId ?: item.iconId),
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = item.iconId),
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(text = item.title) }
            )
        }
    }

}

@Preview
@Composable
fun ExpenseTrackerNavigationBarPreview() {
    val profileOwnerId = 1L
    ExpenseTrackerNavigationBar(
        selectedItem = MainRoute.Home(profileOwnerId),
        profileOwnerId = profileOwnerId,
        onNavigationItemClick = {}
    )
}

