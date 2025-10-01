package org.ghost.expensetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "dues",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_owner_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["card_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DueEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "profile_owner_id")
    val profileOwnerId: Long,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "account_id")
    val accountId: Long?,

    @ColumnInfo(name = "card_id")
    val cardId: Long?,

    val name: String,

    val description: String?,


    val amount: Double,

    val currency: String,

    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean,

    @ColumnInfo(name = "recurrence_interval")
    val recurrenceInterval: Int?,

    @ColumnInfo(name = "recurrence_unit")
    val recurrenceUnit: String?,

    @ColumnInfo(name = "last_payment_date")
    val lastPaymentDate: Long?,

    )
