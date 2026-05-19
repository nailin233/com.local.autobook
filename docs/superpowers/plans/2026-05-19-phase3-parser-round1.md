# Payment Parser Round 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement pure-text WeChat and Alipay payment parsing for success, receipt, refund, amount extraction, and empty-result handling.

**Architecture:** Keep parsing entirely in the `detection` package. Shared text helpers live alongside the detectors, and each platform-specific detector returns `DetectedPayment?` without depending on Android services or repositories.

**Tech Stack:** Kotlin, JUnit, existing `DetectedPayment` model, existing `PaymentDetector` interface.

---

### Task 1: Shared Parsing Helpers

**Files:**
- Modify: `app/src/main/java/com/local/autobook/detection/PaymentDetector.kt`
- Create: `app/src/main/java/com/local/autobook/detection/PaymentTextParser.kt`
- Test: `app/src/test/java/com/local/autobook/detection/PaymentTextParserTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaymentTextParserTest {
    @Test
    fun extractAmount_recognizesCurrencyAndYuanFormats() {
        assertEquals(1234L, PaymentTextParser.extractAmountCents("¥12.34"))
        assertEquals(1234L, PaymentTextParser.extractAmountCents("￥12.34"))
        assertEquals(1234L, PaymentTextParser.extractAmountCents("12.34元"))
        assertEquals(1200L, PaymentTextParser.extractAmountCents("12元"))
    }

    @Test
    fun extractAmount_returnsNullWhenNoAmountExists() {
        assertNull(PaymentTextParser.extractAmountCents("支付成功"))
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: unresolved reference for `PaymentTextParser`.

- [ ] **Step 3: Implement the minimal shared parser**

Add amount extraction and tiny text helpers that later detectors can reuse.

- [ ] **Step 4: Run the test and confirm it passes**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: PASS.

### Task 2: WeChat Parser

**Files:**
- Modify: `app/src/main/java/com/local/autobook/detection/WeChatPaymentDetector.kt`
- Test: `app/src/test/java/com/local/autobook/detection/WeChatPaymentDetectorTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WeChatPaymentDetectorTest {
    @Test
    fun parse_paymentSuccess_returnsExpensePayment() {
        val result = WeChatPaymentDetector().parse("微信支付成功 12.34元 收款方 Coffee")
        assertEquals("EXPENSE", result?.direction)
        assertEquals(1234L, result?.amountCents)
        assertEquals("Coffee", result?.merchant)
        assertEquals("WECHAT", result?.paymentSource)
    }

    @Test
    fun parse_receiptAndRefund_cases_areHandled() {
        val receipt = WeChatPaymentDetector().parse("收款到账 5元 来自 Alice")
        val refund = WeChatPaymentDetector().parse("退款成功 8元")
        assertEquals("INCOME", receipt?.direction)
        assertEquals("INCOME", refund?.direction)
    }

    @Test
    fun parse_withoutAmount_returnsNull() {
        assertNull(WeChatPaymentDetector().parse("支付成功"))
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: failures because `parse()` still returns `null`.

- [ ] **Step 3: Implement the minimal detector**

Add a small platform parser that checks WeChat keywords, extracts amount via `PaymentTextParser`, derives direction, merchant, summary, and returns `DetectedPayment`.

- [ ] **Step 4: Run the test and confirm it passes**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: PASS.

### Task 3: Alipay Parser

**Files:**
- Modify: `app/src/main/java/com/local/autobook/detection/AlipayPaymentDetector.kt`
- Test: `app/src/test/java/com/local/autobook/detection/AlipayPaymentDetectorTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.detection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AlipayPaymentDetectorTest {
    @Test
    fun parse_paymentSuccess_returnsExpensePayment() {
        val result = AlipayPaymentDetector().parse("支付宝支付成功 12.34元 商家 Starbucks")
        assertEquals("EXPENSE", result?.direction)
        assertEquals(1234L, result?.amountCents)
        assertEquals("Starbucks", result?.merchant)
        assertEquals("ALIPAY", result?.paymentSource)
    }

    @Test
    fun parse_receiptAndRefund_cases_areHandled() {
        val receipt = AlipayPaymentDetector().parse("收款到账 5元 来自 Alice")
        val refund = AlipayPaymentDetector().parse("退款到账 8元")
        assertEquals("INCOME", receipt?.direction)
        assertEquals("INCOME", refund?.direction)
    }

    @Test
    fun parse_withoutAmount_returnsNull() {
        assertNull(AlipayPaymentDetector().parse("交易成功"))
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: failures because `parse()` still returns `null`.

- [ ] **Step 3: Implement the minimal detector**

Add Alipay-specific keyword handling, amount extraction, merchant fallback, and `DetectedPayment` output.

- [ ] **Step 4: Run the test and confirm it passes**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: PASS.

### Self-review notes

- Scope stays at pure text parsing only.
- No accessibility or notification code is touched.
- No repository or UI changes are needed for this round.
- Both detectors must return `null` when amount extraction fails.
