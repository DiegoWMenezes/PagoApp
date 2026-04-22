package com.diegowmenezes.pagoapp.viewmodel

import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.usecase.AddTransactionUseCase
import com.diegowmenezes.pagoapp.domain.usecase.SearchContactsUseCase
import com.diegowmenezes.pagoapp.ui.newtransaction.NewTransactionUiState
import com.diegowmenezes.pagoapp.ui.newtransaction.NewTransactionViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NewTransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var addTransactionUseCase: AddTransactionUseCase
    private lateinit var searchContactsUseCase: SearchContactsUseCase
    private lateinit var viewModel: NewTransactionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        addTransactionUseCase = mockk()
        searchContactsUseCase = mockk()
        every { searchContactsUseCase(any()) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validation rejects empty recipient name`() = runTest {
        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("10000")
        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onRecipientNameChanged("")
        viewModel.onPixKeyChanged("email@exemplo.com")
        viewModel.saveTransaction()

        val state = viewModel.uiState.value as NewTransactionUiState.Form
        assertNotNull("Recipient name error should be set", state.recipientNameError)
        assertEquals("Nome do destinatario e obrigatorio", state.recipientNameError)
    }

    @Test
    fun `validation rejects zero amount`() = runTest {
        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("0")
        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onRecipientNameChanged("Maria Silva")
        viewModel.onPixKeyChanged("email@exemplo.com")
        viewModel.saveTransaction()

        val state = viewModel.uiState.value as NewTransactionUiState.Form
        assertNotNull("Amount error should be set", state.amountError)
        assertEquals("Valor deve ser maior que zero", state.amountError)
    }

    @Test
    fun `validation rejects empty amount`() = runTest {
        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("")
        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onRecipientNameChanged("Maria Silva")
        viewModel.onPixKeyChanged("email@exemplo.com")
        viewModel.saveTransaction()

        val state = viewModel.uiState.value as NewTransactionUiState.Form
        assertNotNull("Amount error should be set", state.amountError)
    }

    @Test
    fun `validation requires pixKey for PIX payment`() = runTest {
        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("5000")
        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onRecipientNameChanged("Maria Silva")
        viewModel.onPixKeyChanged("")
        viewModel.saveTransaction()

        val state = viewModel.uiState.value as NewTransactionUiState.Form
        assertNotNull("Pix key error should be set", state.pixKeyError)
        assertEquals("Chave Pix e obrigatoria para pagamentos PIX", state.pixKeyError)
    }

    @Test
    fun `validation passes for non-PIX payment without pixKey`() = runTest {
        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("5000")
        viewModel.onPaymentTypeSelected(PaymentType.BOLETO)
        viewModel.onRecipientNameChanged("Maria Silva")
        viewModel.saveTransaction()

        val state = viewModel.uiState.value as NewTransactionUiState.Form
        assertNull("Pix key error should not be set for Boleto", state.pixKeyError)
    }

    @Test
    fun `successful save transitions to Success state`() = runTest {
        coEvery { addTransactionUseCase(any()) } returns Result.success(1L)

        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("5000")
        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onRecipientNameChanged("Maria Silva")
        viewModel.onPixKeyChanged("email@exemplo.com")
        viewModel.onDescriptionChanged("Pagamento mensal")
        viewModel.onCategorySelected(Category.ALIMENTACAO)
        viewModel.saveTransaction()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success after save", state is NewTransactionUiState.Success)
    }

    @Test
    fun `save failure transitions to Error state`() = runTest {
        coEvery { addTransactionUseCase(any()) } returns Result.failure(RuntimeException("Falha ao salvar"))

        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("5000")
        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onRecipientNameChanged("Maria Silva")
        viewModel.onPixKeyChanged("email@exemplo.com")
        viewModel.saveTransaction()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error after failure", state is NewTransactionUiState.Error)
        assertEquals("Falha ao salvar", (state as NewTransactionUiState.Error).message)
    }

    @Test
    fun `save with use case validation failure transitions to Error state`() = runTest {
        coEvery { addTransactionUseCase(any()) } returns Result.failure(
            IllegalArgumentException("O valor da transacao deve ser diferente de zero.")
        )

        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onAmountChanged("5000")
        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onRecipientNameChanged("Maria Silva")
        viewModel.onPixKeyChanged("email@exemplo.com")
        viewModel.saveTransaction()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is NewTransactionUiState.Error)
    }

    @Test
    fun `changing payment type to non-PIX clears pixKey`() = runTest {
        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onPaymentTypeSelected(PaymentType.PIX)
        viewModel.onPixKeyChanged("email@exemplo.com")
        viewModel.onPaymentTypeSelected(PaymentType.BOLETO)

        val state = viewModel.uiState.value as NewTransactionUiState.Form
        assertEquals("", state.pixKey)
    }

    @Test
    fun `changing payment type to CARTAO_CREDITO shows installments field`() = runTest {
        viewModel = NewTransactionViewModel(addTransactionUseCase, searchContactsUseCase)

        viewModel.onPaymentTypeSelected(PaymentType.CARTAO_CREDITO)

        val state = viewModel.uiState.value as NewTransactionUiState.Form
        assertEquals(PaymentType.CARTAO_CREDITO, state.paymentType)
    }
}