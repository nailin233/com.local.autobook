package com.local.autobook.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class PaymentAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit
}
