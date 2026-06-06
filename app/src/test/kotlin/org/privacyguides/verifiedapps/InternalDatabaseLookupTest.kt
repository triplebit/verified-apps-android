package org.privacyguides.verifiedapps

import org.junit.Assert.assertEquals
import org.junit.Test
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.data.VerificationInfo

class InternalDatabaseLookupTest {

    private val fingerprint = "AA:BB:CC"
    private val entry = InternalDatabaseVerificationInfo(
        "com.example.app",
        listOf(Hashes(listOf(Source.GITHUB), listOf(fingerprint), false)),
    )
    private val byPackage = mapOf(entry.packageName to entry)

    private fun verification(packageName: String, fingerprints: List<String>) =
        VerificationInfo(packageName, Hashes(listOf(Source.NONE), fingerprints, false))

    @Test
    fun notFound_whenPackageAbsent() {
        val result = internalDatabaseInfoFor(verification("com.other", listOf(fingerprint)), byPackage)
        assertEquals(InternalDatabaseStatus.NOT_FOUND, result.internalDatabaseStatus)
    }

    @Test
    fun match_whenFingerprintsEqual() {
        val result = internalDatabaseInfoFor(verification("com.example.app", listOf(fingerprint)), byPackage)
        assertEquals(InternalDatabaseStatus.MATCH, result.internalDatabaseStatus)
        assertEquals(listOf(Source.GITHUB), result.sources)
    }

    @Test
    fun noMatch_whenPackagePresentButFingerprintDiffers() {
        val result = internalDatabaseInfoFor(verification("com.example.app", listOf("DD:EE:FF")), byPackage)
        assertEquals(InternalDatabaseStatus.NOMATCH, result.internalDatabaseStatus)
    }
}
