package com.local.autobook.domain

import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.repository.PendingTransactionStore

class CancelPendingTransactionUseCase(
    private val pendingStore: PendingTransactionStore
) {
    suspend fun cancel(pending: PendingTransactionEntity) {
        pendingStore.delete(pending)
    }
}
