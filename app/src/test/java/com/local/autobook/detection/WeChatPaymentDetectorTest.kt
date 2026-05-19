package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WeChatPaymentDetectorTest {
    @Test
    fun parse_paymentSuccess_returnsExpensePayment() {
        val result = WeChatPaymentDetector().parse("微信支付成功 12.34元 收款方 Coffee")
        assertEquals("EXPENSE", result?.direction)
        assertEquals(1234L, result?.amountCents)
        assertEquals("Coffee", result?.merchant)
        assertEquals("WECHAT", result?.paymentSource)
    }

    @Test
    fun parse_receiptAndRefund_cases_areHandled() {
        val receipt = WeChatPaymentDetector().parse("收款到账 5元 来自 Alice")
        val refund = WeChatPaymentDetector().parse("退款成功 8元")
        assertEquals("INCOME", receipt?.direction)
        assertEquals("INCOME", refund?.direction)
    }

    @Test
    fun parse_withoutAmount_returnsNull() {
        assertNull(WeChatPaymentDetector().parse("支付成功"))
    }
}
