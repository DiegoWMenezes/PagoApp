package com.diegowmenezes.pagoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index("payment_type"),
        Index("status"),
        Index("category"),
        Index("payment_type", "created_at")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "amount_cents")
    val amountCents: Long,

    @ColumnInfo(name = "payment_type")
    val paymentType: String,

    @ColumnInfo(defaultValue = "CONCLUIDO", name = "status")
    val status: String,

    @ColumnInfo(name = "recipient_name")
    val recipientName: String,

    @ColumnInfo(name = "recipient_document")
    val recipientDocument: String? = null,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "scheduled_date")
    val scheduledDate: Long? = null,

    @ColumnInfo(name = "pix_key")
    val pixKey: String? = null,

    @ColumnInfo(name = "pix_key_type")
    val pixKeyType: String? = null,

    @ColumnInfo(name = "bank_code")
    val bankCode: String? = null,

    @ColumnInfo(name = "installments")
    val installments: Int? = null
)