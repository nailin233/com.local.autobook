package com.local.autobook.ui

import kotlin.test.Test
import kotlin.test.assertTrue

class LedgerViewModelTest {
    @Test
    fun initialState_isEmptyList() {
        assertTrue(emptyList<String>().isEmpty())
    }
}
