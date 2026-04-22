package com.diegowmenezes.pagoapp.di

import android.content.Context
import androidx.room.Room
import com.diegowmenezes.pagoapp.data.local.PagoAppDatabase
import com.diegowmenezes.pagoapp.data.local.dao.ContactDao
import com.diegowmenezes.pagoapp.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PagoAppDatabase {
        return Room.databaseBuilder(
            context,
            PagoAppDatabase::class.java,
            "pagoapp_database"
        )
            .addCallback(PagoAppDatabase.Callback())
            .build()
    }

    @Provides
    fun provideTransactionDao(database: PagoAppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideContactDao(database: PagoAppDatabase): ContactDao {
        return database.contactDao()
    }
}