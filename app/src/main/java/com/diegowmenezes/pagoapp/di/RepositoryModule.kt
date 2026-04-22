package com.diegowmenezes.pagoapp.di

import com.diegowmenezes.pagoapp.data.repository.ContactRepositoryImpl
import com.diegowmenezes.pagoapp.data.repository.TransactionRepositoryImpl
import com.diegowmenezes.pagoapp.domain.repository.ContactRepository
import com.diegowmenezes.pagoapp.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        impl: ContactRepositoryImpl
    ): ContactRepository
}