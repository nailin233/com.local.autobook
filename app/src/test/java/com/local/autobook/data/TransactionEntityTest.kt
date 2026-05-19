package com.local.autobook.data

import com.local.autobook.data.entity.TransactionEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionEntityTest {
    @Test
    fun transactionEntity_exposesExpectedFields() {
        val entity = TransactionEntity(
            id = 0L,
            amountCents = 1234L,
            direction = "EXPENSE",
            category = "餐饮",
            merchant = "某商户",
            paymentSource = "MANUAL",
            detectedFrom = "MANUAL",
            occurredAt = 1710000000000L,
            note = "test",
            createdAt = 1710000000000L,
            updatedAt = 1710000000000L
        )
        assertEquals(1234L, entity.amountCents)
    }
}
