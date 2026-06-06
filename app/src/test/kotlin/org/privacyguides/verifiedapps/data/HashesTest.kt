package org.privacyguides.verifiedapps.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.privacyguides.verifiedapps.Source

class HashesTest {

    private fun hashes(fingerprints: List<String>, multipleSigners: Boolean = false) =
        Hashes(listOf(Source.NONE), fingerprints, multipleSigners)

    @Test
    fun matches_whenSameFingerprintSet_ignoringOrder() {
        assertTrue(
            hashes(listOf("A", "B")).matchesSigningFingerprints(hashes(listOf("B", "A"))),
        )
    }

    @Test
    fun doesNotMatch_whenFingerprintSetsDiffer() {
        assertFalse(
            hashes(listOf("A")).matchesSigningFingerprints(hashes(listOf("A", "B"))),
        )
    }

    @Test
    fun doesNotMatch_whenMultipleSignersFlagDiffers() {
        assertFalse(
            hashes(listOf("A"), multipleSigners = true)
                .matchesSigningFingerprints(hashes(listOf("A"), multipleSigners = false)),
        )
    }

    @Test
    fun matches_whenDuplicatesCollapseToSameSet() {
        assertTrue(
            hashes(listOf("A", "A")).matchesSigningFingerprints(hashes(listOf("A"))),
        )
    }

    @Test
    fun doesNotMatch_emptyAgainstNonEmpty() {
        assertFalse(
            hashes(emptyList()).matchesSigningFingerprints(hashes(listOf("A"))),
        )
    }
}
