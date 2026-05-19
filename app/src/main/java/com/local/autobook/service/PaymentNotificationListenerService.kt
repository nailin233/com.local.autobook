package com.local.autobook.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class PaymentNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) = Unit
}
