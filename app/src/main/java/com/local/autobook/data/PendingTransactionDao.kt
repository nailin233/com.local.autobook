package com.local.autobook.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.local.autobook.data.entity.PendingTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingTransactionDao {
    @Query("SELECT * FROM pending_transactions ORDER BY createdAt DESC, id DESC")
    fun observeAll(): Flow<List<PendingTransactionEntity>>

    @Query("SELECT * FROM pending_transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PendingTransactionEntity?

    @Query(
        """
        SELECT * FROM pending_transactions
        WHERE dedupeKey = :dedupeKey AND createdAt >= :sinceMillis
        ORDER BY createdAt DESC
        LIMIT 1
        """
    )
    suspend fun findRecentByDedupeKey(
        dedupeKey: String,
        sinceMillis: Long
    ): PendingTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PendingTransactionEntity): Long

    @Update
    suspend fun update(entity: PendingTransactionEntity)

    @Delete
    suspend fun delete(entity: PendingTransactionEntity)
}
