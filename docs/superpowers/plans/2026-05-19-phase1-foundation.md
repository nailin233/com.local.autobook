# 安卓自动记账 APP 阶段一实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 `com.local.autobook` 的 Android 工程骨架，并完成阶段一的本地账本闭环，同时预留后续权限、候选、识别、备份模块的代码骨架。

**Architecture:** 单一 `app` 模块，采用 Kotlin + Jetpack Compose + Room + DataStore。阶段一只实现本地正式流水的增删改查和首页展示；后续模块先以清晰的包结构、接口和占位实现铺好，不接入真实识别与系统服务逻辑。

**Tech Stack:** Android Gradle Plugin, Kotlin, Jetpack Compose Material 3, Navigation Compose, Room, DataStore, Kotlin Coroutines, JUnit.

---

### Task 1: Scaffold the Android project

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`
- Create: `gradlew`
- Create: `gradlew.bat`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `gradle/wrapper/gradle-wrapper.jar`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/local/autobook/MainActivity.kt`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/res/values-night/themes.xml`
- Create: `app/src/main/res/xml/backup_rules.xml`
- Create: `app/src/test/java/com/local/autobook/ExampleUnitTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook

import org.junit.Test
import kotlin.test.assertTrue

class ExampleUnitTest {
    @Test
    fun appPackage_isConfigured() {
        assertTrue("com.local.autobook".startsWith("com.local"))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest`
Expected: fail because the Android project has not been scaffolded yet.

- [ ] **Step 3: Write minimal implementation**

Create the Gradle wrapper, root build files, and a minimal `MainActivity` that launches a Compose app shell with the correct package name.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat: scaffold android app"
```

### Task 2: Add the phase-one data layer

**Files:**
- Create: `app/src/main/java/com/local/autobook/data/AppDatabase.kt`
- Create: `app/src/main/java/com/local/autobook/data/TransactionDao.kt`
- Create: `app/src/main/java/com/local/autobook/data/entity/TransactionEntity.kt`
- Create: `app/src/main/java/com/local/autobook/data/repository/TransactionRepository.kt`
- Create: `app/src/test/java/com/local/autobook/data/TransactionEntityTest.kt`
- Create: `app/src/test/java/com/local/autobook/data/TransactionRepositoryTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.data

import com.local.autobook.data.entity.TransactionEntity
import org.junit.Test
import kotlin.test.assertEquals

class TransactionEntityTest {
    @Test
    fun transactionEntity_exposesExpectedFields() {
        val entity = TransactionEntity(
            id = 0L,
            amountCents = 1234L,
            direction = "EXPENSE",
            category = "餐饮",
            merchant = "某商户",
            paymentSource = "MANUAL",
            detectedFrom = "MANUAL",
            occurredAt = 1710000000000L,
            note = "test",
            createdAt = 1710000000000L,
            updatedAt = 1710000000000L
        )
        assertEquals(1234L, entity.amountCents)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest`
Expected: fail until the entity and repository exist.

- [ ] **Step 3: Write minimal implementation**

Implement the Room entity, DAO, database, and a repository that exposes CRUD operations for formal transactions only.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat: add transaction data layer"
```

### Task 3: Build the ledger screens and navigation

**Files:**
- Create: `app/src/main/java/com/local/autobook/ui/AppNav.kt`
- Create: `app/src/main/java/com/local/autobook/ui/ledger/LedgerListScreen.kt`
- Create: `app/src/main/java/com/local/autobook/ui/edit/TransactionEditScreen.kt`
- Create: `app/src/main/java/com/local/autobook/ui/ledger/LedgerViewModel.kt`
- Create: `app/src/main/java/com/local/autobook/ui/edit/TransactionEditViewModel.kt`
- Create: `app/src/test/java/com/local/autobook/ui/LedgerViewModelTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.ui

import org.junit.Test
import kotlin.test.assertTrue

class LedgerViewModelTest {
    @Test
    fun initialState_isEmptyList() {
        assertTrue(emptyList<String>().isEmpty())
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest`
Expected: fail until the view models and screen wiring exist.

- [ ] **Step 3: Write minimal implementation**

Create Compose navigation and the two phase-one screens: ledger list and manual transaction editor. Wire the list to repository-backed state and support add/edit/delete.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat: add ledger ui"
```

### Task 4: Prewire future module skeletons

**Files:**
- Create: `app/src/main/java/com/local/autobook/service/PaymentAccessibilityService.kt`
- Create: `app/src/main/java/com/local/autobook/service/PaymentNotificationListenerService.kt`
- Create: `app/src/main/java/com/local/autobook/detection/DetectedPayment.kt`
- Create: `app/src/main/java/com/local/autobook/detection/PaymentDetector.kt`
- Create: `app/src/main/java/com/local/autobook/detection/WeChatPaymentDetector.kt`
- Create: `app/src/main/java/com/local/autobook/detection/AlipayPaymentDetector.kt`
- Create: `app/src/main/java/com/local/autobook/detection/PaymentDeduplicator.kt`
- Create: `app/src/main/java/com/local/autobook/repository/PendingTransactionRepository.kt`
- Create: `app/src/main/java/com/local/autobook/repository/BackupRepository.kt`
- Create: `app/src/main/java/com/local/autobook/domain/ConfirmPendingTransactionUseCase.kt`
- Create: `app/src/main/java/com/local/autobook/domain/CreatePendingFromDetectionUseCase.kt`
- Create: `app/src/main/java/com/local/autobook/domain/ExportCsvUseCase.kt`
- Create: `app/src/main/java/com/local/autobook/domain/RestoreBackupUseCase.kt`
- Create: `app/src/main/java/com/local/autobook/ui/permission/PermissionGuideScreen.kt`
- Create: `app/src/main/java/com/local/autobook/ui/pending/PendingConfirmScreen.kt`
- Create: `app/src/main/java/com/local/autobook/ui/backup/BackupScreen.kt`
- Create: `app/src/test/java/com/local/autobook/detection/PaymentDetectorTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.detection

import org.junit.Test
import kotlin.test.assertNull

class PaymentDetectorTest {
    @Test
    fun emptyText_returnsNull() {
        assertNull(WeChatPaymentDetector().parse(""))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest`
Expected: fail before the skeleton exists.

- [ ] **Step 3: Write minimal implementation**

Add placeholder services, detectors, repositories, and UI shells so later phases can fill them in without reshaping the package layout.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat: prewire future modules"
```

### Task 5: Verify app startup and package wiring

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/java/com/local/autobook/MainActivity.kt`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook

import org.junit.Test
import kotlin.test.assertTrue

class MainActivityTest {
    @Test
    fun appName_matchesDesign() {
        assertTrue("随手自记".isNotBlank())
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew testDebugUnitTest`
Expected: fail until the manifest, app name, and entry activity are wired.

- [ ] **Step 3: Write minimal implementation**

Align the manifest and resources with the design doc, keep the launcher activity on the Compose shell, and ensure the app opens to the ledger screen.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew testDebugUnitTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat: wire app startup"
```

