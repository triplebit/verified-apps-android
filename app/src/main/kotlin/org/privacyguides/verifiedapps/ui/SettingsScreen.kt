package org.privacyguides.verifiedapps.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesViewModel: PreferencesViewModel,
) {
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val dynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    var showCodebergSubmitDialog by remember { mutableStateOf(false) }

    fun setShowCodebergSubmit(enabled: Boolean) {
        coroutineScope.launch {
            preferencesViewModel.setPreference(
                preferencesUiState.showCodebergSubmit.first,
                enabled,
            )
        }
    }

    if (showCodebergSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showCodebergSubmitDialog = false },
            title = { Text(stringResource(R.string.show_codeberg_submit_dialog_title)) },
            text = { Text(stringResource(R.string.show_codeberg_submit_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCodebergSubmitDialog = false
                        setShowCodebergSubmit(true)
                    },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCodebergSubmitDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.theme),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(ListItemDefaults.ContentPadding),
                    style = MaterialTheme.typography.labelLarge,
                )
                ListItem(
                    modifier = Modifier.toggleable(
                        value = preferencesUiState.dynamicColor.second.value,
                        enabled = dynamicColorAvailable,
                        onValueChange = {
                            coroutineScope.launch {
                                preferencesViewModel.setPreference(
                                    preferencesUiState.dynamicColor.first,
                                    it,
                                )
                            }
                        },
                    ),
                    headlineContent = {
                        Text(stringResource(R.string.dynamic_color_setting_name))
                    },
                    supportingContent = {
                        Text(
                            stringResource(
                                if (dynamicColorAvailable) {
                                    R.string.dynamic_color_setting_description
                                } else {
                                    R.string.dynamic_color_setting_unavailable_description
                                },
                            ),
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = preferencesUiState.dynamicColor.second.value,
                            onCheckedChange = null,
                            enabled = dynamicColorAvailable,
                        )
                    },
                )
                ListItem(
                    modifier = Modifier.toggleable(
                        value = preferencesUiState.pitchBlackBackground.second.value,
                        onValueChange = {
                            coroutineScope.launch {
                                preferencesViewModel.setPreference(
                                    preferencesUiState.pitchBlackBackground.first,
                                    it,
                                )
                            }
                        },
                    ),
                    headlineContent = {
                        Text(stringResource(R.string.pitch_black_background_setting_name))
                    },
                    supportingContent = {
                        Text(stringResource(R.string.pitch_black_background_setting_description))
                    },
                    trailingContent = {
                        Switch(
                            checked = preferencesUiState.pitchBlackBackground.second.value,
                            onCheckedChange = null,
                        )
                    },
                )
            }

            Column {
                Text(
                    text = stringResource(R.string.advanced),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(ListItemDefaults.ContentPadding),
                    style = MaterialTheme.typography.labelLarge,
                )
                ListItem(
                    modifier = Modifier.toggleable(
                        value = preferencesUiState.showSystemApps.second.value,
                        onValueChange = {
                            coroutineScope.launch {
                                preferencesViewModel.setPreference(
                                    preferencesUiState.showSystemApps.first,
                                    it,
                                )
                            }
                        },
                    ),
                    headlineContent = { Text(stringResource(R.string.show_system_apps_setting_name)) },
                    supportingContent = { Text(stringResource(R.string.show_system_apps_setting_description)) },
                    trailingContent = {
                        Switch(
                            checked = preferencesUiState.showSystemApps.second.value,
                            onCheckedChange = null,
                        )
                    },
                )
                ListItem(
                    modifier = Modifier.toggleable(
                        value = preferencesUiState.showHasMultipleSigners.second.value,
                        onValueChange = {
                            coroutineScope.launch {
                                preferencesViewModel.setPreference(
                                    preferencesUiState.showHasMultipleSigners.first,
                                    it,
                                )
                            }
                        },
                    ),
                    headlineContent = { Text(stringResource(R.string.show_hasmultiplesigners_setting_name)) },
                    supportingContent = { Text(stringResource(R.string.show_hasmultiplesigners_setting_description)) },
                    trailingContent = {
                        Switch(
                            checked = preferencesUiState.showHasMultipleSigners.second.value,
                            onCheckedChange = null,
                        )
                    },
                )
                ListItem(
                    modifier = Modifier.toggleable(
                        value = preferencesUiState.showSharingTools.second.value,
                        onValueChange = {
                            coroutineScope.launch {
                                preferencesViewModel.setPreference(
                                    preferencesUiState.showSharingTools.first,
                                    it,
                                )
                            }
                        },
                    ),
                    headlineContent = { Text(stringResource(R.string.show_sharing_tools_setting_name)) },
                    supportingContent = { Text(stringResource(R.string.show_sharing_tools_setting_description)) },
                    trailingContent = {
                        Switch(
                            checked = preferencesUiState.showSharingTools.second.value,
                            onCheckedChange = null,
                        )
                    },
                )
                ListItem(
                    modifier = Modifier.toggleable(
                        value = preferencesUiState.alwaysShowGitHubSubmit.second.value,
                        onValueChange = {
                            coroutineScope.launch {
                                preferencesViewModel.setPreference(
                                    preferencesUiState.alwaysShowGitHubSubmit.first,
                                    it,
                                )
                            }
                        },
                    ),
                    headlineContent = { Text(stringResource(R.string.always_show_github_submit_setting_name)) },
                    supportingContent = { Text(stringResource(R.string.always_show_github_submit_setting_description)) },
                    trailingContent = {
                        Switch(
                            checked = preferencesUiState.alwaysShowGitHubSubmit.second.value,
                            onCheckedChange = null,
                        )
                    },
                )
                ListItem(
                    modifier = Modifier.toggleable(
                        value = preferencesUiState.showCodebergSubmit.second.value,
                        onValueChange = { enabled ->
                            if (enabled) {
                                showCodebergSubmitDialog = true
                            } else {
                                setShowCodebergSubmit(false)
                            }
                        },
                    ),
                    headlineContent = { Text(stringResource(R.string.show_codeberg_submit_setting_name)) },
                    supportingContent = { Text(stringResource(R.string.show_codeberg_submit_setting_description)) },
                    trailingContent = {
                        Switch(
                            checked = preferencesUiState.showCodebergSubmit.second.value,
                            onCheckedChange = null,
                        )
                    },
                )
            }
        }
    }
}
