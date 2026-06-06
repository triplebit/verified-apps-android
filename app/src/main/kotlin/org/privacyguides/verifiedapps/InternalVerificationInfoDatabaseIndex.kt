package org.privacyguides.verifiedapps

import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.data.VerificationInfo

/**
 * Package-name index over [internalVerificationInfoDatabase] for O(1) lookups.
 *
 * Keeping here rather than in InternalVerificationInfoDatabase.kt because that file is
 * regenerated wholesale by `.github/scripts/generate_internal_db.py` so should not carry
 * hand-written code. First entry wins on the (unexpected) chance of a duplicate package.
 */
internal val internalVerificationInfoDatabaseByPackage: Map<String, InternalDatabaseVerificationInfo> by lazy {
    buildMap {
        for (info in internalVerificationInfoDatabase) {
            putIfAbsent(info.packageName, info)
        }
    }
}

/**
 * Resolve a parsed app's [VerificationInfo] to its status against the bundled database.
 *
 * No Android dependencies so the security-critical match logic can be unit tested directly.
 * Matching is strict set-equality of fingerprints, so it can only ever produce a false mismatch,
 * never a false verification.
 */
internal fun internalDatabaseInfoFor(
    verificationInfo: VerificationInfo,
    byPackage: Map<String, InternalDatabaseVerificationInfo> = internalVerificationInfoDatabaseByPackage,
): InternalDatabaseInfo {
    val matchedInfo = byPackage[verificationInfo.packageName]
        ?: return InternalDatabaseInfo(InternalDatabaseStatus.NOT_FOUND, listOf(Source.NONE))

    val matchedHashes = matchedInfo.hashesList.find {
        it.matchesSigningFingerprints(verificationInfo.hashes)
    }
    return if (matchedHashes != null) {
        InternalDatabaseInfo(InternalDatabaseStatus.MATCH, matchedHashes.sources)
    } else {
        InternalDatabaseInfo(InternalDatabaseStatus.NOMATCH, listOf(Source.NONE))
    }
}
