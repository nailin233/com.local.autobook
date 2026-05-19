package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class NotificationPaymentProcessorTest {
    @Test
    fun processNotificationText_usesFirstParseableField() {
        val created = mutableListOf<DetectedPayment>()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val processor = NotificationPaymentProcessor(
            createPending = { created += it },
            detectors = listOf(WeChatPaymentDetector(), AlipayPaymentDetector()),
            scope = scope
        )

        processor.processNotificationText(
            title = "微信支付成功",
            text = "微信支付成功 12.34元 收款方 Coffee",
            bigText = null
        )

        assertEquals(1, created.size)
        assertEquals("NOTIFICATION", created.single().detectedFrom)
        assertEquals("Coffee", created.single().merchant)
    }

    @Test
    fun processAccessibilityText_usesFirstParseableEntry() {
        val created = mutableListOf<DetectedPayment>()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val processor = NotificationPaymentProcessor(
            createPending = { created += it },
            detectors = listOf(WeChatPaymentDetector(), AlipayPaymentDetector()),
            scope = scope
        )

        processor.processAccessibilityText(
            listOf("支付宝支付成功", "支付宝支付成功 12.34元 商家 Starbucks")
        )

        assertEquals(1, created.size)
        assertEquals("ACCESSIBILITY", created.single().detectedFrom)
        assertEquals("Starbucks", created.single().merchant)
    }
}
