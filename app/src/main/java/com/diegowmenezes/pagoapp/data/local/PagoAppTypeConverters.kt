package com.diegowmenezes.pagoapp.data.local

import androidx.room.TypeConverter
import com.diegowmenezes.pagoapp.domain.model.Category
import com.diegowmenezes.pagoapp.domain.model.PaymentType
import com.diegowmenezes.pagoapp.domain.model.PixKeyType
import com.diegowmenezes.pagoapp.domain.model.TransactionStatus

class PagoAppTypeConverters {

    @TypeConverter
    fun fromPaymentType(value: PaymentType): String {
        return value.name
    }

    @TypeConverter
    fun toPaymentType(value: String): PaymentType {
        return PaymentType.valueOf(value)
    }

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus {
        return TransactionStatus.valueOf(value)
    }

    @TypeConverter
    fun fromCategory(value: Category): String {
        return value.name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return Category.valueOf(value)
    }

    @TypeConverter
    fun fromPixKeyType(value: PixKeyType): String {
        return value.name
    }

    @TypeConverter
    fun toPixKeyType(value: String): PixKeyType {
        return PixKeyType.valueOf(value)
    }
}