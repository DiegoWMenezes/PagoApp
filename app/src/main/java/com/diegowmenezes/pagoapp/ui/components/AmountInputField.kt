package com.diegowmenezes.pagoapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

@Composable
fun AmountInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val displayValue = if (value.isNotEmpty()) {
        val cents = value.toLongOrNull() ?: 0L
        val reais = cents / 100.0
        currencyFormat.format(reais)
    } else {
        currencyFormat.format(0.0)
    }

    OutlinedTextField(
        value = displayValue,
        onValueChange = { rawInput ->
            val digits = rawInput
                .filter { it.isDigit() }
                .take(14)
            onValueChange(digits)
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else {
            null
        },
        singleLine = true,
        prefix = { Text("R$ ") }
    )
}