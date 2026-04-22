package com.diegowmenezes.pagoapp.viewmodel

import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.usecase.FilterTransactionsUseCase
import com.diegowmenezes.pagoapp.ui.transactions.TransactionListUiState
import com.diegowmenezes.pagoapp.ui.transactions.TransactionListViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class TransactionListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var filterTransactionsUseCase: FilterTransactionsUseCase
    private lateinit var viewModel: TransactionListViewModel

    private val sampleTransactions = listOf(
        Transaction(
            id = 1L,
            amountCents = -5000L,
            paymentType = PaymentType.PIX,
            status = TransactionStatus.CONCLUIDO,
            recipientName = "Mercado Extra",
            description = "Compras",
            category = Category.ALIMENTACAO,
            createdAt = LocalDateTime.now()
        ),
        Transaction(
            id = 2L,
            amountCents = -12000L,
            paymentType = PaymentType.CARTAO_CREDITO,
            status = TransactionStatus.PENDENTE,
            recipientName = "Loja ABC",
            description = "Roupas",
            category = Category.LAZER,
            createdAt = LocalDateTime.now()
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        filterTransactionsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filter by payment type returns filtered transactions`() = runTest {
        val pixTransactions = sampleTransactions.filter { it.paymentType == PaymentType.PIX }

        every {
            filterTransactionsUseCase(
                paymentType = PaymentType.PIX,
                status = null,
                category = null,
                startMillis = any(),
                endMillis = any()
            )
        } returns flowOf(pixTransactions)

        every {
            filterTransactionsUseCase(
                paymentType = null,
                status = null,
                category = null,
                startMillis = any(),
                endMillis = any()
            )
        } returns flowOf(sampleTransactions)

        viewModel = TransactionListViewModel(filterTransactionsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is TransactionListUiState.Success)
        val transactions = (state as TransactionListUiState.Success).transactions
        assertEquals(1, transactions.size)
        assertEquals(PaymentType.PIX, transactions[0].paymentType)
    }

    @Test
    fun `filter by category returns filtered transactions`() = runTest {
        val alimentacaoTransactions = sampleTransactions.filter { it.category == Category.ALIMENTACAO }

        every {
            filterTransactionsUseCase(
                paymentType = null,
                status = null,
                category = Category.ALIMENTACAO,
                startMillis = any(),
                endMillis = any()
            )
        } returns flowOf(alimentacaoTransactions)

        every {
            filterTransactionsUseCase(
                paymentType = null,
                status = null,
                category = null,
                startMillis = any(),
                endMillis = any()
            )
        } returns flowOf(sampleTransactions)

        viewModel = TransactionListViewModel(filterTransactionsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCategorySelected(Category.ALIMENTACAO)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is TransactionListUiState.Success)
        val transactions = (state as TransactionListUiState.Success).transactions
        assertEquals(1, transactions.size)
        assertEquals(Category.ALIMENTACAO, transactions[0].category)
    }

    @Test
    fun `search query triggers reload with filters`() = runTest {
        every {
            filterTransactionsUseCase(
                paymentType = null,
                status = null,
                category = null,
                startMillis = any(),
                endMillis = any()
            )
        } returns flowOf(sampleTransactions)

        viewModel = TransactionListViewModel(filterTransactionsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Mercado")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is TransactionListUiState.Success)
    }

    @Test
    fun `empty result returns Success with empty list`() = runTest {
        every {
            filterTransactionsUseCase(
                paymentType = any(),
                status = any(),
                category = any(),
                startMillis = any(),
                endMillis = any()
            )
        } returns flowOf(emptyList())

        viewModel = TransactionListViewModel(filterTransactionsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPaymentTypeSelected(PaymentType.TED)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is TransactionListUiState.Success)
        val transactions = (state as TransactionListUiState.Success).transactions
        assertEquals(0, transactions.size)
    }

    @Test
    fun `error state is set when use case throws`() = runTest {
        every {
            filterTransactionsUseCase(any(), any(), any(), any(), any())
        } throws RuntimeException("Erro de rede")

        viewModel = TransactionListViewModel(filterTransactionsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is TransactionListUiState.Error)
        assertEquals("Erro de rede", (state as TransactionListUiState.Error).message)
    }

    @Test
    fun `toggling same payment type deselects it`() = runTest {
        every {
            filterTransactionsUseCase(any(), any(), any(), any(), any())
        } returns flowOf(sampleTransactions)

        viewModel = TransactionListViewModel(filterTransactionsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(PaymentType.PIX, viewModel.selectedPaymentType.value)

        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(null, viewModel.selectedPaymentType.value)
    }
}