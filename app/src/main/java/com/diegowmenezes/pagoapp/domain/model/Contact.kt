package com.diegowmenezes.pagoapp.domain.model

import java.time.LocalDateTime

data class Contact(
    val id: Long = 0,
    val name: String,
    val document: String,
    val bankCode: String? = null,
    val bankName: String? = null,
    val agency: String? = null,
    val account: String? = null,
    val pixKey: String? = null,
    val pixKeyType: PixKeyType? = null,
    val isFavorite: Boolean = false,
    val createdAt: LocalDateTime
)

data class ContactFrequency(
    val contact: Contact,
    val transactionCount: Int
)