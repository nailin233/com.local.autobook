package com.local.autobook.repository

import com.local.autobook.data.PendingTransactionDao
import com.local.autobook.data.entity.PendingTransactionEntity
import kotlinx.coroutines.flow.Flow

class PendingTransactionRepository(
    private val pendingTransactionDao: PendingTransactionDao
) : PendingTransactionStore {
    override fun observePendingTransactions(): Flow<List<PendingTransactionEntity>> =
        pendingTransactionDao.observeAll()

    override suspend fun getById(id: Long): PendingTransactionEntity? =
        pendingTransactionDao.getById(id)

    override suspend fun findRecentByDedupeKey(
        dedupeKey: String,
        sinceMillis: Long
    ): PendingTransactionEntity? =
        pendingTransactionDao.findRecentByDedupeKey(dedupeKey, sinceMillis)

    override suspend fun insert(entity: PendingTransactionEntity): Long =
        pendingTransactionDao.insert(entity)

    override suspend fun update(entity: PendingTransactionEntity) =
        pendingTransactionDao.update(entity)

    override suspend fun delete(entity: PendingTransactionEntity) =
        pendingTransactionDao.delete(entity)
}
