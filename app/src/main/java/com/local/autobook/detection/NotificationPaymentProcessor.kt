package com.local.autobook.detection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.LinkedHashMap

class NotificationPaymentProcessor(
    private val createPending: suspend (DetectedPayment) -> Unit,
    private val detectors: List<PaymentDetector>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val recentSignatures = LinkedHashMap<String, Long>()
    private val duplicateWindowMillis = 5_000L

    fun process(text: String, source: String) {
        val normalized = text.trim()
        if (normalized.isBlank()) return

        scope.launch {
            detectors.asSequence()
                .mapNotNull { it.parse(normalized) }
                .firstOrNull()
                ?.let { detected ->
                    val candidate = detected.copy(detectedFrom = source, rawSummary = normalized)
                    if (accept(candidate)) {
                        createPending(candidate)
                    }
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

    private fun accept(payment: DetectedPayment): Boolean {
        val now = System.currentTimeMillis()
        val signature = payment.signature()
        synchronized(recentSignatures) {
            prune(now)
            val lastSeen = recentSignatures[signature]
            if (lastSeen != null && now - lastSeen < duplicateWindowMillis) {
                return false
            }
            recentSignatures[signature] = now
            return true
        }
    }

    private fun prune(now: Long) {
        val iterator = recentSignatures.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value >= duplicateWindowMillis) {
                iterator.remove()
            }
        }
    }

    private fun DetectedPayment.signature(): String =
        listOf(
            paymentSource,
            amountCents.toString(),
            direction,
            merchant.trim().lowercase()
        ).joinToString("|")
}
