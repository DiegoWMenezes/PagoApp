package com.diegowmenezes.pagoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.diegowmenezes.pagoapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

data class BalanceSummaryResult(
    val totalIncomeCents: Long,
    val totalExpenseCents: Long,
    val balanceCents: Long,
    val totalTransactionCount: Int
)

data class PaymentTypeSummaryResult(
    val paymentType: String,
    val transactionCount: Int,
    val totalAmountCents: Long
)

data class MonthlySummaryResult(
    val month: String,
    val incomeCents: Long,
    val expenseCents: Long,
    val transactionCount: Int
)

data class RecipientSummaryResult(
    val recipientName: String,
    val transactionCount: Int,
    val totalAmountCents: Long
)

data class CategorySummaryResult(
    val category: String,
    val transactionCount: Int,
    val totalAmountCents: Long
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY created_at DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY created_at DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN amount_cents > 0 THEN amount_cents ELSE 0 END), 0) AS totalIncomeCents,
               COALESCE(SUM(CASE WHEN amount_cents < 0 THEN ABS(amount_cents) ELSE 0 END), 0) AS totalExpenseCents,
               COALESCE(SUM(amount_cents), 0) AS balanceCents,
               COUNT(*) AS totalTransactionCount
        FROM transactions WHERE status = 'CONCLUIDO'
        """
    )
    suspend fun getBalanceSummary(): BalanceSummaryResult

    @Query(
        """
        SELECT payment_type AS paymentType,
               COUNT(*) AS transactionCount,
               ABS(SUM(amount_cents)) AS totalAmountCents
        FROM transactions
        WHERE status = 'CONCLUIDO' AND amount_cents < 0
        GROUP BY payment_type
        ORDER BY totalAmountCents DESC
        """
    )
    fun getSpendingByPaymentType(): Flow<List<PaymentTypeSummaryResult>>

    @Query(
        """
        SELECT strftime('%Y-%m', created_at / 1000, 'unixepoch') AS month,
               SUM(CASE WHEN amount_cents > 0 THEN amount_cents ELSE 0 END) AS incomeCents,
               SUM(CASE WHEN amount_cents < 0 THEN ABS(amount_cents) ELSE 0 END) AS expenseCents,
               COUNT(*) AS transactionCount
        FROM transactions
        WHERE status = 'CONCLUIDO'
        GROUP BY month
        ORDER BY month DESC
        LIMIT :limitMonths
        """
    )
    fun getMonthlyTrend(limitMonths: Int): Flow<List<MonthlySummaryResult>>

    @Query(
        """
        SELECT recipient_name AS recipientName,
               COUNT(*) AS transactionCount,
               SUM(ABS(amount_cents)) AS totalAmountCents
        FROM transactions
        WHERE status = 'CONCLUIDO' AND amount_cents < 0
        GROUP BY recipient_name
        ORDER BY totalAmountCents DESC
        LIMIT :limit
        """
    )
    fun getTopRecipients(limit: Int): Flow<List<RecipientSummaryResult>>

    @Query(
        """
        SELECT category,
               COUNT(*) AS transactionCount,
               ABS(SUM(amount_cents)) AS totalAmountCents
        FROM transactions
        WHERE status = 'CONCLUIDO' AND amount_cents < 0
        GROUP BY category
        ORDER BY totalAmountCents DESC
        """
    )
    fun getSpendingByCategory(): Flow<List<CategorySummaryResult>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE (:paymentType IS NULL OR payment_type = :paymentType)
          AND (:status IS NULL OR status = :status)
          AND (:category IS NULL OR category = :category)
          AND created_at BETWEEN :startMillis AND :endMillis
        ORDER BY created_at DESC
        """
    )
    fun getFilteredTransactions(
        paymentType: String?,
        status: String?,
        category: String?,
        startMillis: Long,
        endMillis: Long
    ): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE recipient_name LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY created_at DESC
        """
    )
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("UPDATE transactions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}