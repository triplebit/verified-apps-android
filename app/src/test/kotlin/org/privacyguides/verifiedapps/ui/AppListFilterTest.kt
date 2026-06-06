package org.privacyguides.verifiedapps.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppListFilterTest {

    @Test
    fun defaultMask_selectsEveryFilter() {
        val mask = defaultStatusFilterMask()
        AppListFilter.entries.forEach { filter ->
            assertTrue(isStatusFilterSelected(mask, filter))
        }
    }

    @Test
    fun toggle_clearsThenRestoresSingleFilter() {
        val mask = defaultStatusFilterMask()

        val cleared = toggleStatusFilter(mask, AppListFilter.MISMATCH)
        assertFalse(isStatusFilterSelected(cleared, AppListFilter.MISMATCH))
        // Other filters are untouched.
        assertTrue(isStatusFilterSelected(cleared, AppListFilter.VERIFIED))
        assertTrue(isStatusFilterSelected(cleared, AppListFilter.NOT_IN_DATABASE))

        val restored = toggleStatusFilter(cleared, AppListFilter.MISMATCH)
        assertEquals(mask, restored)
    }

    @Test
    fun filterBits_areDistinct() {
        val bits = AppListFilter.entries.map { statusFilterBit(it) }
        assertEquals(bits.size, bits.toSet().size)
    }
}
