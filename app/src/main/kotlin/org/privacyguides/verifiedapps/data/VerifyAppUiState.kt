package org.privacyguides.verifiedapps.data

import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import org.privacyguides.verifiedapps.Source

data class VerifyAppUiState(
    val name: MutableState<String> = mutableStateOf(""),
    val packageName: MutableState<String> = mutableStateOf(""),
    val hashes: MutableState<Hashes> = mutableStateOf(Hashes(listOf(Source.NONE), listOf(""), false)),
    val icon: MutableState<Drawable?> = mutableStateOf(null),
    val apkFailedToParse: MutableState<Boolean> = mutableStateOf(false),
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

enum class InternalDatabaseStatus(
    val info: String,
    val simpleInternalDatabaseStatus: SimpleInternalDatabaseStatus,
) {
    NOT_FOUND(
        "This app was not found in the internal database. You can submit its signing fingerprints for " +
                "review using the button below.",
        SimpleInternalDatabaseStatus.NOT_FOUND,
    ),
    MATCH(
        "This app's verification info matches an entry in the internal database. You don't need to verify normally.",
        SimpleInternalDatabaseStatus.SUCCESS,
    ),
    NOMATCH(
        "This app was found in the internal database, but its hash did NOT match. This app may be " +
                "non-genuine.",
        SimpleInternalDatabaseStatus.FAILURE,
    ),
}

enum class SimpleInternalDatabaseStatus(val color: Color) {
    NOT_FOUND(Color.Gray),
    SUCCESS(Color.Green),
    FAILURE(Color.Red)
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

sealed class SubmissionUiState {
    data object Idle : SubmissionUiState()
    data object Submitting : SubmissionUiState()
    data object Success : SubmissionUiState()
    data class Error(val message: String) : SubmissionUiState()
}
