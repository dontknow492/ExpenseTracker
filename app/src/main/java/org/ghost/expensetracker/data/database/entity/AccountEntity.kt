package org.ghost.expensetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This entity is now production-ready, following best practices for naming.
@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profile_owner_id", "name"], unique = true),
    ]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "profile_owner_id")
    val profileOwnerId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,


    @ColumnInfo(name = "currency")
    val currency: String,

    @ColumnInfo(name = "balance")
    val balance: Double,

    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean,

    @ColumnInfo(name = "display_order")
    val displayOrder: Int // <-- Add this new column
)