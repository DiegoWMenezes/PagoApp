package com.diegowmenezes.pagoapp.domain.model

data class ReportsData(
    val monthlyTrend: List<MonthlySummary>,
    val topRecipients: List<RecipientSummary>,
    val spendingByCategory: List<CategorySummary>
)