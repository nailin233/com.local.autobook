package com.local.autobook.repository

import com.local.autobook.data.entity.PendingTransactionEntity
import kotlinx.coroutines.flow.Flow

interface PendingTransactionStore {
    fun observePendingTransactions(): Flow<List<PendingTransactionEntity>>

    suspend fun getById(id: Long): PendingTransactionEntity?

    suspend fun findRecentByDedupeKey(
        dedupeKey: String,
        sinceMillis: Long
    ): PendingTransactionEntity?

    suspend fun insert(entity: PendingTransactionEntity): Long

    suspend fun update(entity: PendingTransactionEntity)

    suspend fun delete(entity: PendingTransactionEntity)
}
