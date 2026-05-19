package com.local.autobook.domain

import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.data.entity.TransactionEntity
import com.local.autobook.data.repository.TransactionStore
import com.local.autobook.repository.PendingTransactionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfirmPendingTransactionUseCaseTest {
    @Test
    fun confirm_convertsPendingToFormalTransactionAndDeletesPending() = runTest {
        val transactionStore = FakeTransactionStore()
        val pendingStore = FakePendingStore()
        val pending = samplePending()
        val useCase = ConfirmPendingTransactionUseCase(
            transactionStore = transactionStore,
            pendingStore = pendingStore
        )

        useCase.confirm(pending)

        assertEquals(1, transactionStore.inserted.size)
        val inserted = transactionStore.inserted.single()
        assertEquals(pending.amountCents, inserted.amountCents)
        assertEquals(pending.direction, inserted.direction)
        assertEquals(pending.category, inserted.category)
        assertEquals(pending.merchant, inserted.merchant)
        assertEquals(pending.paymentSource, inserted.paymentSource)
        assertEquals(pending.detectedFrom, inserted.detectedFrom)
        assertEquals(pending.occurredAt, inserted.occurredAt)
        assertEquals(pending.rawSummary, inserted.note)
        assertEquals(listOf(pending), pendingStore.deleted)
    }
}

private fun samplePending() = PendingTransactionEntity(
    id = 7L,
    amountCents = 1299L,
    direction = "EXPENSE",
    category = "餐饮",
    merchant = "Coffee",
    paymentSource = "WECHAT",
    detectedFrom = "ACCESSIBILITY",
    occurredAt = 1710000000000L,
    confidence = "CLEAR",
    rawSummary = "微信支付 12.99",
    dedupeKey = "key",
    createdAt = 1710000000100L
)

private class FakeTransactionStore : TransactionStore {
    val inserted = mutableListOf<TransactionEntity>()

    override fun observeTransactions(): Flow<List<TransactionEntity>> = flowOf(inserted)

    override suspend fun getById(id: Long): TransactionEntity? =
        inserted.firstOrNull { it.id == id }

    override suspend fun insert(transaction: TransactionEntity): Long {
        inserted += transaction
        return inserted.size.toLong()
    }

    override suspend fun update(transaction: TransactionEntity) = Unit

    override suspend fun delete(transaction: TransactionEntity) {
        inserted.remove(transaction)
    }
}

private class FakePendingStore : PendingTransactionStore {
    val deleted = mutableListOf<PendingTransactionEntity>()

    override fun observePendingTransactions(): Flow<List<PendingTransactionEntity>> = flowOf(emptyList())

    override suspend fun getById(id: Long): PendingTransactionEntity? = null

    override suspend fun findRecentByDedupeKey(
        dedupeKey: String,
        sinceMillis: Long
    ): PendingTransactionEntity? = null

    override suspend fun insert(entity: PendingTransactionEntity): Long = 0L

    override suspend fun update(entity: PendingTransactionEntity) = Unit

    override suspend fun delete(entity: PendingTransactionEntity) {
        deleted += entity
    }
}
