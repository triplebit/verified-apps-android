package org.privacyguides.verifiedapps.data

import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.privacyguides.verifiedapps.Source

data class VerifyAppUiState(
    val name: MutableState<String> = mutableStateOf(""),
    val packageName: MutableState<String> = mutableStateOf(""),
    val hashes: MutableState<Hashes> = mutableStateOf(Hashes(listOf(Source.NONE), listOf(""), false)),
    val icon: MutableState<Drawable?> = mutableStateOf(null),
    val apkFailedToParse: MutableState<Boolean> = mutableStateOf(false),
    val isSystemApp: MutableState<Boolean> = mutableStateOf(false),
    val internalDatabaseInfo: MutableState<InternalDatabaseInfo> = mutableStateOf(
        InternalDatabaseInfo(
            InternalDatabaseStatus.NOT_FOUND,
            listOf(Source.NONE)
        )
    ),
    val searchQuery: MutableState<String> = mutableStateOf(""),
)


class InternalDatabaseInfo(
    val internalDatabaseStatus: InternalDatabaseStatus,
    val sources: List<Source>
)

enum class InternalDatabaseStatus {
    NOT_FOUND,
    MATCH,
    NOMATCH,
}

data class Hashes(
    val sources: List<Source>,
    val hashes: List<String>,
    val hasMultipleSigners: Boolean,
) {
    /**
     * Whether [other] describes the same signing configuration as this entry.
     * Fingerprint lists are compared as sets so order does not matter (e.g. certificate history).
     */
    fun matchesSigningFingerprints(other: Hashes): Boolean {
        if (hasMultipleSigners != other.hasMultipleSigners) {
            return false
        }
        return hashes.toSet() == other.hashes.toSet()
    }
}

data class VerificationInfo(val packageName: String, val hashes: Hashes)
