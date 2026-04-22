package com.diegowmenezes.pagoapp.domain.usecase

import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Long> {
        if (transaction.amountCents == 0L) {
            return Result.failure(IllegalArgumentException("O valor da transacao deve ser diferente de zero."))
        }
        if (transaction.recipientName.isBlank()) {
            return Result.failure(IllegalArgumentException("O nome do beneficiario nao pode estar vazio."))
        }
        if (transaction.paymentType == PaymentType.PIX && transaction.pixKey.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Chave Pix e obrigatoria para pagamentos via Pix."))
        }
        return try {
            val id = repository.addTransaction(transaction)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}