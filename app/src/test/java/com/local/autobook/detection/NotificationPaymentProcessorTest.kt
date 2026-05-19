package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class NotificationPaymentProcessorTest {
    @Test
    fun processNotificationEvent_usesNotificationKeyAndFirstParseableFragment() {
        val created = mutableListOf<DetectedPayment>()
        val processor = NotificationPaymentProcessor(
            createPending = { created += it },
            detectors = listOf(WeChatPaymentDetector(), AlipayPaymentDetector()),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        )

        processor.processNotificationEvent(
            notificationKey = "notif-key-1",
            title = "微信支付成功",
            text = "微信支付成功 12.34元 收款方 Coffee",
            bigText = null
        )

        assertEquals(1, created.size)
        assertEquals("NOTIFICATION", created.single().detectedFrom)
        assertEquals("Coffee", created.single().merchant)
    }

    @Test
    fun processAccessibilityEvent_usesFirstParseableFragment() {
        val created = mutableListOf<DetectedPayment>()
        val processor = NotificationPaymentProcessor(
            createPending = { created += it },
            detectors = listOf(WeChatPaymentDetector(), AlipayPaymentDetector()),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        )

        processor.processAccessibilityEvent(
            listOf("支付宝支付成功", "支付宝支付成功 12.34元 商家 Starbucks")
        )

        assertEquals(1, created.size)
        assertEquals("ACCESSIBILITY", created.single().detectedFrom)
        assertEquals("Starbucks", created.single().merchant)
    }

    @Test
    fun duplicateSignature_betweenNotificationAndAccessibility_isSuppressed() {
        val created = mutableListOf<DetectedPayment>()
        val processor = NotificationPaymentProcessor(
            createPending = { created += it },
            detectors = listOf(WeChatPaymentDetector(), AlipayPaymentDetector()),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        )

        processor.processNotificationEvent(
            notificationKey = "notif-key-2",
            title = "微信支付成功",
            text = "微信支付成功88.90元收款方DailyShop",
            bigText = null
        )
        processor.processAccessibilityEvent(
            listOf("微信支付成功88.90元收款方DailyShop")
        )

        assertEquals(1, created.size)
    }

    @Test
    fun accessibility_isIgnoredWhenNotificationRecentlyFired() {
        val created = mutableListOf<DetectedPayment>()
        val processor = NotificationPaymentProcessor(
            createPending = { created += it },
            detectors = listOf(WeChatPaymentDetector(), AlipayPaymentDetector()),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        )

        processor.processNotificationEvent(
            notificationKey = "notif-key-3",
            title = "微信支付成功",
            text = "微信支付成功77.77元收款方FinalShop",
            bigText = null
        )
        processor.processAccessibilityEvent(
            listOf("微信支付成功77.77元收款方FinalShop")
        )

        assertEquals(1, created.size)
        assertEquals("NOTIFICATION", created.single().detectedFrom)
    }
}
