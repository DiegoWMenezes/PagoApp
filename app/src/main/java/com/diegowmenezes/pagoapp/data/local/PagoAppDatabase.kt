package com.diegowmenezes.pagoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegowmenezes.pagoapp.data.local.dao.ContactDao
import com.diegowmenezes.pagoapp.data.local.dao.TransactionDao
import com.diegowmenezes.pagoapp.data.local.entity.ContactEntity
import com.diegowmenezes.pagoapp.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, ContactEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PagoAppTypeConverters::class)
abstract class PagoAppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun contactDao(): ContactDao

    class Callback : RoomDatabase.Callback() {

        override fun onCreate(connection: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(connection)
            val now = System.currentTimeMillis()
            val oneDayMillis = 86_400_000L

            val sampleTransactions = listOf(
                // PIX transactions
                TransactionEntity(
                    amountCents = -15000L,
                    paymentType = "PIX",
                    status = "CONCLUIDO",
                    recipientName = "Maria Silva",
                    recipientDocument = "12345678901",
                    description = "Divisao do jantar",
                    category = "ALIMENTACAO",
                    createdAt = now - oneDayMillis * 1,
                    pixKey = "12345678901",
                    pixKeyType = "CPF"
                ),
                TransactionEntity(
                    amountCents = 250000L,
                    paymentType = "PIX",
                    status = "CONCLUIDO",
                    recipientName = "Nubank",
                    recipientDocument = null,
                    description = "Recebimento salario",
                    category = "OUTROS",
                    createdAt = now - oneDayMillis * 2,
                    pixKey = "nubank@pagamento.com",
                    pixKeyType = "EMAIL"
                ),
                TransactionEntity(
                    amountCents = -8990L,
                    paymentType = "PIX",
                    status = "CONCLUIDO",
                    recipientName = "Farmacia RA",
                    recipientDocument = "98765432101",
                    description = "Medicamentos",
                    category = "SAUDE",
                    createdAt = now - oneDayMillis * 3,
                    pixKey = "11999887766",
                    pixKeyType = "TELEFONE"
                ),
                TransactionEntity(
                    amountCents = -45000L,
                    paymentType = "PIX",
                    status = "PENDENTE",
                    recipientName = "Joao Santos",
                    recipientDocument = "45678912300",
                    description = "Pagamento freelancer",
                    category = "OUTROS",
                    createdAt = now - oneDayMillis * 4,
                    pixKey = "abc123-def456",
                    pixKeyType = "ALEATORIA"
                ),

                // Cartao de credito transactions
                TransactionEntity(
                    amountCents = -18990L,
                    paymentType = "CARTAO_CREDITO",
                    status = "CONCLUIDO",
                    recipientName = "Mercado Livre",
                    recipientDocument = null,
                    description = "Fone de ouvido bluetooth",
                    category = "LAZER",
                    createdAt = now - oneDayMillis * 5,
                    installments = 3
                ),
                TransactionEntity(
                    amountCents = -35790L,
                    paymentType = "CARTAO_CREDITO",
                    status = "CONCLUIDO",
                    recipientName = "Supermercado Extra",
                    recipientDocument = null,
                    description = "Compras do mes",
                    category = "ALIMENTACAO",
                    createdAt = now - oneDayMillis * 6,
                    installments = 1
                ),
                TransactionEntity(
                    amountCents = -200000L,
                    paymentType = "CARTAO_CREDITO",
                    status = "CONCLUIDO",
                    recipientName = "Casas Bahia",
                    recipientDocument = null,
                    description = "Geladeira nova",
                    category = "MORADIA",
                    createdAt = now - oneDayMillis * 10,
                    installments = 10
                ),
                TransactionEntity(
                    amountCents = -15990L,
                    paymentType = "CARTAO_CREDITO",
                    status = "FALHOU",
                    recipientName = "Netflix",
                    recipientDocument = null,
                    description = "Assinatura mensal",
                    category = "LAZER",
                    createdAt = now - oneDayMillis * 7,
                    installments = 1
                ),

                // Cartao de debito transactions
                TransactionEntity(
                    amountCents = -6500L,
                    paymentType = "CARTAO_DEBITO",
                    status = "CONCLUIDO",
                    recipientName = "Padaria Pao Quente",
                    recipientDocument = null,
                    description = "Cafe e pao de queijo",
                    category = "ALIMENTACAO",
                    createdAt = now - oneDayMillis * 2
                ),
                TransactionEntity(
                    amountCents = -4500L,
                    paymentType = "CARTAO_DEBITO",
                    status = "CONCLUIDO",
                    recipientName = "Estacionamento Central",
                    recipientDocument = null,
                    description = "Estacionamento 2 horas",
                    category = "TRANSPORTE",
                    createdAt = now - oneDayMillis * 3
                ),
                TransactionEntity(
                    amountCents = -12000L,
                    paymentType = "CARTAO_DEBITO",
                    status = "CONCLUIDO",
                    recipientName = "Drogasil",
                    recipientDocument = null,
                    description = "Vitaminas e suplementos",
                    category = "SAUDE",
                    createdAt = now - oneDayMillis * 8
                ),

                // Boleto transactions
                TransactionEntity(
                    amountCents = -150000L,
                    paymentType = "BOLETO",
                    status = "CONCLUIDO",
                    recipientName = "Companhia de Eletricidade",
                    recipientDocument = "11222333000144",
                    description = "Conta de luz marco",
                    category = "MORADIA",
                    createdAt = now - oneDayMillis * 15
                ),
                TransactionEntity(
                    amountCents = -95000L,
                    paymentType = "BOLETO",
                    status = "CONCLUIDO",
                    recipientName = "Companhia de Gas",
                    recipientDocument = "55666777000188",
                    description = "Conta de gas abril",
                    category = "MORADIA",
                    createdAt = now - oneDayMillis * 12
                ),
                TransactionEntity(
                    amountCents = -50000L,
                    paymentType = "BOLETO",
                    status = "CANCELADO",
                    recipientName = "Academia Fit",
                    recipientDocument = null,
                    description = "Mensalidade academia",
                    category = "SAUDE",
                    createdAt = now - oneDayMillis * 20,
                    scheduledDate = now - oneDayMillis * 18
                ),
                TransactionEntity(
                    amountCents = -28000L,
                    paymentType = "BOLETO",
                    status = "CONCLUIDO",
                    recipientName = "Curso de Ingles",
                    recipientDocument = "99888777000166",
                    description = "Mensalidade curso",
                    category = "EDUCACAO",
                    createdAt = now - oneDayMillis * 25
                ),

                // TED transactions
                TransactionEntity(
                    amountCents = -200000L,
                    paymentType = "TED",
                    status = "CONCLUIDO",
                    recipientName = "Ana Oliveira",
                    recipientDocument = "78945612300",
                    description = "Emprestimo pessoal",
                    category = "OUTROS",
                    createdAt = now - oneDayMillis * 30,
                    bankCode = "341"
                ),
                TransactionEntity(
                    amountCents = -75000L,
                    paymentType = "TED",
                    status = "CONCLUIDO",
                    recipientName = "Condominio Residencial Park",
                    recipientDocument = "11222333000144",
                    description = "Condominio abril",
                    category = "MORADIA",
                    createdAt = now - oneDayMillis * 10,
                    bankCode = "104"
                ),
                TransactionEntity(
                    amountCents = 50000L,
                    paymentType = "TED",
                    status = "CONCLUIDO",
                    recipientName = "Carlos Souza",
                    recipientDocument = "32165498700",
                    description = "Reembolso jantar",
                    category = "ALIMENTACAO",
                    createdAt = now - oneDayMillis * 5,
                    bankCode = "260"
                ),

                // Additional varied transactions
                TransactionEntity(
                    amountCents = -8900L,
                    paymentType = "PIX",
                    status = "CONCLUIDO",
                    recipientName = "Uber",
                    recipientDocument = null,
                    description = "Corrida para o aeroporto",
                    category = "TRANSPORTE",
                    createdAt = now - oneDayMillis * 9,
                    pixKey = "uber@pix.com",
                    pixKeyType = "EMAIL"
                ),
                TransactionEntity(
                    amountCents = -120000L,
                    paymentType = "CARTAO_CREDITO",
                    status = "CONCLUIDO",
                    recipientName = "C&A",
                    recipientDocument = null,
                    description = "Roupas novas",
                    category = "OUTROS",
                    createdAt = now - oneDayMillis * 14,
                    installments = 2
                )
            )

            val sampleContacts = listOf(
                ContactEntity(
                    name = "Maria Silva",
                    document = "12345678901",
                    bankCode = "260",
                    bankName = "Nubank",
                    agency = "0001",
                    account = "123456-7",
                    pixKey = "12345678901",
                    pixKeyType = "CPF",
                    isFavorite = true,
                    createdAt = now
                ),
                ContactEntity(
                    name = "Joao Santos",
                    document = "45678912300",
                    bankCode = "341",
                    bankName = "Itau",
                    agency = "1234",
                    account = "98765-4",
                    pixKey = "joao.santos@email.com",
                    pixKeyType = "EMAIL",
                    isFavorite = true,
                    createdAt = now
                ),
                ContactEntity(
                    name = "Ana Oliveira",
                    document = "78945612300",
                    bankCode = "104",
                    bankName = "Caixa Economica",
                    agency = "5678",
                    account = "43210-1",
                    pixKey = "11988776655",
                    pixKeyType = "TELEFONE",
                    isFavorite = false,
                    createdAt = now
                ),
                ContactEntity(
                    name = "Carlos Souza",
                    document = "32165498700",
                    bankCode = "033",
                    bankName = "Santander",
                    agency = "9012",
                    account = "56789-0",
                    pixKey = "xyz789-abc012",
                    pixKeyType = "ALEATORIA",
                    isFavorite = false,
                    createdAt = now
                ),
                ContactEntity(
                    name = "Farmacia RA",
                    document = "98765432101",
                    bankCode = "260",
                    bankName = "Nubank",
                    agency = "0001",
                    account = "11111-2",
                    pixKey = "11999887766",
                    pixKeyType = "TELEFONE",
                    isFavorite = true,
                    createdAt = now
                )
            )

            // Insert sample data using raw SQL since the DAO is not available in the callback
            sampleTransactions.forEach { transaction ->
                connection.execSQL(
                    """
                    INSERT INTO transactions (amount_cents, payment_type, status, recipient_name, recipient_document,
                        description, category, created_at, scheduled_date, pix_key, pix_key_type, bank_code, installments)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    arrayOf(
                        transaction.amountCents.toString(),
                        transaction.paymentType,
                        transaction.status,
                        transaction.recipientName,
                        transaction.recipientDocument,
                        transaction.description,
                        transaction.category,
                        transaction.createdAt.toString(),
                        transaction.scheduledDate?.toString(),
                        transaction.pixKey,
                        transaction.pixKeyType,
                        transaction.bankCode,
                        transaction.installments?.toString()
                    )
                )
            }

            sampleContacts.forEach { contact ->
                connection.execSQL(
                    """
                    INSERT INTO contacts (name, document, bank_code, bank_name, agency, account,
                        pix_key, pix_key_type, is_favorite, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    arrayOf(
                        contact.name,
                        contact.document,
                        contact.bankCode,
                        contact.bankName,
                        contact.agency,
                        contact.account,
                        contact.pixKey,
                        contact.pixKeyType,
                        if (contact.isFavorite) "1" else "0",
                        contact.createdAt.toString()
                    )
                )
            }
        }
    }
}