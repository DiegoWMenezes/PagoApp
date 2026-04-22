package com.diegowmenezes.pagoapp.viewmodel

import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.PaymentTypeSummary
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.TransactionSummary
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import com.diegowmenezes.pagoapp.domain.usecase.GetTransactionSummaryUseCase
import com.diegowmenezes.pagoapp.ui.dashboard.DashboardUiState
import com.diegowmenezes.pagoapp.ui.dashboard.DashboardViewModel
import io.mockk.coEvery
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

class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getTransactionSummaryUseCase: GetTransactionSummaryUseCase
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var viewModel: DashboardViewModel

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
        )
    )

    private val sampleSpendingByType = listOf(
        PaymentTypeSummary(PaymentType.PIX.name, 5, 15000L),
        PaymentTypeSummary(PaymentType.CARTAO_CREDITO.name, 3, 8000L)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTransactionSummaryUseCase = mockk()
        transactionRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        coEvery { getTransactionSummaryUseCase() } returns TransactionSummary(
            balanceCents = 10000L,
            totalIncomeCents = 20000L,
            totalExpenseCents = 10000L,
            totalTransactionCount = 5
        )
        every { transactionRepository.getRecentTransactions(any()) } returns flowOf(sampleTransactions)
        every { transactionRepository.getSpendingByPaymentType() } returns flowOf(sampleSpendingByType)

        viewModel = DashboardViewModel(getTransactionSummaryUseCase, transactionRepository)

        val state = viewModel.uiState.value
        assertTrue("Initial state should be Loading", state is DashboardUiState.Loading)
    }

    @Test
    fun `loadData sets Success state with correct data`() = runTest {
        coEvery { getTransactionSummaryUseCase() } returns TransactionSummary(
            balanceCents = 10000L,
            totalIncomeCents = 20000L,
            totalExpenseCents = 10000L,
            totalTransactionCount = 5
        )
        every { transactionRepository.getRecentTransactions(any()) } returns flowOf(sampleTransactions)
        every { transactionRepository.getSpendingByPaymentType() } returns flowOf(sampleSpendingByType)

        viewModel = DashboardViewModel(getTransactionSummaryUseCase, transactionRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is DashboardUiState.Success)
        val successState = state as DashboardUiState.Success
        assertEquals(10000L, successState.balanceCents)
        assertEquals(20000L, successState.totalIncomeCents)
        assertEquals(10000L, successState.totalExpenseCents)
        assertEquals(5, successState.totalTransactions)
        assertEquals(1, successState.recentTransactions.size)
        assertEquals(2, successState.spendingByType.size)
    }

    @Test
    fun `loadData handles error and sets Error state`() = runTest {
        coEvery { getTransactionSummaryUseCase() } throws RuntimeException("Erro de conexao")

        viewModel = DashboardViewModel(getTransactionSummaryUseCase, transactionRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is DashboardUiState.Error)
        assertEquals("Erro de conexao", (state as DashboardUiState.Error).message)
    }

    @Test
    fun `refresh reloads data successfully`() = runTest {
        coEvery { getTransactionSummaryUseCase() } returns TransactionSummary(
            balanceCents = 5000L,
            totalIncomeCents = 15000L,
            totalExpenseCents = 10000L,
            totalTransactionCount = 3
        )
        every { transactionRepository.getRecentTransactions(any()) } returns flowOf(emptyList())
        every { transactionRepository.getSpendingByPaymentType() } returns flowOf(emptyList())

        viewModel = DashboardViewModel(getTransactionSummaryUseCase, transactionRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { getTransactionSummaryUseCase() } returns TransactionSummary(
            balanceCents = 8000L,
            totalIncomeCents = 18000L,
            totalExpenseCents = 10000L,
            totalTransactionCount = 4
        )

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as DashboardUiState.Success
        assertEquals(8000L, state.balanceCents)
        assertEquals(18000L, state.totalIncomeCents)
        assertEquals(4, state.totalTransactions)
    }
}