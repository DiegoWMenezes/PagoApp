package com.diegowmenezes.pagoapp.domain.model

data class RecipientSummary(
    val recipientName: String,
    val transactionCount: Int,
    val totalAmountCents: Long
)