package com.local.autobook.detection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationPaymentProcessor(
    private val createPending: suspend (DetectedPayment) -> Unit,
    private val detectors: List<PaymentDetector>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    fun process(text: String, source: String) {
        val normalized = text.trim()
        if (normalized.isBlank()) return

        scope.launch {
            detectors.asSequence()
                .mapNotNull { it.parse(normalized) }
                .firstOrNull()
                ?.let { detected ->
                    createPending(detected.copy(detectedFrom = source, rawSummary = normalized))
                }
        }
    }

    fun processNotificationText(title: String?, text: String?, bigText: String?) {
        sequenceOf(title, text, bigText)
            .filterNotNull()
            .forEach { process(it, "NOTIFICATION") }
    }

    fun processAccessibilityText(texts: List<String?>) {
        texts.asSequence()
            .filterNotNull()
            .forEach { process(it, "ACCESSIBILITY") }
    }
}
