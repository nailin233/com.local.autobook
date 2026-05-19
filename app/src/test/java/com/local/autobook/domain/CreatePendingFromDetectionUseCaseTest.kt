package com.local.autobook.domain

import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.detection.DetectedPayment
import com.local.autobook.detection.PaymentDeduplicator
import com.local.autobook.repository.PendingTransactionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CreatePendingFromDetectionUseCaseTest {
    @Test
    fun create_insertsPendingWhenNoRecentDuplicateExists() = runTest {
        val store = FakeCreatePendingStore()
        val useCase = CreatePendingFromDetectionUseCase(store, PaymentDeduplicator())

        val created = useCase.create(sampleDetected())

        assertEquals(1, store.inserted.size)
        assertEquals(store.inserted.single(), created)
        assertEquals("WECHAT|1299|EXPENSE|coffee|summary", created?.dedupeKey)
    }

    @Test
    fun create_returnsNullWhenRecentDuplicateExists() = runTest {
        val duplicate = PendingTransactionEntity(
            id = 1L,
            amountCents = 1299L,
            direction = "EXPENSE",
            category = "默认",
            merchant = "Coffee",
            paymentSource = "WECHAT",
            detectedFrom = "ACCESSIBILITY",
            occurredAt = 1710000000000L,
            confidence = "CLEAR",
            rawSummary = "summary",
            dedupeKey = "WECHAT|1299|EXPENSE|coffee|summary",
            createdAt = 1710000000000L
        )
        val store = FakeCreatePendingStore(recentDuplicate = duplicate)
        val useCase = CreatePendingFromDetectionUseCase(store, PaymentDeduplicator())

        val created = useCase.create(sampleDetected())

        assertNull(created)
        assertEquals(emptyList(), store.inserted)
    }
}

private fun sampleDetected() = DetectedPayment(
    amountCents = 1299L,
    direction = "EXPENSE",
    merchant = "Coffee",
    paymentSource = "WECHAT",
    detectedFrom = "ACCESSIBILITY",
    occurredAt = 1710000000000L,
    confidence = "CLEAR",
    rawSummary = "summary"
)

private class FakeCreatePendingStore(
    private val recentDuplicate: PendingTransactionEntity? = null
) : PendingTransactionStore {
    val inserted = mutableListOf<PendingTransactionEntity>()

    override fun observePendingTransactions(): Flow<List<PendingTransactionEntity>> = flowOf(inserted)

    override suspend fun getById(id: Long): PendingTransactionEntity? = null

    override suspend fun findRecentByDedupeKey(
        dedupeKey: String,
        sinceMillis: Long
    ): PendingTransactionEntity? = recentDuplicate?.takeIf { it.dedupeKey == dedupeKey }

    override suspend fun insert(entity: PendingTransactionEntity): Long {
        inserted += entity.copy(id = 1L)
        return 1L
    }

    override suspend fun update(entity: PendingTransactionEntity) = Unit

    override suspend fun delete(entity: PendingTransactionEntity) = Unit
}
