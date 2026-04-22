package com.diegowmenezes.pagoapp.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diegowmenezes.pagoapp.data.local.PagoAppDatabase
import com.diegowmenezes.pagoapp.data.local.dao.TransactionDao
import com.diegowmenezes.pagoapp.data.local.entity.TransactionEntity
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.ZoneId

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var database: PagoAppDatabase
    private lateinit var transactionDao: TransactionDao

    private val nowMillis = LocalDateTime.now()
        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PagoAppDatabase::class.java
        ).allowMainThreadQueries().build()
        transactionDao = database.transactionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createEntity(
        id: Long = 0,
        amountCents: Long = -5000L,
        paymentType: String = PaymentType.PIX.name,
        status: String = TransactionStatus.CONCLUIDO.name,
        recipientName: String = "Mercado Extra",
        description: String = "Compras",
        category: String = Category.ALIMENTACAO.name,
        pixKey: String? = null,
        bankCode: String? = null,
        installments: Int? = null,
        createdAt: Long = nowMillis
    ) = TransactionEntity(
        id = id,
        amountCents = amountCents,
        paymentType = paymentType,
        status = status,
        recipientName = recipientName,
        description = description,
        category = category,
        pixKey = pixKey,
        bankCode = bankCode,
        installments = installments,
        createdAt = createdAt
    )

    @Test
    fun insertAndRetrieve() = runTest {
        val entity = createEntity()
        val insertedId = transactionDao.insert(entity)

        val transactions = transactionDao.getAllTransactions().first()
        assertEquals(1, transactions.size)
        assertEquals(insertedId, transactions[0].id)
        assertEquals(-5000L, transactions[0].amountCents)
        assertEquals("Mercado Extra", transactions[0].recipientName)
        assertEquals(PaymentType.PIX.name, transactions[0].paymentType)
        assertEquals(TransactionStatus.CONCLUIDO.name, transactions[0].status)
        assertEquals(Category.ALIMENTACAO.name, transactions[0].category)
    }

    @Test
    fun getBalanceSummaryCalculatesCorrectly() = runTest {
        transactionDao.insert(createEntity(amountCents = 100000L))
        transactionDao.insert(createEntity(amountCents = 50000L))
        transactionDao.insert(createEntity(amountCents = -30000L))
        transactionDao.insert(createEntity(amountCents = -20000L))

        val summary = transactionDao.getBalanceSummary()

        assertEquals(150000L, summary.totalIncomeCents)
        assertEquals(50000L, summary.totalExpenseCents)
        assertEquals(100000L, summary.balanceCents)
        assertEquals(4, summary.totalTransactionCount)
    }

    @Test
    fun getFilteredTransactionsWithNullReturnsAll() = runTest {
        transactionDao.insert(createEntity(paymentType = PaymentType.PIX.name))
        transactionDao.insert(createEntity(paymentType = PaymentType.BOLETO.name))

        val result = transactionDao.getFilteredTransactions(
            paymentType = null,
            status = null,
            category = null,
            startMillis = 0L,
            endMillis = Long.MAX_VALUE
        ).first()
        assertEquals(2, result.size)
    }

    @Test
    fun filterByPaymentType() = runTest {
        transactionDao.insert(createEntity(paymentType = PaymentType.PIX.name))
        transactionDao.insert(createEntity(paymentType = PaymentType.BOLETO.name))
        transactionDao.insert(createEntity(paymentType = PaymentType.CARTAO_CREDITO.name))

        val result = transactionDao.getFilteredTransactions(
            paymentType = PaymentType.PIX.name,
            status = null,
            category = null,
            startMillis = 0L,
            endMillis = Long.MAX_VALUE
        ).first()
        assertEquals(1, result.size)
        assertEquals(PaymentType.PIX.name, result[0].paymentType)
    }

    @Test
    fun filterByCategory() = runTest {
        transactionDao.insert(createEntity(category = Category.ALIMENTACAO.name))
        transactionDao.insert(createEntity(category = Category.TRANSPORTE.name))

        val result = transactionDao.getFilteredTransactions(
            paymentType = null,
            status = null,
            category = Category.ALIMENTACAO.name,
            startMillis = 0L,
            endMillis = Long.MAX_VALUE
        ).first()
        assertEquals(1, result.size)
        assertEquals(Category.ALIMENTACAO.name, result[0].category)
    }

    @Test
    fun getRecentTransactionsReturnsLimitedResults() = runTest {
        for (i in 1..10) {
            transactionDao.insert(createEntity(createdAt = nowMillis - (10 - i) * 1000L))
        }

        val result = transactionDao.getRecentTransactions(5).first()
        assertEquals(5, result.size)
    }

    @Test
    fun searchTransactionsReturnsMatchingResults() = runTest {
        transactionDao.insert(createEntity(recipientName = "Mercado Extra"))
        transactionDao.insert(createEntity(recipientName = "Farmacia Popular"))
        transactionDao.insert(createEntity(recipientName = "Posto Shell"))

        val result = transactionDao.searchTransactions("Mercado").first()
        assertEquals(1, result.size)
        assertEquals("Mercado Extra", result[0].recipientName)
    }

    @Test
    fun searchTransactionsReturnsEmptyForNoMatch() = runTest {
        transactionDao.insert(createEntity(recipientName = "Mercado Extra"))

        val result = transactionDao.searchTransactions("Inexistente").first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getMonthlyTrendReturnsGroupedByMonth() = runTest {
        val marchMillis = LocalDateTime.of(2026, 3, 15, 10, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val februaryMillis = LocalDateTime.of(2026, 2, 10, 10, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        transactionDao.insert(createEntity(amountCents = -5000L, createdAt = marchMillis))
        transactionDao.insert(createEntity(amountCents = -8000L, createdAt = februaryMillis))

        val trend = transactionDao.getMonthlyTrend(12).first()
        assertTrue(trend.size >= 2)
    }

    @Test
    fun getTopRecipientsReturnsOrderedByAmount() = runTest {
        transactionDao.insert(createEntity(recipientName = "Padaria", amountCents = -2000L))
        transactionDao.insert(createEntity(recipientName = "Supermercado", amountCents = -15000L))

        val recipients = transactionDao.getTopRecipients(10).first()
        assertTrue(recipients.size >= 2)
        assertEquals("Supermercado", recipients[0].recipientName)
    }
}