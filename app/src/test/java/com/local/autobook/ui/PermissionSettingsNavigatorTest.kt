package com.local.autobook.ui

import com.local.autobook.ui.permission.PermissionSettingsNavigator
import kotlin.test.Test
import kotlin.test.assertEquals

class PermissionSettingsNavigatorTest {
    @Test
    fun accessibilityIntent_usesAccessibilitySettingsAction() {
        assertEquals(
            "android.settings.ACCESSIBILITY_SETTINGS",
            PermissionSettingsNavigator.accessibilitySettingsAction()
        )
    }

    @Test
    fun notificationIntent_usesNotificationListenerSettingsAction() {
        assertEquals(
            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS",
            PermissionSettingsNavigator.notificationListenerSettingsAction()
        )
    }
}
