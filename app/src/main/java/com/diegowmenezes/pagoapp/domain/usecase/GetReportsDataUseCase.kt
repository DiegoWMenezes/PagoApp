package com.diegowmenezes.pagoapp.domain.usecase

import com.diegowmenezes.pagoapp.domain.model.ReportsData
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetReportsDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<ReportsData> {
        return combine(
            repository.getMonthlyTrend(DEFAULT_LIMIT_MONTHS),
            repository.getTopRecipients(DEFAULT_LIMIT_RECIPIENTS),
            repository.getSpendingByCategory()
        ) { monthlyTrend, topRecipients, spendingByCategory ->
            ReportsData(
                monthlyTrend = monthlyTrend,
                topRecipients = topRecipients,
                spendingByCategory = spendingByCategory
            )
        }
    }

    companion object {
        private const val DEFAULT_LIMIT_MONTHS = 12
        private const val DEFAULT_LIMIT_RECIPIENTS = 10
    }
}