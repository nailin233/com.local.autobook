package com.local.autobook.data.repository

import com.local.autobook.data.TransactionDao
import com.local.autobook.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun observeTransactions(): Flow<List<TransactionEntity>> = transactionDao.observeAll()

    suspend fun getById(id: Long): TransactionEntity? = transactionDao.getById(id)

    suspend fun insert(transaction: TransactionEntity): Long = transactionDao.insert(transaction)

    suspend fun update(transaction: TransactionEntity) = transactionDao.update(transaction)

    suspend fun delete(transaction: TransactionEntity) = transactionDao.delete(transaction)
}
