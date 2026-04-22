package com.diegowmenezes.pagoapp.repository

import com.diegowmenezes.pagoapp.data.local.dao.TransactionDao
import com.diegowmenezes.pagoapp.data.local.entity.TransactionEntity
import com.diegowmenezes.pagoapp.data.repository.TransactionRepositoryImpl
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.model.Category
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class TransactionRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var transactionDao: TransactionDao
    private lateinit var repository: TransactionRepositoryImpl

    private val nowMillis = LocalDateTime.now()
        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private val sampleEntity = TransactionEntity(
        id = 1L,
        amountCents = -5000L,
        paymentType = PaymentType.PIX.name,
        status = TransactionStatus.CONCLUIDO.name,
        recipientName = "Mercado Extra",
        description = "Compras",
        category = Category.ALIMENTACAO.name,
        pixKey = null,
        bankCode = null,
        installments = null,
        createdAt = nowMillis
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        transactionDao = mockk()
        repository = TransactionRepositoryImpl(transactionDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTransactions delegates to DAO and maps entities`() = runTest {
        every { transactionDao.getAllTransactions() } returns flowOf(listOf(sampleEntity))

        val result = repository.getTransactions()

        result.collect { transactions ->
            assertEquals(1, transactions.size)
            assertEquals(1L, transactions[0].id)
            assertEquals(-5000L, transactions[0].amountCents)
            assertEquals(PaymentType.PIX, transactions[0].paymentType)
            assertEquals(TransactionStatus.CONCLUIDO, transactions[0].status)
            assertEquals("Mercado Extra", transactions[0].recipientName)
            assertEquals(Category.ALIMENTACAO, transactions[0].category)
        }
    }

    @Test
    fun `getRecentTransactions delegates to DAO with correct limit`() = runTest {
        every { transactionDao.getRecentTransactions(5) } returns flowOf(listOf(sampleEntity))

        val result = repository.getRecentTransactions(5)

        result.collect { transactions ->
            assertEquals(1, transactions.size)
        }
        verify { transactionDao.getRecentTransactions(5) }
    }

    @Test
    fun `addTransaction delegates to DAO and maps domain model`() = runTest {
        coEvery { transactionDao.insert(any()) } returns 1L

        val transaction = com.diegowmenezes.pagoapp.domain.model.Transaction(
            id = 0L,
            amountCents = -8000L,
            paymentType = PaymentType.CARTAO_CREDITO,
            status = TransactionStatus.PENDENTE,
            recipientName = "Loja ABC",
            description = "Eletronicos",
            category = Category.LAZER,
            createdAt = LocalDateTime.now()
        )

        repository.addTransaction(transaction)

        verify { transactionDao.insert(match { entity ->
            entity.amountCents == -8000L &&
            entity.paymentType == PaymentType.CARTAO_CREDITO.name &&
            entity.recipientName == "Loja ABC" &&
            entity.category == Category.LAZER.name
        }) }
    }

    @Test
    fun `getFilteredTransactions with all null filters returns all transactions`() = runTest {
        every {
            transactionDao.getFilteredTransactions(null, null, null, any(), any())
        } returns flowOf(listOf(sampleEntity))

        val result = repository.getFilteredTransactions(null, null, null, 0L, Long.MAX_VALUE)

        result.collect { transactions ->
            assertEquals(1, transactions.size)
        }
    }

    @Test
    fun `getFilteredTransactions with payment type filter delegates correctly`() = runTest {
        every {
            transactionDao.getFilteredTransactions(PaymentType.PIX.name, null, null, any(), any())
        } returns flowOf(listOf(sampleEntity))

        val result = repository.getFilteredTransactions(
            paymentType = PaymentType.PIX,
            status = null,
            category = null,
            startMillis = 0L,
            endMillis = Long.MAX_VALUE
        )

        result.collect { transactions ->
            assertEquals(1, transactions.size)
        }
        verify { transactionDao.getFilteredTransactions(PaymentType.PIX.name, null, null, any(), any()) }
    }

    @Test
    fun `searchTransactions delegates to DAO with query`() = runTest {
        every { transactionDao.searchTransactions("Mercado") } returns flowOf(listOf(sampleEntity))

        val result = repository.searchTransactions("Mercado")

        result.collect { transactions ->
            assertEquals(1, transactions.size)
        }
        verify { transactionDao.searchTransactions("Mercado") }
    }
}