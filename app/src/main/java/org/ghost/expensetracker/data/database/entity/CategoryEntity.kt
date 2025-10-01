package org.ghost.expensetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profile_owner_id", "name"], unique = true)
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "profile_owner_id")
    val profileOwnerId: Long,

    val name: String,        // "Groceries", "Transport", "My Fun Stuff"
    @ColumnInfo(name = "color_hex")
    val colorHex: String?,   // e.g., "#FF5733" for beautiful UI charts

    @ColumnInfo(name = "icon_id")
    val iconName: String?,      // e.g., "icon_car", "icon_food" for UI

    @ColumnInfo(name = "display_order")
    val displayOrder: Int // <-- Add this new column
)
