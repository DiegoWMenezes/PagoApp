package com.diegowmenezes.pagoapp.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegowmenezes.pagoapp.domain.model.ReportsData
import com.diegowmenezes.pagoapp.domain.usecase.GetReportsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportsDataUseCase: GetReportsDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportsUiState>(ReportsUiState.Loading)
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        loadReports()
    }

    fun loadReports() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = ReportsUiState.Loading
            getReportsDataUseCase()
                .catch { e ->
                    _uiState.value = ReportsUiState.Error(
                        message = e.message ?: "Erro ao carregar relatorios"
                    )
                }
                .collect { reportsData ->
                    _uiState.value = ReportsUiState.Success(reportsData)
                }
        }
    }
}

sealed interface ReportsUiState {
    data object Loading : ReportsUiState
    data class Success(val data: ReportsData) : ReportsUiState
    data class Error(val message: String) : ReportsUiState
}