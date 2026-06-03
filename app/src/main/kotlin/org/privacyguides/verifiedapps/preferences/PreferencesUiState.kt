package org.privacyguides.verifiedapps.preferences

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

/** Preference pairs, the first is the preference key, and the second is the default value. */
data class PreferencesUiState(
    /** Whether to show hasMultipleSigners */
    val showHasMultipleSigners: Pair<Preferences.Key<Boolean>, MutableState<Boolean>> = Pair(
        (booleanPreferencesKey("SHOW_HAS_MULTIPLE_SIGNERS")),
        mutableStateOf(false)
    ),

    /** Whether to show share and copy verification info on the verify app screen. */
    val showSharingTools: Pair<Preferences.Key<Boolean>, MutableState<Boolean>> = Pair(
        (booleanPreferencesKey("SHOW_SHARING_TOOLS")),
        mutableStateOf(false)
    ),

    /** Whether to always show the GitHub submission button on the verify app screen. */
    val alwaysShowGitHubSubmit: Pair<Preferences.Key<Boolean>, MutableState<Boolean>> = Pair(
        (booleanPreferencesKey("ALWAYS_SHOW_GITHUB_SUBMIT")),
        mutableStateOf(false)
    ),

    /** Whether to include system apps in the app list. */
    val showSystemApps: Pair<Preferences.Key<Boolean>, MutableState<Boolean>> = Pair(
        (booleanPreferencesKey("SHOW_SYSTEM_APPS")),
        mutableStateOf(false)
    ),

    /** Use Material You dynamic color from the system wallpaper (Android 12+). */
    val dynamicColor: Pair<Preferences.Key<Boolean>, MutableState<Boolean>> = Pair(
        booleanPreferencesKey("DYNAMIC_COLOR"),
        mutableStateOf(false),
    ),

    /** Pitch black background. */
    val pitchBlackBackground: Pair<Preferences.Key<Boolean>, MutableState<Boolean>> = Pair(
        (booleanPreferencesKey("PITCH_BLACK_BACKGROUND")),
        mutableStateOf(false)
    ),
)