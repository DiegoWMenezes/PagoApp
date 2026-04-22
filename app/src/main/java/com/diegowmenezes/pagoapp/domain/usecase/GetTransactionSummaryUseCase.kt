package com.diegowmenezes.pagoapp.domain.usecase

import com.diegowmenezes.pagoapp.domain.model.TransactionSummary
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(): TransactionSummary = repository.getBalanceSummary()
}