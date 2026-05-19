# Phase 2 Pending Transactions Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the pending transaction workflow: detected candidates are stored separately, users can confirm them into formal transactions or cancel them, and duplicate candidates are filtered.

**Architecture:** Extend the existing single Android app module. Room stores pending candidates beside formal transactions; small domain use cases coordinate repository actions; Compose adds a pending list/confirm screen that can be exercised manually before real payment detection is connected.

**Tech Stack:** Kotlin, Jetpack Compose, Room, Coroutines, JUnit, existing Gradle/Android stack.

---

### Task 1: Pending Transaction Data Model

**Files:**
- Create: `app/src/main/java/com/local/autobook/data/entity/PendingTransactionEntity.kt`
- Create: `app/src/main/java/com/local/autobook/data/PendingTransactionDao.kt`
- Modify: `app/src/main/java/com/local/autobook/data/AppDatabase.kt`
- Modify: `app/src/main/java/com/local/autobook/repository/PendingTransactionRepository.kt`
- Test: `app/src/test/java/com/local/autobook/data/PendingTransactionEntityTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.data

import com.local.autobook.data.entity.PendingTransactionEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class PendingTransactionEntityTest {
    @Test
    fun pendingTransactionEntity_exposesDesignFields() {
        val entity = PendingTransactionEntity(
            id = 0L,
            amountCents = 1299L,
            direction = "EXPENSE",
            category = "餐饮",
            merchant = "Coffee",
            paymentSource = "WECHAT",
            detectedFrom = "ACCESSIBILITY",
            occurredAt = 1710000000000L,
            confidence = "CLEAR",
            rawSummary = "微信支付 12.99",
            dedupeKey = "WECHAT|1299|EXPENSE|coffee|28500000",
            createdAt = 1710000000000L
        )

        assertEquals("WECHAT", entity.paymentSource)
        assertEquals("CLEAR", entity.confidence)
        assertEquals("WECHAT|1299|EXPENSE|coffee|28500000", entity.dedupeKey)
    }
}
```

- [ ] **Step 2: Run the test and confirm it fails**

Run: `.\gradlew testDebugUnitTest`

Expected: `Unresolved reference 'PendingTransactionEntity'`.

- [ ] **Step 3: Implement the data model**

Create the Room entity and DAO with `observeAll()`, `getById(id)`, `findRecentByDedupeKey(dedupeKey, sinceMillis)`, `insert(entity)`, and `delete(entity)`. Update `AppDatabase` to include `PendingTransactionEntity` and bump `version` to `2` with destructive migration allowed for the internal prototype.

- [ ] **Step 4: Run the test and confirm it passes**

Run: `.\gradlew testDebugUnitTest`

Expected: PASS.

### Task 2: Confirm And Cancel Use Cases

**Files:**
- Modify: `app/src/main/java/com/local/autobook/domain/ConfirmPendingTransactionUseCase.kt`
- Create: `app/src/main/java/com/local/autobook/domain/CancelPendingTransactionUseCase.kt`
- Test: `app/src/test/java/com/local/autobook/domain/ConfirmPendingTransactionUseCaseTest.kt`
- Test: `app/src/test/java/com/local/autobook/domain/CancelPendingTransactionUseCaseTest.kt`

- [ ] **Step 1: Write the failing tests**

Tests should prove confirmation converts pending data into `TransactionEntity` with the same amount, direction, category, merchant, source, detection source, time, and summary note, then deletes the pending row. Cancellation should delete only the pending row and create no formal transaction.

- [ ] **Step 2: Run tests and confirm they fail**

Run: `.\gradlew testDebugUnitTest`

Expected: failures because use cases are empty or missing.

- [ ] **Step 3: Implement the use cases**

`ConfirmPendingTransactionUseCase.confirm(pending)` inserts a formal transaction and then deletes the candidate. `CancelPendingTransactionUseCase.cancel(pending)` deletes the candidate only.

- [ ] **Step 4: Run tests and confirm they pass**

Run: `.\gradlew testDebugUnitTest`

Expected: PASS.

### Task 3: Candidate Creation And Deduplication

**Files:**
- Modify: `app/src/main/java/com/local/autobook/detection/PaymentDeduplicator.kt`
- Modify: `app/src/main/java/com/local/autobook/domain/CreatePendingFromDetectionUseCase.kt`
- Test: `app/src/test/java/com/local/autobook/detection/PaymentDeduplicatorTest.kt`
- Test: `app/src/test/java/com/local/autobook/domain/CreatePendingFromDetectionUseCaseTest.kt`

- [ ] **Step 1: Write the failing tests**

Tests should prove the dedupe key is `paymentSource + amountCents + direction + normalizedMerchant + minuteBucket(occurredAt)`, duplicates inside two minutes are rejected, and a non-duplicate detection creates one pending transaction.

- [ ] **Step 2: Run tests and confirm they fail**

Run: `.\gradlew testDebugUnitTest`

Expected: failures because dedupe logic is not implemented.

- [ ] **Step 3: Implement minimal dedupe and creation logic**

Use a small `PaymentDeduplicator` with `buildDedupeKey(payment)` and `isDuplicate(existing, candidate, windowMillis = 120000L)`. `CreatePendingFromDetectionUseCase.create(detected)` creates `PendingTransactionEntity` only when recent pending data with the same dedupe key does not exist.

- [ ] **Step 4: Run tests and confirm they pass**

Run: `.\gradlew testDebugUnitTest`

Expected: PASS.

### Task 4: Pending Confirmation UI

**Files:**
- Modify: `app/src/main/java/com/local/autobook/data/AppContainer.kt`
- Modify: `app/src/main/java/com/local/autobook/ui/AppNav.kt`
- Modify: `app/src/main/java/com/local/autobook/ui/pending/PendingConfirmScreen.kt`
- Create: `app/src/main/java/com/local/autobook/ui/pending/PendingConfirmViewModel.kt`
- Test: `app/src/test/java/com/local/autobook/ui/PendingConfirmViewModelTest.kt`

- [ ] **Step 1: Write the failing test**

Test should instantiate the view model with a fake pending repository and prove confirm removes pending data and exposes an empty state.

- [ ] **Step 2: Run test and confirm it fails**

Run: `.\gradlew testDebugUnitTest`

Expected: failure because the view model is missing.

- [ ] **Step 3: Implement the UI**

Wire `PendingTransactionRepository` into `AppContainer`; add navigation route `pending`; show a pending list with CLEAR/UNCLEAR, amount, merchant, category, confirm and cancel buttons. Keep editing simple in phase two: confirmation uses the candidate fields as stored.

- [ ] **Step 4: Run tests and assemble**

Run: `.\gradlew testDebugUnitTest assembleDebug`

Expected: PASS.

### Self-review notes

- Scope is limited to phase two. No real WeChat/Alipay Accessibility or notification parsing is implemented here.
- Candidate fields match `docs/detailed-design-v1.md`.
- Confirmation never writes a formal transaction without user action.
- Cancellation deletes pending data only.
- Deduplication is limited to pending candidates in this phase; formal-transaction duplicate checks can be strengthened when detection is wired in phase three.
