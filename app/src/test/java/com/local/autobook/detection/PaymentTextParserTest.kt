package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaymentTextParserTest {
    @Test
    fun extractAmount_recognizesCurrencyAndYuanFormats() {
        assertEquals(1234L, PaymentTextParser.extractAmountCents("¥12.34"))
        assertEquals(1234L, PaymentTextParser.extractAmountCents("￥12.34"))
        assertEquals(1234L, PaymentTextParser.extractAmountCents("12.34元"))
        assertEquals(1200L, PaymentTextParser.extractAmountCents("12元"))
    }

    @Test
    fun extractAmount_returnsNullWhenNoAmountExists() {
        assertNull(PaymentTextParser.extractAmountCents("支付成功"))
        assertNull(PaymentTextParser.extractAmountCents("Modex Agent(35条新消息)"))
    }
}
