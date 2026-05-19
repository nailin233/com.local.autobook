package com.local.autobook.detection

class WeChatPaymentDetector : PaymentDetector {
    override fun parse(text: String): DetectedPayment? {
        val amountCents = PaymentTextParser.extractAmountCents(text) ?: return null
        val normalized = PaymentTextParser.compactText(text)
        val direction = when {
            normalized.contains("退款") || normalized.contains("收款到账") || normalized.contains("转账收入") -> "INCOME"
            else -> "EXPENSE"
        }
        val merchant = PaymentTextParser.extractTrailingMerchant(
            normalized,
            listOf("收款方", "商户", "商家", "来自", "对方", "付款给")
        )
        return DetectedPayment(
            amountCents = amountCents,
            direction = direction,
            merchant = merchant,
            paymentSource = "WECHAT",
            detectedFrom = "WECHAT_TEXT",
            occurredAt = System.currentTimeMillis(),
            confidence = "HIGH",
            rawSummary = normalized
        )
    }
}
