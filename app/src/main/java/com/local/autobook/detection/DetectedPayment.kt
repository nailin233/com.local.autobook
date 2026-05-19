package com.local.autobook.detection

data class DetectedPayment(
    val amountCents: Long,
    val direction: String,
    val merchant: String,
    val paymentSource: String,
    val detectedFrom: String,
    val occurredAt: Long,
    val confidence: String,
    val rawSummary: String
)
