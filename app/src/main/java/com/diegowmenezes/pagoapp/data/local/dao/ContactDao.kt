package com.diegowmenezes.pagoapp.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.diegowmenezes.pagoapp.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

data class ContactFrequencyResult(
    val id: Long,
    val name: String,
    val document: String,
    @ColumnInfo(name = "bank_code")
    val bankCode: String?,
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    val agency: String?,
    val account: String?,
    @ColumnInfo(name = "pix_key")
    val pixKey: String?,
    @ColumnInfo(name = "pix_key_type")
    val pixKeyType: String?,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    val transactionCount: Int
)

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): ContactEntity?

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchContacts(query: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE is_favorite = 1 ORDER BY name ASC")
    fun getFavoriteContacts(): Flow<List<ContactEntity>>

    @Query(
        """
        SELECT c.*,
               (SELECT COUNT(*) FROM transactions t WHERE t.recipient_name = c.name) AS transactionCount
        FROM contacts c
        ORDER BY transactionCount DESC, c.name ASC
        LIMIT :limit
        """
    )
    fun getFrequentContacts(limit: Int): Flow<List<ContactFrequencyResult>>

    @Insert
    suspend fun insert(contact: ContactEntity): Long

    @Update
    suspend fun update(contact: ContactEntity)

    @Delete
    suspend fun delete(contact: ContactEntity)
}