package org.privacyguides.verifiedapps.github

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppInstallSourceTest {

    @Test
    fun mapsKnownInstallerPackages() {
        assertEquals(
            AppInstallSource.APP_SOURCE_GOOGLE_PLAY,
            AppInstallSource.mapInstaller("com.android.vending"),
        )
        assertEquals(
            AppInstallSource.APP_SOURCE_F_DROID,
            AppInstallSource.mapInstaller("org.fdroid.fdroid"),
        )
        assertEquals(
            AppInstallSource.APP_SOURCE_ACCRESCENT,
            AppInstallSource.mapInstaller("app.accrescent.client"),
        )
    }

    @Test
    fun mapsBySubstringFallback() {
        assertEquals(
            AppInstallSource.APP_SOURCE_F_DROID,
            AppInstallSource.mapInstaller("org.fdroid.basic.fork"),
        )
        assertEquals(
            AppInstallSource.APP_SOURCE_CODEBERG,
            AppInstallSource.mapInstaller("org.codeberg.client"),
        )
    }

    @Test
    fun returnsNullForNullBlankOrUnknown() {
        assertNull(AppInstallSource.mapInstaller(null))
        assertNull(AppInstallSource.mapInstaller("   "))
        assertNull(AppInstallSource.mapInstaller("com.unknown.installer"))
    }
}
