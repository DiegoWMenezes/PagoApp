package com.diegowmenezes.pagoapp.data.repository

import com.diegowmenezes.pagoapp.data.local.dao.TransactionDao
import com.diegowmenezes.pagoapp.data.mapper.toDomain
import com.diegowmenezes.pagoapp.data.mapper.toEntity
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.CategorySummary
import com.diegowmenezes.pagoapp.domain.model.MonthlySummary
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.PaymentTypeSummary
import com.diegowmenezes.pagoapp.domain.model.RecipientSummary
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.model.TransactionSummary
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }

    override suspend fun addTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun updateStatus(id: Long, status: TransactionStatus) {
        transactionDao.updateStatus(id, status.name)
    }

    override suspend fun getBalanceSummary(): TransactionSummary {
        val result = transactionDao.getBalanceSummary()
        return TransactionSummary(
            balanceCents = result.balanceCents,
            totalIncomeCents = result.totalIncomeCents,
            totalExpenseCents = result.totalExpenseCents,
            totalTransactionCount = result.totalTransactionCount
        )
    }

    override fun getSpendingByPaymentType(): Flow<List<PaymentTypeSummary>> {
        return transactionDao.getSpendingByPaymentType().map { results ->
            results.map { result ->
                PaymentTypeSummary(
                    paymentType = result.paymentType,
                    transactionCount = result.transactionCount,
                    totalAmountCents = result.totalAmountCents
                )
            }
        }
    }

    override fun getMonthlyTrend(limitMonths: Int): Flow<List<MonthlySummary>> {
        return transactionDao.getMonthlyTrend(limitMonths).map { results ->
            results.map { result ->
                MonthlySummary(
                    month = result.month,
                    incomeCents = result.incomeCents,
                    expenseCents = result.expenseCents,
                    transactionCount = result.transactionCount
                )
            }
        }
    }

    override fun getTopRecipients(limit: Int): Flow<List<RecipientSummary>> {
        return transactionDao.getTopRecipients(limit).map { results ->
            results.map { result ->
                RecipientSummary(
                    recipientName = result.recipientName,
                    transactionCount = result.transactionCount,
                    totalAmountCents = result.totalAmountCents
                )
            }
        }
    }

    override fun getSpendingByCategory(): Flow<List<CategorySummary>> {
        return transactionDao.getSpendingByCategory().map { results ->
            results.map { result ->
                CategorySummary(
                    category = result.category,
                    transactionCount = result.transactionCount,
                    totalAmountCents = result.totalAmountCents
                )
            }
        }
    }

    override fun getFilteredTransactions(
        paymentType: PaymentType?,
        status: TransactionStatus?,
        category: Category?,
        startMillis: Long,
        endMillis: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getFilteredTransactions(
            paymentType = paymentType?.name,
            status = status?.name,
            category = category?.name,
            startMillis = startMillis,
            endMillis = endMillis
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}