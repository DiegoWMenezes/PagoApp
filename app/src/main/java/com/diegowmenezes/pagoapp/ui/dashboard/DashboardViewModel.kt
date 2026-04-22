package com.diegowmenezes.pagoapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegowmenezes.pagoapp.domain.model.PaymentTypeSummary
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import com.diegowmenezes.pagoapp.domain.usecase.GetTransactionSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTransactionSummaryUseCase: GetTransactionSummaryUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var dataJob: kotlinx.coroutines.Job? = null

    init {
        loadData()
    }

    fun loadData() {
        dataJob?.cancel()
        dataJob = viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val summary = getTransactionSummaryUseCase()
                combine(
                    transactionRepository.getRecentTransactions(limit = 5),
                    transactionRepository.getSpendingByPaymentType()
                ) { recent, spending ->
                    DashboardData(recent, spending)
                }.catch { e ->
                    _uiState.value = DashboardUiState.Error(
                        message = e.message ?: "Erro ao carregar dados"
                    )
                }.collect { data ->
                    _uiState.value = DashboardUiState.Success(
                        balanceCents = summary.balanceCents,
                        totalIncomeCents = summary.totalIncomeCents,
                        totalExpenseCents = summary.totalExpenseCents,
                        totalTransactions = summary.totalTransactionCount,
                        recentTransactions = data.recentTransactions,
                        spendingByType = data.spendingByType
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    message = e.message ?: "Erro ao carregar dados"
                )
            }
        }
    }

    fun refresh() {
        dataJob?.cancel()
        dataJob = viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val summary = getTransactionSummaryUseCase()
                combine(
                    transactionRepository.getRecentTransactions(limit = 5),
                    transactionRepository.getSpendingByPaymentType()
                ) { recent, spending ->
                    DashboardData(recent, spending)
                }.catch { e ->
                    _uiState.value = DashboardUiState.Error(
                        message = e.message ?: "Erro ao carregar dados"
                    )
                }.collect { data ->
                    _uiState.value = DashboardUiState.Success(
                        balanceCents = summary.balanceCents,
                        totalIncomeCents = summary.totalIncomeCents,
                        totalExpenseCents = summary.totalExpenseCents,
                        totalTransactions = summary.totalTransactionCount,
                        recentTransactions = data.recentTransactions,
                        spendingByType = data.spendingByType
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    message = e.message ?: "Erro ao carregar dados"
                )
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private data class DashboardData(
        val recentTransactions: List<Transaction>,
        val spendingByType: List<PaymentTypeSummary>
    )
}

sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Success(
        val balanceCents: Long,
        val totalIncomeCents: Long,
        val totalExpenseCents: Long,
        val totalTransactions: Int,
        val recentTransactions: List<Transaction>,
        val spendingByType: List<PaymentTypeSummary>
    ) : DashboardUiState

    data class Error(val message: String) : DashboardUiState
}