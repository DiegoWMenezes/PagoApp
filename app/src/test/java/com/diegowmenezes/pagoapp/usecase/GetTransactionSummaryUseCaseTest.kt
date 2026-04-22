package com.diegowmenezes.pagoapp.usecase

import com.diegowmenezes.pagoapp.domain.model.TransactionSummary
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import com.diegowmenezes.pagoapp.domain.usecase.GetTransactionSummaryUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTransactionSummaryUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var useCase: GetTransactionSummaryUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        transactionRepository = mockk()
        useCase = GetTransactionSummaryUseCase(transactionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke calls repository getBalanceSummary`() = runTest {
        val expectedSummary = TransactionSummary(
            balanceCents = 15000L,
            totalIncomeCents = 50000L,
            totalExpenseCents = 35000L,
            totalTransactionCount = 12
        )

        coEvery { transactionRepository.getBalanceSummary() } returns expectedSummary

        val result = useCase()

        assertEquals(15000L, result.balanceCents)
        assertEquals(50000L, result.totalIncomeCents)
        assertEquals(35000L, result.totalExpenseCents)
        assertEquals(12, result.totalTransactionCount)
        verify { coEvery { transactionRepository.getBalanceSummary() } }
    }

    @Test
    fun `invoke propagates error from repository`() = runTest {
        coEvery { transactionRepository.getBalanceSummary() } throws RuntimeException("DB Error")

        var exceptionThrown = false
        var exceptionMessage = ""
        try {
            useCase()
        } catch (e: RuntimeException) {
            exceptionThrown = true
            exceptionMessage = e.message ?: ""
        }

        assertEquals(true, exceptionThrown)
        assertEquals("DB Error", exceptionMessage)
    }

    @Test
    fun `invoke returns correct balance for zero transactions`() = runTest {
        val emptySummary = TransactionSummary(
            balanceCents = 0L,
            totalIncomeCents = 0L,
            totalExpenseCents = 0L,
            totalTransactionCount = 0
        )

        coEvery { transactionRepository.getBalanceSummary() } returns emptySummary

        val result = useCase()

        assertEquals(0L, result.balanceCents)
        assertEquals(0L, result.totalIncomeCents)
        assertEquals(0L, result.totalExpenseCents)
        assertEquals(0, result.totalTransactionCount)
    }
}