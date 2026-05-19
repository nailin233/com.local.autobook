package com.local.autobook.data.repository

import com.local.autobook.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionStore {
    fun observeTransactions(): Flow<List<TransactionEntity>>

    suspend fun getById(id: Long): TransactionEntity?

    suspend fun insert(transaction: TransactionEntity): Long

    suspend fun update(transaction: TransactionEntity)

    suspend fun delete(transaction: TransactionEntity)
}
