package com.diegowmenezes.pagoapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contacts"
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(index = true, name = "name")
    val name: String,

    @ColumnInfo(name = "document")
    val document: String,

    @ColumnInfo(name = "bank_code")
    val bankCode: String? = null,

    @ColumnInfo(name = "bank_name")
    val bankName: String? = null,

    @ColumnInfo(name = "agency")
    val agency: String? = null,

    @ColumnInfo(name = "account")
    val account: String? = null,

    @ColumnInfo(name = "pix_key")
    val pixKey: String? = null,

    @ColumnInfo(name = "pix_key_type")
    val pixKeyType: String? = null,

    @ColumnInfo(defaultValue = "0", name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)