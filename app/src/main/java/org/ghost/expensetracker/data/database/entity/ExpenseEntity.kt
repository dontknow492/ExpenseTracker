package org.ghost.expensetracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_owner_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["card_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DueEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_due_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["account_id"]),
        Index(value = ["card_id"]),
        Index(value = ["profile_owner_id"]),
        Index(value = ["source_due_id"]),
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,


    @ColumnInfo(name = "profile_owner_id")
    val profileOwnerId: Long,
    @ColumnInfo(name = "account_id")
    val accountId: Long?,
    @ColumnInfo(name = "card_id")
    val cardId: Long?,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "source_due_id")
    val sourceDueId: Long?,


    val amount: Double,
    val currency: String,
    @ColumnInfo(name = "is_send")
    val isSend: Boolean,
    val title: String,
    val description: String?,
    val date: Long,

    @ColumnInfo(name = "icon_id")
    val iconName: String?,
    @ColumnInfo(name = "image_uri")
    val imageUri: String?,
)
