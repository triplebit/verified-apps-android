package org.privacyguides.verifiedapps.ui

import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.MessageDigest

class HexFingerprintTest {

    @Test
    fun formatsSha256AsUpperColonHex() {
        // Known vector: SHA-256 of the empty input.
        val digest = MessageDigest.getInstance("SHA-256").digest(ByteArray(0))
        val expected =
            "E3:B0:C4:42:98:FC:1C:14:9A:FB:F4:C8:99:6F:B9:24:" +
                "27:AE:41:E4:64:9B:93:4C:A4:95:99:1B:78:52:B8:55"
        assertEquals(expected, digest.toUpperColonHex())
    }

    @Test
    fun zeroPadsSingleDigitBytes() {
        assertEquals("00:0F:A0", byteArrayOf(0x00, 0x0F, 0xA0.toByte()).toUpperColonHex())
    }
}
