package com.local.autobook.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class PaymentNotificationListenerService : NotificationListenerService() {
    // Phase 3 only registers the service and keeps behavior conservative.
    override fun onNotificationPosted(sbn: StatusBarNotification?) = Unit
}
