package com.diegowmenezes.pagoapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import com.diegowmenezes.pagoapp.domain.usecase.FilterTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val filterTransactionsUseCase: FilterTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionListUiState>(TransactionListUiState.Loading)
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val _selectedPaymentType = MutableStateFlow<PaymentType?>(null)
    val selectedPaymentType: StateFlow<PaymentType?> = _selectedPaymentType.asStateFlow()

    private val _selectedStatus = MutableStateFlow<TransactionStatus?>(null)
    val selectedStatus: StateFlow<TransactionStatus?> = _selectedStatus.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMonthOffset = MutableStateFlow<Int?>(null)
    val selectedMonthOffset: StateFlow<Int?> = _selectedMonthOffset.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = TransactionListUiState.Loading
            val startMillis = calculateStartMillis()
            val endMillis = calculateEndMillis()

            filterTransactionsUseCase(
                paymentType = _selectedPaymentType.value,
                status = _selectedStatus.value,
                category = _selectedCategory.value,
                startMillis = startMillis,
                endMillis = endMillis
            ).catch { e ->
                _uiState.value = TransactionListUiState.Error(
                    message = e.message ?: "Erro ao carregar transacoes"
                )
            }.collect { transactions ->
                _uiState.value = TransactionListUiState.Success(transactions)
            }
        }
    }

    fun onPaymentTypeSelected(paymentType: PaymentType?) {
        _selectedPaymentType.value = if (_selectedPaymentType.value == paymentType) null else paymentType
        loadTransactions()
    }

    fun onStatusSelected(status: TransactionStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
        loadTransactions()
    }

    fun onCategorySelected(category: Category?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
        loadTransactions()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onMonthOffsetSelected(monthOffset: Int?) {
        _selectedMonthOffset.value = monthOffset
        loadTransactions()
    }

    private fun calculateStartMillis(): Long {
        val offset = _selectedMonthOffset.value ?: return 0L
        val start = LocalDate.now()
            .minusMonths(offset.toLong())
            .withDayOfMonth(1)
            .atStartOfDay()
        return start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun calculateEndMillis(): Long {
        val offset = _selectedMonthOffset.value ?: return Long.MAX_VALUE
        val end = LocalDate.now()
            .minusMonths(offset.toLong())
            .withDayOfMonth(1)
            .plusMonths(1)
            .atStartOfDay()
        return end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}

sealed interface TransactionListUiState {
    data object Loading : TransactionListUiState
    data class Success(val transactions: List<Transaction>) : TransactionListUiState
    data class Error(val message: String) : TransactionListUiState
}