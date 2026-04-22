package com.diegowmenezes.pagoapp.domain.repository

import com.diegowmenezes.pagoapp.domain.model.Contact
import com.diegowmenezes.pagoapp.domain.model.ContactFrequency
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    fun getContacts(): Flow<List<Contact>>

    fun searchContacts(query: String): Flow<List<Contact>>

    fun getFavoriteContacts(): Flow<List<Contact>>

    fun getFrequentContacts(limit: Int): Flow<List<ContactFrequency>>

    suspend fun addContact(contact: Contact): Long

    suspend fun updateContact(contact: Contact)

    suspend fun deleteContact(contact: Contact)
}