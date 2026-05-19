package com.local.autobook.data.repository

import com.local.autobook.data.TransactionDao
import com.local.autobook.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) : TransactionStore {
    override fun observeTransactions(): Flow<List<TransactionEntity>> = transactionDao.observeAll()

    override suspend fun getById(id: Long): TransactionEntity? = transactionDao.getById(id)

    override suspend fun insert(transaction: TransactionEntity): Long = transactionDao.insert(transaction)

    override suspend fun update(transaction: TransactionEntity) = transactionDao.update(transaction)

    override suspend fun delete(transaction: TransactionEntity) = transactionDao.delete(transaction)
}
