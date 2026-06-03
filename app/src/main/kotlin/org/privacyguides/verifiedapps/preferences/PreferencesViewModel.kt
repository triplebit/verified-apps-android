package org.privacyguides.verifiedapps.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PreferencesViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

    private val _preferencesLoaded = MutableStateFlow(false)
    val preferencesLoaded: StateFlow<Boolean> = _preferencesLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            populateSettingsFromDatastore()
        }
    }

    private suspend fun populateSettingsFromDatastore() {
        dataStore.data.collect { settings ->
            _uiState.update { state ->
                state.showHasMultipleSigners.second.value =
                    settings[state.showHasMultipleSigners.first] ?: false
                state.showSharingTools.second.value =
                    settings[state.showSharingTools.first] ?: false
                state.alwaysShowGitHubSubmit.second.value =
                    settings[state.alwaysShowGitHubSubmit.first] ?: false
                state.showSystemApps.second.value =
                    settings[state.showSystemApps.first] ?: false
                state.dynamicColor.second.value =
                    settings[state.dynamicColor.first] ?: false
                state.pitchBlackBackground.second.value =
                    settings[state.pitchBlackBackground.first] ?: false
                state
            }
            _preferencesLoaded.value = true
        }
    }

    suspend fun setPreference(key: Preferences.Key<Boolean>, value: Boolean) {
        updateLocalPreference(key, value)
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    private fun updateLocalPreference(key: Preferences.Key<Boolean>, value: Boolean) {
        _uiState.update { state ->
            when (key) {
                state.showHasMultipleSigners.first ->
                    state.showHasMultipleSigners.second.value = value
                state.showSharingTools.first ->
                    state.showSharingTools.second.value = value
                state.alwaysShowGitHubSubmit.first ->
                    state.alwaysShowGitHubSubmit.second.value = value
                state.showSystemApps.first ->
                    state.showSystemApps.second.value = value
                state.dynamicColor.first ->
                    state.dynamicColor.second.value = value
                state.pitchBlackBackground.first ->
                    state.pitchBlackBackground.second.value = value
            }
            state
        }
    }

    class PreferencesViewModelFactory(private val dataStore: DataStore<Preferences>) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PreferencesViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
    }
}
