package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentDeduplicatorTest {
    @Test
    fun buildDedupeKey_usesSourceAmountDirectionMerchantAndMinuteBucket() {
        val payment = DetectedPayment(
            amountCents = 1299L,
            direction = "EXPENSE",
            merchant = " Coffee Shop ",
            paymentSource = "WECHAT",
            detectedFrom = "ACCESSIBILITY",
            occurredAt = 1710000061000L,
            confidence = "CLEAR",
            rawSummary = "summary"
        )

        val key = PaymentDeduplicator().buildDedupeKey(payment)

        assertEquals("WECHAT|1299|EXPENSE|coffee shop|28500001", key)
    }

    @Test
    fun isDuplicate_returnsTrueInsideTwoMinutesAndFalseOutsideWindow() {
        val deduplicator = PaymentDeduplicator()

        assertTrue(deduplicator.isDuplicate("key", 1000L, "key", 119000L))
        assertFalse(deduplicator.isDuplicate("key", 1000L, "key", 122000L))
        assertFalse(deduplicator.isDuplicate("one", 1000L, "two", 119000L))
    }
}
