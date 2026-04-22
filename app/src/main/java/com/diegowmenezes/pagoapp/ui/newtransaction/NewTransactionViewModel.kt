package com.diegowmenezes.pagoapp.ui.newtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.Contact
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.usecase.AddTransactionUseCase
import com.diegowmenezes.pagoapp.domain.usecase.SearchContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NewTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val searchContactsUseCase: SearchContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewTransactionUiState>(NewTransactionUiState.Form())
    val uiState: StateFlow<NewTransactionUiState> = _uiState.asStateFlow()

    private val _contactSuggestions = MutableStateFlow<List<Contact>>(emptyList())
    val contactSuggestions: StateFlow<List<Contact>> = _contactSuggestions.asStateFlow()

    private var searchJob: Job? = null

    fun onAmountChanged(amount: String) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(amountCentsRaw = amount, amountError = null)
    }

    fun onPaymentTypeSelected(paymentType: PaymentType) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(
            paymentType = paymentType,
            pixKey = if (paymentType != PaymentType.PIX) "" else current.pixKey,
            pixKeyError = null,
            bankCode = if (paymentType != PaymentType.TED) "" else current.bankCode,
            installments = if (paymentType != PaymentType.CARTAO_CREDITO) "" else current.installments
        )
    }

    fun onRecipientNameChanged(name: String) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(recipientName = name, recipientNameError = null)
        searchContacts(name)
    }

    fun onDescriptionChanged(description: String) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(description = description)
    }

    fun onCategorySelected(category: Category) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(category = category)
    }

    fun onPixKeyChanged(pixKey: String) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(pixKey = pixKey, pixKeyError = null)
    }

    fun onBankCodeChanged(bankCode: String) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(bankCode = bankCode)
    }

    fun onInstallmentsChanged(installments: String) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(installments = installments)
    }

    fun onContactSuggestionSelected(contact: Contact) {
        val current = (_uiState.value as? NewTransactionUiState.Form) ?: return
        _uiState.value = current.copy(
            recipientName = contact.name,
            recipientNameError = null
        )
        _contactSuggestions.value = emptyList()
    }

    fun onContactSuggestionsDismissed() {
        _contactSuggestions.value = emptyList()
    }

    fun saveTransaction() {
        val form = (_uiState.value as? NewTransactionUiState.Form) ?: return

        val errors = validateForm(form)
        if (errors.hasErrors) {
            _uiState.value = form.copy(
                amountError = errors.amountError,
                recipientNameError = errors.recipientNameError,
                pixKeyError = errors.pixKeyError
            )
            return
        }

        val amountCents = form.amountCentsRaw.toLongOrNull() ?: 0L

        val transaction = Transaction(
            amountCents = -amountCents,
            paymentType = form.paymentType,
            status = TransactionStatus.PENDENTE,
            recipientName = form.recipientName.trim(),
            description = form.description.trim(),
            category = form.category,
            pixKey = form.pixKey.takeIf { it.isNotBlank() },
            bankCode = form.bankCode.takeIf { it.isNotBlank() },
            installments = form.installments.toIntOrNull(),
            createdAt = LocalDateTime.now()
        )

        viewModelScope.launch {
            _uiState.value = NewTransactionUiState.Saving
            val result = addTransactionUseCase(transaction)
            if (result.isSuccess) {
                _uiState.value = NewTransactionUiState.Success
            } else {
                _uiState.value = NewTransactionUiState.Error(
                    message = result.exceptionOrNull()?.message ?: "Erro ao salvar transacao"
                )
            }
        }
    }

    private fun validateForm(form: NewTransactionUiState.Form): ValidationResult {
        var amountError: String? = null
        var recipientNameError: String? = null
        var pixKeyError: String? = null

        val amountCents = form.amountCentsRaw.toLongOrNull() ?: 0L
        if (amountCents <= 0) {
            amountError = "Valor deve ser maior que zero"
        }

        if (form.recipientName.isBlank()) {
            recipientNameError = "Nome do destinatario e obrigatorio"
        }

        if (form.paymentType == PaymentType.PIX && form.pixKey.isBlank()) {
            pixKeyError = "Chave Pix e obrigatoria para pagamentos PIX"
        }

        return ValidationResult(
            amountError = amountError,
            recipientNameError = recipientNameError,
            pixKeyError = pixKeyError,
            hasErrors = amountError != null || recipientNameError != null || pixKeyError != null
        )
    }

    private fun searchContacts(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _contactSuggestions.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            searchContactsUseCase(query)
                .catch { _contactSuggestions.value = emptyList() }
                .collect { contacts ->
                    _contactSuggestions.value = contacts
                }
        }
    }
}

sealed interface NewTransactionUiState {
    data class Form(
        val amountCentsRaw: String = "",
        val paymentType: PaymentType = PaymentType.PIX,
        val recipientName: String = "",
        val description: String = "",
        val category: Category = Category.OUTROS,
        val pixKey: String = "",
        val bankCode: String = "",
        val installments: String = "",
        val amountError: String? = null,
        val recipientNameError: String? = null,
        val pixKeyError: String? = null
    ) : NewTransactionUiState

    data object Saving : NewTransactionUiState
    data object Success : NewTransactionUiState
    data class Error(val message: String) : NewTransactionUiState
}

private data class ValidationResult(
    val amountError: String?,
    val recipientNameError: String?,
    val pixKeyError: String?,
    val hasErrors: Boolean
)