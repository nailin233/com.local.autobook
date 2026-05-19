package com.local.autobook.detection

interface PaymentDetector {
    fun parse(text: String): DetectedPayment?
}
