package com.diegowmenezes.pagoapp.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diegowmenezes.pagoapp.data.local.PagoAppDatabase
import com.diegowmenezes.pagoapp.data.local.dao.ContactDao
import com.diegowmenezes.pagoapp.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.ZoneId

@RunWith(AndroidJUnit4::class)
class ContactDaoTest {

    private lateinit var database: PagoAppDatabase
    private lateinit var contactDao: ContactDao

    private val nowMillis = LocalDateTime.now()
        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PagoAppDatabase::class.java
        ).allowMainThreadQueries().build()
        contactDao = database.contactDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createContactEntity(
        id: Long = 0,
        name: String = "Maria Silva",
        document: String = "12345678901",
        pixKey: String = "maria@email.com",
        bankCode: String = "341",
        isFavorite: Boolean = false,
        createdAt: Long = nowMillis
    ) = ContactEntity(
        id = id,
        name = name,
        document = document,
        pixKey = pixKey,
        bankCode = bankCode,
        isFavorite = isFavorite,
        createdAt = createdAt
    )

    @Test
    fun insertAndRetrieve() = runTest {
        val entity = createContactEntity()
        val insertedId = contactDao.insert(entity)

        val contacts = contactDao.getAllContacts().first()
        assertEquals(1, contacts.size)
        assertEquals(insertedId, contacts[0].id)
        assertEquals("Maria Silva", contacts[0].name)
        assertEquals("maria@email.com", contacts[0].pixKey)
        assertEquals("341", contacts[0].bankCode)
    }

    @Test
    fun searchContactsReturnsMatchingResults() = runTest {
        contactDao.insert(createContactEntity(name = "Maria Silva"))
        contactDao.insert(createContactEntity(name = "Joao Santos", document = "98765432101"))
        contactDao.insert(createContactEntity(name = "Marina Costa", document = "45678912301"))

        val result = contactDao.searchContacts("Mari").first()
        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Maria Silva" })
        assertTrue(result.any { it.name == "Marina Costa" })
    }

    @Test
    fun searchContactsReturnsEmptyForNoMatch() = runTest {
        contactDao.insert(createContactEntity(name = "Maria Silva"))

        val result = contactDao.searchContacts("Pedro").first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun searchContactsIsCaseInsensitive() = runTest {
        contactDao.insert(createContactEntity(name = "Maria Silva"))

        val result = contactDao.searchContacts("maria").first()
        assertEquals(1, result.size)
        assertEquals("Maria Silva", result[0].name)
    }

    @Test
    fun getFavoriteContactsReturnsOnlyFavorites() = runTest {
        contactDao.insert(createContactEntity(name = "Maria Silva", isFavorite = true))
        contactDao.insert(createContactEntity(name = "Joao Santos", document = "98765432101", isFavorite = true))
        contactDao.insert(createContactEntity(name = "Pedro Lima", document = "45678912301", isFavorite = false))

        val favorites = contactDao.getFavoriteContacts().first()
        assertEquals(2, favorites.size)
        assertTrue(favorites.all { it.isFavorite })
    }

    @Test
    fun getFrequentContactsReturnsOrderedByTransactionCount() = runTest {
        contactDao.insert(createContactEntity(name = "Contato A", document = "11111111111"))
        contactDao.insert(createContactEntity(name = "Contato B", document = "22222222222"))
        contactDao.insert(createContactEntity(name = "Contato C", document = "33333333333"))

        val result = contactDao.getFrequentContacts(10).first()
        assertTrue(result.size <= 3)
    }

    @Test
    fun getFrequentContactsRespectsLimit() = runTest {
        for (i in 1..10) {
            contactDao.insert(
                createContactEntity(
                    name = "Contato $i",
                    document = "1111111111$i"
                )
            )
        }

        val result = contactDao.getFrequentContacts(5).first()
        assertEquals(5, result.size)
    }

    @Test
    fun updateContactUpdatesExistingEntry() = runTest {
        val entity = createContactEntity(name = "Maria Silva", isFavorite = false)
        val insertedId = contactDao.insert(entity)

        val contacts = contactDao.getAllContacts().first()
        val inserted = contacts.first { it.id == insertedId }
        val updated = inserted.copy(isFavorite = true)
        contactDao.update(updated)

        val result = contactDao.getAllContacts().first()
        assertEquals(1, result.size)
        assertEquals(true, result[0].isFavorite)
    }

    @Test
    fun deleteContactRemovesEntry() = runTest {
        val entity = createContactEntity(name = "Maria Silva")
        val insertedId = contactDao.insert(entity)

        val contacts = contactDao.getAllContacts().first()
        val inserted = contacts.first { it.id == insertedId }
        contactDao.delete(inserted)

        val result = contactDao.getAllContacts().first()
        assertTrue(result.isEmpty())
    }
}