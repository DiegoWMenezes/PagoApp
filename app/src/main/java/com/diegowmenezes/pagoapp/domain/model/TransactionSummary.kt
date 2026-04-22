package com.diegowmenezes.pagoapp.domain.model

data class TransactionSummary(
    val balanceCents: Long,
    val totalIncomeCents: Long,
    val totalExpenseCents: Long,
    val totalTransactionCount: Int
)