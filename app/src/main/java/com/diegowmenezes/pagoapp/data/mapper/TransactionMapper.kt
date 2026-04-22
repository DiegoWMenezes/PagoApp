package com.diegowmenezes.pagoapp.data.mapper

import com.diegowmenezes.pagoapp.data.local.entity.TransactionEntity
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.PixKeyType
import com.diegowmenezes.pagoapp.domain.model.Transaction
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amountCents = amountCents,
        paymentType = PaymentType.valueOf(paymentType),
        status = TransactionStatus.valueOf(status),
        recipientName = recipientName,
        recipientDocument = recipientDocument,
        description = description,
        category = Category.valueOf(category),
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneOffset.UTC
        ),
        scheduledDate = scheduledDate?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneOffset.UTC
            )
        },
        pixKey = pixKey,
        pixKeyType = pixKeyType?.let { PixKeyType.valueOf(it) },
        bankCode = bankCode,
        installments = installments
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amountCents = amountCents,
        paymentType = paymentType.name,
        status = status.name,
        recipientName = recipientName,
        recipientDocument = recipientDocument,
        description = description,
        category = category.name,
        createdAt = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
        scheduledDate = scheduledDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli(),
        pixKey = pixKey,
        pixKeyType = pixKeyType?.name,
        bankCode = bankCode,
        installments = installments
    )
}