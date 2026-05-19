package com.local.autobook.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.local.autobook.AutoBookApp

class PaymentNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val app = application as? AutoBookApp ?: return
        val extras = sbn?.notification?.extras
        app.container.notificationPaymentProcessor.processNotificationText(
            title = extras?.getCharSequence("android.title")?.toString(),
            text = extras?.getCharSequence("android.text")?.toString(),
            bigText = extras?.getCharSequence("android.bigText")?.toString()
        )
    }
}
