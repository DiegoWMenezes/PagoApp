package com.diegowmenezes.pagoapp.domain.model

data class PaymentTypeSummary(
    val paymentType: String,
    val transactionCount: Int,
    val totalAmountCents: Long
)