package org.ghost.expensetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profile_owner_id", "card_company", "card_last_four_digits"], unique = true)
    ]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "profile_owner_id")
    val profileOwnerId: Long,

    @ColumnInfo(name = "holder_name")
    val holderName: String,

    val type: String,

    val balance: Double,

    val currency: String,

    @ColumnInfo(name = "card_company")
    val cardCompany: String,

    @ColumnInfo(name = "card_last_four_digits")
    val cardLastFourDigits: Int,

    @ColumnInfo(name = "expiration_date")
    val expirationDate: Long?,

    @ColumnInfo(name = "added_at")
    val addedAt: Long,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean,

    @ColumnInfo(name = "hex_color")
    val hexColor: String?,

    @ColumnInfo(name = "display_order")
    val displayOrder: Int // <-- Add this new column
)
