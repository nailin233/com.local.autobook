package com.local.autobook.data

import com.local.autobook.data.entity.PendingTransactionEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class PendingTransactionEntityTest {
    @Test
    fun pendingTransactionEntity_exposesDesignFields() {
        val entity = PendingTransactionEntity(
            id = 0L,
            amountCents = 1299L,
            direction = "EXPENSE",
            category = "餐饮",
            merchant = "Coffee",
            paymentSource = "WECHAT",
            detectedFrom = "ACCESSIBILITY",
            occurredAt = 1710000000000L,
            confidence = "CLEAR",
            rawSummary = "微信支付 12.99",
            dedupeKey = "WECHAT|1299|EXPENSE|coffee|28500000",
            createdAt = 1710000000000L
        )

        assertEquals("WECHAT", entity.paymentSource)
        assertEquals("CLEAR", entity.confidence)
        assertEquals("WECHAT|1299|EXPENSE|coffee|28500000", entity.dedupeKey)
    }
}
