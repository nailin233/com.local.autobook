package com.local.autobook.domain

import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.data.entity.TransactionEntity
import com.local.autobook.data.repository.TransactionStore
import com.local.autobook.repository.PendingTransactionStore

class ConfirmPendingTransactionUseCase(
    private val transactionStore: TransactionStore,
    private val pendingStore: PendingTransactionStore
) {
    suspend fun confirm(pending: PendingTransactionEntity): Long {
        val now = System.currentTimeMillis()
        val transactionId = transactionStore.insert(
            TransactionEntity(
                amountCents = pending.amountCents,
                direction = pending.direction,
                category = pending.category,
                merchant = pending.merchant.ifBlank { "\u672a\u586b\u5199" },
                paymentSource = pending.paymentSource,
                detectedFrom = pending.detectedFrom,
                occurredAt = pending.occurredAt,
                note = pending.rawSummary,
                createdAt = now,
                updatedAt = now
            )
        )
        pendingStore.delete(pending)
        return transactionId
    }
}
