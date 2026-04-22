package com.diegowmenezes.pagoapp.domain.model

data class MonthlySummary(
    val month: String,
    val incomeCents: Long,
    val expenseCents: Long,
    val transactionCount: Int
)