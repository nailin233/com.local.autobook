package com.local.autobook.ui

import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.data.entity.TransactionEntity
import com.local.autobook.data.repository.TransactionStore
import com.local.autobook.repository.PendingTransactionStore
import com.local.autobook.ui.pending.PendingConfirmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PendingConfirmViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun confirm_removesPendingAndCreatesFormalTransaction() = runTest(dispatcher) {
        val pending = samplePending()
        val pendingStore = FakePendingStoreForViewModel(listOf(pending))
        val transactionStore = FakeTransactionStoreForViewModel()
        val viewModel = PendingConfirmViewModel(
            pendingStore = pendingStore,
            transactionStore = transactionStore
        )

        viewModel.confirm(pending)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(emptyList(), pendingStore.items.value)
        assertEquals(1, transactionStore.inserted.size)
    }
}

private fun samplePending() = PendingTransactionEntity(
    id = 1L,
    amountCents = 1200L,
    direction = "EXPENSE",
    category = "默认",
    merchant = "Coffee",
    paymentSource = "WECHAT",
    detectedFrom = "ACCESSIBILITY",
    occurredAt = 1710000000000L,
    confidence = "CLEAR",
    rawSummary = "summary",
    dedupeKey = "key",
    createdAt = 1710000000100L
)

private class FakePendingStoreForViewModel(
    initialItems: List<PendingTransactionEntity>
) : PendingTransactionStore {
    val items = MutableStateFlow(initialItems)

    override fun observePendingTransactions(): Flow<List<PendingTransactionEntity>> = items

    override suspend fun getById(id: Long): PendingTransactionEntity? =
        items.value.firstOrNull { it.id == id }

    override suspend fun findRecentByDedupeKey(
        dedupeKey: String,
        sinceMillis: Long
    ): PendingTransactionEntity? = null

    override suspend fun insert(entity: PendingTransactionEntity): Long {
        items.value = items.value + entity
        return entity.id
    }

    override suspend fun update(entity: PendingTransactionEntity) = Unit

    override suspend fun delete(entity: PendingTransactionEntity) {
        items.value = items.value.filterNot { it.id == entity.id }
    }
}

private class FakeTransactionStoreForViewModel : TransactionStore {
    val inserted = mutableListOf<TransactionEntity>()

    override fun observeTransactions(): Flow<List<TransactionEntity>> =
        MutableStateFlow(inserted)

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
