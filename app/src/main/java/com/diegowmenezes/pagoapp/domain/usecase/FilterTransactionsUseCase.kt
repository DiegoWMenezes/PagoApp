package com.diegowmenezes.pagoapp.domain.usecase

import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FilterTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(
        paymentType: PaymentType?,
        status: TransactionStatus?,
        category: Category?,
        startMillis: Long,
        endMillis: Long
    ): Flow<List<Transaction>> =
        repository.getFilteredTransactions(paymentType, status, category, startMillis, endMillis)
}