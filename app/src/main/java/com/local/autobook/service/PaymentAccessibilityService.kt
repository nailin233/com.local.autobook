package com.local.autobook.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.local.autobook.AutoBookApp

class PaymentAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val app = application as? AutoBookApp ?: return
        app.container.notificationPaymentProcessor.processAccessibilityEvent(
            eventTexts(event) + windowTexts(rootInActiveWindow)
        )
    }

    override fun onInterrupt() = Unit

    private fun eventTexts(event: AccessibilityEvent?): List<String?> =
        event?.text?.map { it?.toString() }.orEmpty()

    private fun windowTexts(root: AccessibilityNodeInfo?): List<String> {
        if (root == null) return emptyList()
        val results = mutableListOf<String>()
        collectNodeTexts(root, results)
        return results
    }

    private fun collectNodeTexts(node: AccessibilityNodeInfo, results: MutableList<String>) {
        node.text?.toString()?.takeIf { it.isNotBlank() }?.let(results::add)
        node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let(results::add)

        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            collectNodeTexts(child, results)
            child.recycle()
        }
    }
}
