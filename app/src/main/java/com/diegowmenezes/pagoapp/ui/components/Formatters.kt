package com.diegowmenezes.pagoapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.ui.graphics.vector.ImageVector
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import java.text.NumberFormat
import java.util.Locale

fun Long.formatCents(): String {
    val reais = this / 100.0
    val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return formatter.format(reais)
}

fun PaymentType.toImageVector(): ImageVector {
    return when (this) {
        PaymentType.PIX -> Icons.Filled.QrCode2
        PaymentType.CARTAO_CREDITO -> Icons.Filled.CreditCard
        PaymentType.CARTAO_DEBITO -> Icons.Filled.Payment
        PaymentType.BOLETO -> Icons.Filled.Description
        PaymentType.TED -> Icons.Filled.AccountBalance
    }
}

fun String.toCategoryLabel(): String {
    return try {
        Category.valueOf(this).label
    } catch (_: IllegalArgumentException) {
        this
    }
}

fun String.toPaymentTypeLabel(): String {
    return try {
        PaymentType.valueOf(this).label
    } catch (_: IllegalArgumentException) {
        this
    }
}

fun String.toCategory(): Category {
    return try {
        Category.valueOf(this)
    } catch (_: IllegalArgumentException) {
        Category.OUTROS
    }
}

fun String.toPaymentTypeImageVector(): ImageVector {
    return try {
        PaymentType.valueOf(this).toImageVector()
    } catch (_: IllegalArgumentException) {
        Icons.Filled.Payment
    }
}