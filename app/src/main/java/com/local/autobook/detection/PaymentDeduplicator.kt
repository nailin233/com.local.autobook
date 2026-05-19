package com.local.autobook.detection

class PaymentDeduplicator {
    fun buildDedupeKey(payment: DetectedPayment): String {
        return listOf(
            payment.paymentSource,
            payment.amountCents.toString(),
            payment.direction,
            payment.merchant.normalizeMerchant(),
            (payment.occurredAt / MINUTE_MILLIS).toString()
        ).joinToString("|")
    }

    fun isDuplicate(
        existingDedupeKey: String,
        existingCreatedAt: Long,
        candidateDedupeKey: String,
        candidateCreatedAt: Long,
        windowMillis: Long = DUPLICATE_WINDOW_MILLIS
    ): Boolean {
        return existingDedupeKey == candidateDedupeKey &&
            kotlin.math.abs(candidateCreatedAt - existingCreatedAt) < windowMillis
    }

    private fun String.normalizeMerchant(): String =
        trim().lowercase().replace(Regex("\\s+"), " ")

    companion object {
        const val MINUTE_MILLIS = 60_000L
        const val DUPLICATE_WINDOW_MILLIS = 120_000L
    }
}
