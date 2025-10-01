package org.ghost.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil3.compose.AsyncImage
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.utils.getDrawableResourceId
import org.ghost.expensetracker.core.utils.getResourceEntryName
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.models.CategoryWithExpenseCount

@Composable // showBackground helps visualize the card
fun AddItemTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onAddNewClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth() // Make the card fill width for better gradient application
            .height(180.dp), // Give it a fixed height or let content define it
        shape = MaterialTheme.shapes.medium, // Modern Material 3 shape
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh) // Use a surface color
    ) {
        Box( // Use a Box to layer the image and the gradient over it
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Image on the right side
            AsyncImage(
                model = R.drawable.expense,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd) // Align to the right
                    .aspectRatio(1f), // Maintain aspect ratio if image is square
                contentScale = ContentScale.Crop // Use Crop to fill space
            )

            // 2. Gradient OVER the image, starting from the right edge of the Column
            // This gradient goes from Primary/Tertiary to Transparent, left-to-right
            // Its width should match the text content section
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.6f) // Take up 60% of the width for the text/button
//                    .blur(10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .background(
                        brush = Brush.horizontalGradient( // Use horizontalGradient for simplicity
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(0.65f),
                                Color.Transparent,// Start with the card's background color
                            ),
                            startX = Float.POSITIVE_INFINITY,
                            endX = 0f,
                            tileMode = TileMode.Mirror
                        )
                    )
            )

            // 3. Content (Text and Button) on the left
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f) // Match the gradient box width
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center // Center content vertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall, // headlineSmall often fits better for cards
                    color = MaterialTheme.colorScheme.onSurface // Use onSurface for text color
                )
                Spacer(Modifier.height(8.dp)) // Small gap
                Button(
                    onClick = onAddNewClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary // Use primary for button
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp) // Smaller icon
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Add New",
                        style = MaterialTheme.typography.labelLarge // labelLarge for button text
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleCategoryItem(
    modifier: Modifier = Modifier,
    category: Category,
    isSelected: Boolean, // 1. New parameter to control state
    isError: Boolean,
    onClick: () -> Unit
) {
    val width = 80.dp
    val height = 60.dp
    val defaultColor = MaterialTheme.colorScheme.secondaryContainer
    val color = remember(category.colorHex) {
        if (category.colorHex == null) {
            defaultColor
        } else {
            try {
                Color(category.colorHex.toColorInt())
            } catch (e: Exception) {
                defaultColor
            }
        }
    }

    // 2. Conditionally choose the Card's appearance
    if (isSelected) {
        // --- SELECTED STATE ---
        Card( // Use a filled Card for selection
            modifier = modifier.size(width = width, height = height),
            onClick = onClick,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            SimpleItemContent(category, color, true)
        }
    } else {
        // --- UNSELECTED STATE ---
        OutlinedCard( // Use an OutlinedCard for others
            modifier = modifier.size(width = width, height = height),
            onClick = onClick
        ) {
            SimpleItemContent(category, color, false)
        }
    }
}

// 3. Extracted the content to avoid code duplication
@Composable
private fun SimpleItemContent(category: Category, iconBackgroundColor: Color, isSelected: Boolean) {

    val context = LocalContext.current
    val iconId: Int? = remember(category.iconName) {
        if (category.iconName == null) return@remember null
        return@remember getDrawableResourceId(category.iconName, context = context)
    }

    // Use appropriate content color based on selection
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (iconId != null) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = category.name,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        } else {
            Text(
                text = category.name.firstOrNull()?.toString()?.uppercase() ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = contentColor, // Apply the dynamic color
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CategoryWithExpenseItem(
    modifier: Modifier = Modifier,
    categoryWithExpenseCount: CategoryWithExpenseCount,
    dragHandle: @Composable () -> Unit,
    onClick: (CategoryWithExpenseCount) -> Unit,
    onEditClick: (CategoryWithExpenseCount) -> Unit,
    onDeleteClick: (CategoryWithExpenseCount) -> Unit,
) {

    val category = categoryWithExpenseCount.category
    val context = LocalContext.current
    val iconId: Int? = remember(category.iconName) {
        if (category.iconName == null) return@remember null
        return@remember getDrawableResourceId(category.iconName, context = context)
    }
    Card(
        modifier = modifier.clickable { onClick(categoryWithExpenseCount) },
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (iconId != null) {
                    Icon(
                        painter = painterResource(iconId),
                        contentDescription = category.name
                    )
                } else {
                    Text(
                        text = category.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${categoryWithExpenseCount.expenseCount} " + stringResource(R.string.expenses),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(
                onClick = { onEditClick(categoryWithExpenseCount) }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
            }

            IconButton(
                onClick = { onDeleteClick(categoryWithExpenseCount) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }

            dragHandle()
        }
    }
}

@Composable
fun DragHandle(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource
) {
    IconButton(
        modifier = modifier,
        onClick = {},
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_drag_handle_24),
            contentDescription = stringResource(R.string.drag_handle)
        )
    }
}

@Preview(showBackground = !true, name = "With Icon")
@Composable
private fun CategoryWithExpenseItemPreview() {
    val iconName = getResourceEntryName(R.drawable.outline_shopping_cart_24, LocalContext.current)
    val mockCategory = Category(
        id = 1,
        name = "Groceries",
        iconName = iconName, // Using a default drawable for preview,
        colorHex = "#FF5733",
        profileOwnerId = 1,
        displayOrder = 0,
    )
    val mockCategoryWithExpenseCount = CategoryWithExpenseCount(
        category = mockCategory,
        expenseCount = 5
    )
    CategoryWithExpenseItem(
        categoryWithExpenseCount = mockCategoryWithExpenseCount,
        onClick = {},
        dragHandle = {},
        onEditClick = {},
        onDeleteClick = {}
    )
}

@Preview(showBackground = !true, name = "Without Icon")
@Composable
private fun CategoryWithExpenseItemNoIconPreview() {
    val mockCategory = Category(
        id = 2,
        name = "Utilities",
        iconName = null,
        colorHex = "#FF5733",
        profileOwnerId = 1,
        displayOrder = 0,
    )
    val mockCategoryWithExpenseCount = CategoryWithExpenseCount(
        category = mockCategory,
        expenseCount = 3
    )
    CategoryWithExpenseItem(
        categoryWithExpenseCount = mockCategoryWithExpenseCount,
        onClick = {},
        dragHandle = {},
        onEditClick = {},
        onDeleteClick = {}
    )
}