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
    private var lastNotificationEventAt = 0L
    private val notificationPrimaryWindowMillis = 120_000L

    fun processNotificationEvent(notificationKey: String?, title: String?, text: String?, bigText: String?) {
        lastNotificationEventAt = System.currentTimeMillis()
        processFragments(
            source = "NOTIFICATION",
            fragments = sequenceOf(title, text, bigText).filterNotNull().toList()
        )
    }

    fun processAccessibilityEvent(texts: List<String?>) {
        val now = System.currentTimeMillis()
        if (now - lastNotificationEventAt < notificationPrimaryWindowMillis) return
        processFragments(
            source = "ACCESSIBILITY",
            fragments = texts
        )
    }

    private fun processFragments(source: String, fragments: List<String?>) {
        val normalizedFragments = fragments
            .asSequence()
            .filterNotNull()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toList()
        if (normalizedFragments.isEmpty()) return

        val rawSummary = normalizedFragments.joinToString(" ")

        scope.launch {
            detectors.asSequence()
                .mapNotNull { it.parse(rawSummary) }
                .firstOrNull()
                ?.let { detected ->
                    val candidate = detected.copy(detectedFrom = source, rawSummary = rawSummary)
                    if (accept(candidate.contentSignature())) {
                        createPending(candidate)
                    }
                }
        }
    }

    private fun accept(signature: String): Boolean {
        val now = System.currentTimeMillis()
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

    private fun DetectedPayment.contentSignature(): String =
        listOf(
            paymentSource,
            amountCents.toString(),
            direction,
            merchant.trim().lowercase()
        ).joinToString("|")
}
