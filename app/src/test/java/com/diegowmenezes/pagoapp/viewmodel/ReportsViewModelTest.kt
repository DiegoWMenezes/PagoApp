package com.diegowmenezes.pagoapp.viewmodel

import com.diegowmenezes.pagoapp.domain.model.CategorySummary
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.MonthlySummary
import com.diegowmenezes.pagoapp.domain.model.RecipientSummary
import com.diegowmenezes.pagoapp.domain.model.ReportsData
import com.diegowmenezes.pagoapp.domain.usecase.GetReportsDataUseCase
import com.diegowmenezes.pagoapp.ui.reports.ReportsUiState
import com.diegowmenezes.pagoapp.ui.reports.ReportsViewModel
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

class ReportsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getReportsDataUseCase: GetReportsDataUseCase
    private lateinit var viewModel: ReportsViewModel

    private val sampleReportsData = ReportsData(
        monthlyTrend = listOf(
            MonthlySummary(
                month = "2026-03",
                incomeCents = 50000L,
                expenseCents = 30000L,
                transactionCount = 10
            ),
            MonthlySummary(
                month = "2026-02",
                incomeCents = 45000L,
                expenseCents = 35000L,
                transactionCount = 12
            )
        ),
        topRecipients = listOf(
            RecipientSummary(
                recipientName = "Mercado Extra",
                totalAmountCents = 15000L,
                transactionCount = 8
            ),
            RecipientSummary(
                recipientName = "Posto Shell",
                totalAmountCents = 8000L,
                transactionCount = 4
            )
        ),
        spendingByCategory = listOf(
            CategorySummary(
                category = Category.ALIMENTACAO.name,
                totalAmountCents = 20000L,
                transactionCount = 15
            ),
            CategorySummary(
                category = Category.TRANSPORTE.name,
                totalAmountCents = 12000L,
                transactionCount = 8
            )
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getReportsDataUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        every { getReportsDataUseCase() } returns flowOf(sampleReportsData)

        viewModel = ReportsViewModel(getReportsDataUseCase)

        val state = viewModel.uiState.value
        assertTrue("Initial state should be Loading", state is ReportsUiState.Loading)
    }

    @Test
    fun `loadReports sets Success state with reports data`() = runTest {
        every { getReportsDataUseCase() } returns flowOf(sampleReportsData)

        viewModel = ReportsViewModel(getReportsDataUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is ReportsUiState.Success)
        val data = (state as ReportsUiState.Success).data
        assertEquals(2, data.monthlyTrend.size)
        assertEquals(2, data.topRecipients.size)
        assertEquals(2, data.spendingByCategory.size)
    }

    @Test
    fun `loadReports sets Error state on failure`() = runTest {
        every { getReportsDataUseCase() } throws RuntimeException("Erro no servidor")

        viewModel = ReportsViewModel(getReportsDataUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is ReportsUiState.Error)
        assertEquals("Erro no servidor", (state as ReportsUiState.Error).message)
    }

    @Test
    fun `loadReports reloads data on retry`() = runTest {
        var callCount = 0
        every { getReportsDataUseCase() } answers {
            callCount++
            if (callCount == 1) throw RuntimeException("Erro") else flowOf(sampleReportsData)
        }

        viewModel = ReportsViewModel(getReportsDataUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ReportsUiState.Error)

        viewModel.loadReports()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ReportsUiState.Success)
        val data = (viewModel.uiState.value as ReportsUiState.Success).data
        assertEquals(2, data.monthlyTrend.size)
    }

    @Test
    fun `monthlyTrend contains correct values`() = runTest {
        every { getReportsDataUseCase() } returns flowOf(sampleReportsData)

        viewModel = ReportsViewModel(getReportsDataUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val data = (viewModel.uiState.value as ReportsUiState.Success).data
        val marchData = data.monthlyTrend[0]
        assertEquals("2026-03", marchData.month)
        assertEquals(50000L, marchData.incomeCents)
        assertEquals(30000L, marchData.expenseCents)
    }

    @Test
    fun `topRecipients contains correct values`() = runTest {
        every { getReportsDataUseCase() } returns flowOf(sampleReportsData)

        viewModel = ReportsViewModel(getReportsDataUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val data = (viewModel.uiState.value as ReportsUiState.Success).data
        val topRecipient = data.topRecipients[0]
        assertEquals("Mercado Extra", topRecipient.recipientName)
        assertEquals(15000L, topRecipient.totalAmountCents)
        assertEquals(8, topRecipient.transactionCount)
    }
}