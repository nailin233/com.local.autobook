package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AlipayPaymentDetectorTest {
    @Test
    fun parse_paymentSuccess_returnsExpensePayment() {
        val result = AlipayPaymentDetector().parse("支付宝支付成功 12.34元 商家 Starbucks")
        assertEquals("EXPENSE", result?.direction)
        assertEquals(1234L, result?.amountCents)
        assertEquals("Starbucks", result?.merchant)
        assertEquals("ALIPAY", result?.paymentSource)
    }

    @Test
    fun parse_receiptAndRefund_cases_areHandled() {
        val receipt = AlipayPaymentDetector().parse("收款到账 5元 来自 Alice")
        val refund = AlipayPaymentDetector().parse("退款到账 8元")
        assertEquals("INCOME", receipt?.direction)
        assertEquals("INCOME", refund?.direction)
    }

    @Test
    fun parse_withoutAmount_returnsNull() {
        assertNull(AlipayPaymentDetector().parse("交易成功"))
    }
}
