package com.local.autobook.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class PaymentAccessibilityService : AccessibilityService() {
    // Phase 3 only registers the service and keeps behavior conservative.
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit
}
