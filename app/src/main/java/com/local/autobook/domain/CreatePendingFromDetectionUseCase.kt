package com.local.autobook.domain

import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.detection.DetectedPayment
import com.local.autobook.detection.PaymentDeduplicator
import com.local.autobook.repository.PendingTransactionStore

class CreatePendingFromDetectionUseCase(
    private val pendingStore: PendingTransactionStore,
    private val deduplicator: PaymentDeduplicator
) {
    suspend fun create(detectedPayment: DetectedPayment): PendingTransactionEntity? {
        val now = System.currentTimeMillis()
        val dedupeKey = deduplicator.buildDedupeKey(detectedPayment)
        val duplicate = pendingStore.findRecentByDedupeKey(
            dedupeKey = dedupeKey,
            sinceMillis = now - PaymentDeduplicator.DUPLICATE_WINDOW_MILLIS
        )
        if (duplicate != null) return null

        val pending = PendingTransactionEntity(
            amountCents = detectedPayment.amountCents,
            direction = detectedPayment.direction,
            category = defaultCategoryFor(detectedPayment),
            merchant = detectedPayment.merchant.ifBlank { defaultMerchantFor(detectedPayment) },
            paymentSource = detectedPayment.paymentSource,
            detectedFrom = detectedPayment.detectedFrom,
            occurredAt = detectedPayment.occurredAt,
            confidence = detectedPayment.confidence,
            rawSummary = detectedPayment.rawSummary,
            dedupeKey = dedupeKey,
            createdAt = now
        )
        val id = pendingStore.insert(pending)
        return pending.copy(id = id)
    }

    private fun defaultCategoryFor(payment: DetectedPayment): String =
        if (payment.rawSummary.contains("\u9000\u6b3e")) "\u9000\u6b3e" else "\u9ed8\u8ba4"

    private fun defaultMerchantFor(payment: DetectedPayment): String =
        when (payment.paymentSource) {
            "WECHAT" -> "\u5fae\u4fe1\u652f\u4ed8"
            "ALIPAY" -> "\u652f\u4ed8\u5b9d"
            else -> "\u672a\u586b\u5199"
        }
}
