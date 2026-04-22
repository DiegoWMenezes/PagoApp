package com.diegowmenezes.pagoapp.domain.model

data class CategorySummary(
    val category: String,
    val transactionCount: Int,
    val totalAmountCents: Long
)