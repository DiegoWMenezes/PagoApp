package com.diegowmenezes.pagoapp.data.mapper

import com.diegowmenezes.pagoapp.data.local.dao.ContactFrequencyResult
import com.diegowmenezes.pagoapp.data.local.entity.ContactEntity
import com.diegowmenezes.pagoapp.domain.model.Contact
import com.diegowmenezes.pagoapp.domain.model.ContactFrequency
import com.diegowmenezes.pagoapp.domain.model.PixKeyType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun ContactEntity.toDomain(): Contact {
    return Contact(
        id = id,
        name = name,
        document = document,
        bankCode = bankCode,
        bankName = bankName,
        agency = agency,
        account = account,
        pixKey = pixKey,
        pixKeyType = pixKeyType?.let { PixKeyType.valueOf(it) },
        isFavorite = isFavorite,
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt),
            ZoneOffset.UTC
        )
    )
}

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        name = name,
        document = document,
        bankCode = bankCode,
        bankName = bankName,
        agency = agency,
        account = account,
        pixKey = pixKey,
        pixKeyType = pixKeyType?.name,
        isFavorite = isFavorite,
        createdAt = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()
    )
}

fun ContactFrequencyResult.toDomain(): ContactFrequency {
    return ContactFrequency(
        contact = Contact(
            id = id,
            name = name,
            document = document,
            bankCode = bankCode,
            bankName = bankName,
            agency = agency,
            account = account,
            pixKey = pixKey,
            pixKeyType = pixKeyType?.let { PixKeyType.valueOf(it) },
            isFavorite = isFavorite,
            createdAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(createdAt),
                ZoneOffset.UTC
            )
        ),
        transactionCount = transactionCount
    )
}