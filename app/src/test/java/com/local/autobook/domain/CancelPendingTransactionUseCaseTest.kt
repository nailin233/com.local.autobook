package com.local.autobook.domain

import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.repository.PendingTransactionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CancelPendingTransactionUseCaseTest {
    @Test
    fun cancel_deletesPendingWithoutCreatingTransaction() = runTest {
        val pendingStore = FakeCancelPendingStore()
        val pending = PendingTransactionEntity(
            id = 9L,
            amountCents = 500L,
            direction = "INCOME",
            category = "退款",
            merchant = "支付宝",
            paymentSource = "ALIPAY",
            detectedFrom = "NOTIFICATION",
            occurredAt = 1710000000000L,
            confidence = "UNCLEAR",
            rawSummary = "退款到账 5元",
            dedupeKey = "key",
            createdAt = 1710000000100L
        )
        val useCase = CancelPendingTransactionUseCase(pendingStore)

        useCase.cancel(pending)

        assertEquals(listOf(pending), pendingStore.deleted)
    }
}

private class FakeCancelPendingStore : PendingTransactionStore {
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
