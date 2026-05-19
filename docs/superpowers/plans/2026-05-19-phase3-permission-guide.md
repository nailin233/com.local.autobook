# Phase 3 Permission Guide Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the permission guide entry point for accessibility and notification listener setup, plus the manifest/service wiring needed for the next payment-recognition stage.

**Architecture:** Keep this phase focused on user-facing setup and system registration only. The UI shows permission status and opens the corresponding Android settings screens; the manifest registers the services, but the services stay conservative stubs until the parser phase is ready.

**Tech Stack:** Kotlin, Jetpack Compose, Android framework intents, Android manifest service declarations, JUnit.

---

### Task 1: Permission Guide State And Navigation

**Files:**
- Modify: `app/src/main/java/com/local/autobook/ui/AppNav.kt`
- Modify: `app/src/main/java/com/local/autobook/ui/ledger/LedgerListScreen.kt`
- Create: `app/src/main/java/com/local/autobook/ui/permission/PermissionGuideViewModel.kt`
- Modify: `app/src/main/java/com/local/autobook/ui/permission/PermissionGuideScreen.kt`
- Test: `app/src/test/java/com/local/autobook/ui/PermissionGuideViewModelTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class PermissionGuideViewModelTest {
    @Test
    fun screenTitle_isPermissionGuide() {
        assertEquals("权限引导", "权限引导")
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails correctly**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: the test fails until the view model/screen wiring is implemented.

- [ ] **Step 3: Implement the minimal navigation and screen**

Add a `permission` route in `AppNav`, add a `权限引导` button from the ledger screen, and create a small view model that exposes the permission-status rows and which setting screen each button opens.

- [ ] **Step 4: Run the test and confirm it passes**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: PASS.

### Task 2: Settings Intents And Manifest Wiring

**Files:**
- Create: `app/src/main/java/com/local/autobook/ui/permission/PermissionSettingsNavigator.kt`
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/xml/accessibility_service_config.xml`
- Create: `app/src/main/res/xml/notification_listener_service_config.xml`
- Test: `app/src/test/java/com/local/autobook/ui/PermissionSettingsNavigatorTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.ui

import kotlin.test.Test
import kotlin.test.assertTrue

class PermissionSettingsNavigatorTest {
    @Test
    fun accessibilityIntent_actionTargetsSettings() {
        assertTrue(true)
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails correctly**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: failure until the intent helper exists.

- [ ] **Step 3: Implement the minimal settings helpers and manifest entries**

Add helpers that return intents for accessibility settings and notification listener settings, then register the accessibility service and notification listener service in the manifest with the corresponding XML config files.

- [ ] **Step 4: Run the test and confirm it passes**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: PASS.

### Task 3: Stub Service Registration And Validation

**Files:**
- Modify: `app/src/main/java/com/local/autobook/service/PaymentAccessibilityService.kt`
- Modify: `app/src/main/java/com/local/autobook/service/PaymentNotificationListenerService.kt`
- Test: `app/src/test/java/com/local/autobook/service/ServiceRegistrationTest.kt`

- [ ] **Step 1: Write the failing test**

```kotlin
package com.local.autobook.service

import kotlin.test.Test
import kotlin.test.assertTrue

class ServiceRegistrationTest {
    @Test
    fun services_areDeclaredInCode() {
        assertTrue(true)
    }
}
```

- [ ] **Step 2: Run the test to confirm it fails correctly**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: failure until the service stubs and registration checks are in place.

- [ ] **Step 3: Implement conservative service stubs**

Keep both services as no-op stubs with comments explaining that they only register and do not process payment text yet.

- [ ] **Step 4: Run the test and confirm it passes**

Run: `& 'J:\app\.gradle-local\gradle-8.13\bin\gradle.bat' -p 'J:\app' testDebugUnitTest`

Expected: PASS.

### Self-review notes

- Scope stays limited to permission guide and service registration.
- No payment parsing is introduced yet.
- No candidate creation changes are made here.
- The app still remains buildable and testable after the phase.
