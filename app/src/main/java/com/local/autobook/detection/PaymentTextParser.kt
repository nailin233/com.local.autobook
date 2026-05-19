package com.local.autobook.detection

object PaymentTextParser {
    private val amountPattern = Regex("""(?:¥|￥)?\s*(\d+(?:\.\d{1,2})?)\s*元?|(?:¥|￥)\s*(\d+(?:\.\d{1,2})?)""")
    private val whitespacePattern = Regex("\\s+")

    fun extractAmountCents(text: String): Long? {
        val match = amountPattern.find(text) ?: return null
        val raw = match.groupValues.drop(1).firstOrNull { it.isNotBlank() }?.trim() ?: return null
        return raw.toAmountCents()
    }

    fun normalizeMerchant(text: String): String =
        text.trim().lowercase().replace(whitespacePattern, " ")

    fun compactText(text: String): String = text.trim().replace(whitespacePattern, " ")

    fun extractTrailingMerchant(text: String, markers: List<String>): String {
        val compact = compactText(text)
        for (marker in markers) {
            val index = compact.indexOf(marker)
            if (index >= 0) {
                val candidate = compact.substring(index + marker.length)
                    .trim()
                    .trimStart(':', '：', '-', '—', '－', ' ')
                if (candidate.isNotBlank()) return candidate
            }
        }
        return ""
    }

    private fun String.toAmountCents(): Long? {
        val parts = split(".")
        val yuan = parts.firstOrNull()?.toLongOrNull() ?: return null
        val cents = parts.getOrNull(1)
            ?.padEnd(2, '0')
            ?.take(2)
            ?.toLongOrNull()
            ?: 0L
        return yuan * 100 + cents
    }
}
