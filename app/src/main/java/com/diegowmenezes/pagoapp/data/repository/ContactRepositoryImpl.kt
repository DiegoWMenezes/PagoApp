package com.diegowmenezes.pagoapp.data.repository

import com.diegowmenezes.pagoapp.data.local.dao.ContactDao
import com.diegowmenezes.pagoapp.data.mapper.toDomain
import com.diegowmenezes.pagoapp.data.mapper.toEntity
import com.diegowmenezes.pagoapp.domain.model.Contact
import com.diegowmenezes.pagoapp.domain.model.ContactFrequency
import com.diegowmenezes.pagoapp.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
) : ContactRepository {

    override fun getContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteContacts(): Flow<List<Contact>> {
        return contactDao.getFavoriteContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFrequentContacts(limit: Int): Flow<List<ContactFrequency>> {
        return contactDao.getFrequentContacts(limit).map { results ->
            results.map { it.toDomain() }
        }
    }

    override suspend fun addContact(contact: Contact): Long {
        return contactDao.insert(contact.toEntity())
    }

    override suspend fun updateContact(contact: Contact) {
        contactDao.update(contact.toEntity())
    }

    override suspend fun deleteContact(contact: Contact) {
        contactDao.delete(contact.toEntity())
    }
}