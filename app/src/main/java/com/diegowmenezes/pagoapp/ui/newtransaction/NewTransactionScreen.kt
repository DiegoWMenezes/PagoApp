package com.diegowmenezes.pagoapp.ui.newtransaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.Contact
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.ui.components.AmountInputField
import com.diegowmenezes.pagoapp.ui.components.PaymentTypeChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val contactSuggestions by viewModel.contactSuggestions.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is NewTransactionUiState.Success) {
            snackbarHostState.showSnackbar("Transacao salva com sucesso!")
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Transacao") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        modifier = Modifier
                            .clickable(onClick = onNavigateBack)
                            .padding(8.dp)
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (uiState) {
            is NewTransactionUiState.Saving -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NewTransactionUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as NewTransactionUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.saveTransaction() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            else -> {
                val form = (uiState as? NewTransactionUiState.Form) ?: NewTransactionUiState.Form()
                NewTransactionForm(
                    form = form,
                    contactSuggestions = contactSuggestions,
                    innerPadding = innerPadding,
                    onAmountChanged = viewModel::onAmountChanged,
                    onPaymentTypeSelected = viewModel::onPaymentTypeSelected,
                    onRecipientNameChanged = viewModel::onRecipientNameChanged,
                    onDescriptionChanged = viewModel::onDescriptionChanged,
                    onCategorySelected = viewModel::onCategorySelected,
                    onPixKeyChanged = viewModel::onPixKeyChanged,
                    onBankCodeChanged = viewModel::onBankCodeChanged,
                    onInstallmentsChanged = viewModel::onInstallmentsChanged,
                    onContactSuggestionSelected = viewModel::onContactSuggestionSelected,
                    onContactSuggestionsDismissed = viewModel::onContactSuggestionsDismissed,
                    onSaveClicked = viewModel::saveTransaction
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTransactionForm(
    form: NewTransactionUiState.Form,
    contactSuggestions: List<Contact>,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onAmountChanged: (String) -> Unit,
    onPaymentTypeSelected: (PaymentType) -> Unit,
    onRecipientNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onPixKeyChanged: (String) -> Unit,
    onBankCodeChanged: (String) -> Unit,
    onInstallmentsChanged: (String) -> Unit,
    onContactSuggestionSelected: (Contact) -> Unit,
    onContactSuggestionsDismissed: () -> Unit,
    onSaveClicked: () -> Unit
) {
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Valor",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        AmountInputField(
            value = form.amountCentsRaw,
            onValueChange = onAmountChanged,
            label = "Valor da transacao",
            isError = form.amountError != null,
            errorMessage = form.amountError,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Tipo de Pagamento",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentType.entries.forEach { paymentType ->
                PaymentTypeChip(
                    paymentType = paymentType,
                    isSelected = form.paymentType == paymentType,
                    onClick = { onPaymentTypeSelected(paymentType) }
                )
            }
        }

        Text(
            text = "Destinatario",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Box {
            OutlinedTextField(
                value = form.recipientName,
                onValueChange = onRecipientNameChanged,
                label = { Text("Nome do destinatario") },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "Destinatario")
                },
                modifier = Modifier.fillMaxWidth(),
                isError = form.recipientNameError != null,
                supportingText = form.recipientNameError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                singleLine = true
            )
            if (contactSuggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp)
                ) {
                    contactSuggestions.forEach { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onContactSuggestionSelected(contact)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = contact.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

        OutlinedTextField(
            value = form.description,
            onValueChange = onDescriptionChanged,
            label = { Text("Descricao (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = categoryMenuExpanded,
            onExpandedChange = { categoryMenuExpanded = it }
        ) {
            OutlinedTextField(
                value = form.category.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { categoryMenuExpanded = false }
            ) {
                Category.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.label) },
                        onClick = {
                            onCategorySelected(category)
                            categoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        if (form.paymentType == PaymentType.PIX) {
            OutlinedTextField(
                value = form.pixKey,
                onValueChange = onPixKeyChanged,
                label = { Text("Chave Pix") },
                modifier = Modifier.fillMaxWidth(),
                isError = form.pixKeyError != null,
                supportingText = form.pixKeyError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                singleLine = true
            )
        }

        if (form.paymentType == PaymentType.TED) {
            OutlinedTextField(
                value = form.bankCode,
                onValueChange = onBankCodeChanged,
                label = { Text("Codigo do banco") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        if (form.paymentType == PaymentType.CARTAO_CREDITO) {
            OutlinedTextField(
                value = form.installments,
                onValueChange = onInstallmentsChanged,
                label = { Text("Numero de parcelas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        val isFormValid = form.amountCentsRaw.toLongOrNull()?.let { it > 0 } == true
            && form.recipientName.isNotBlank()
            && (form.paymentType != PaymentType.PIX || form.pixKey.isNotBlank())

        Button(
            onClick = onSaveClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = isFormValid
        ) {
            Text("Salvar Transacao")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}