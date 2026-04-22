package com.diegowmenezes.pagoapp.domain.model

import java.time.LocalDateTime

enum class PaymentType(val label: String, val icon: String) {
    PIX("Pix", "ic_pix"),
    CARTAO_CREDITO("Cartao de Credito", "ic_cartao_credito"),
    CARTAO_DEBITO("Cartao de Debito", "ic_cartao_debito"),
    BOLETO("Boleto", "ic_boleto"),
    TED("TED", "ic_ted")
}

enum class TransactionStatus(val label: String) {
    PENDENTE("Pendente"),
    CONCLUIDO("Concluido"),
    FALHOU("Falhou"),
    CANCELADO("Cancelado")
}

enum class Category(val label: String) {
    ALIMENTACAO("Alimentacao"),
    TRANSPORTE("Transporte"),
    SAUDE("Saude"),
    EDUCACAO("Educacao"),
    LAZER("Lazer"),
    MORADIA("Moradia"),
    OUTROS("Outros")
}

enum class PixKeyType(val label: String) {
    CPF("CPF"),
    EMAIL("E-mail"),
    TELEFONE("Telefone"),
    ALEATORIA("Chave Aleatoria")
}

data class Transaction(
    val id: Long = 0,
    val amountCents: Long,
    val paymentType: PaymentType,
    val status: TransactionStatus,
    val recipientName: String,
    val recipientDocument: String? = null,
    val description: String,
    val category: Category,
    val createdAt: LocalDateTime,
    val scheduledDate: LocalDateTime? = null,
    val pixKey: String? = null,
    val pixKeyType: PixKeyType? = null,
    val bankCode: String? = null,
    val installments: Int? = null
)