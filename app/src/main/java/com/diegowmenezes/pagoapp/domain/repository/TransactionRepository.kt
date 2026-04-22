package com.diegowmenezes.pagoapp.domain.repository

import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.CategorySummary
import com.diegowmenezes.pagoapp.domain.model.MonthlySummary
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.PaymentTypeSummary
import com.diegowmenezes.pagoapp.domain.model.RecipientSummary
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.model.TransactionSummary
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getTransactions(): Flow<List<Transaction>>

    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    suspend fun getTransactionById(id: Long): Transaction?

    suspend fun addTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun updateStatus(id: Long, status: TransactionStatus)

    suspend fun getBalanceSummary(): TransactionSummary

    fun getSpendingByPaymentType(): Flow<List<PaymentTypeSummary>>

    fun getMonthlyTrend(limitMonths: Int): Flow<List<MonthlySummary>>

    fun getTopRecipients(limit: Int): Flow<List<RecipientSummary>>

    fun getSpendingByCategory(): Flow<List<CategorySummary>>

    fun getFilteredTransactions(
        paymentType: PaymentType?,
        status: TransactionStatus?,
        category: Category?,
        startMillis: Long,
        endMillis: Long
    ): Flow<List<Transaction>>

    fun searchTransactions(query: String): Flow<List<Transaction>>
}