package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertNull

class PaymentDetectorTest {
    @Test
    fun emptyText_returnsNull() {
        assertNull(WeChatPaymentDetector().parse(""))
    }
}
