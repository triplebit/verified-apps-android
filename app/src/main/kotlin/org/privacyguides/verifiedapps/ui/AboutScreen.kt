@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.privacyguides.verifiedapps.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.privacyguides.verifiedapps.R

@Composable
fun AboutScreen(
    onOpenAppListClicked: () -> Unit,
    onLicenseIconButtonClicked: () -> Unit,
    onPrivacyPolicyIconButtonClicked: () -> Unit,
    onCreditsIconButtonClicked: () -> Unit,
) {
    val localUriHandler = LocalUriHandler.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                    )
                    Text(
                        text = stringResource(R.string.app_header),
                        style = MaterialTheme.typography.headlineLargeEmphasized,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = stringResource(R.string.startup_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(onClick = onOpenAppListClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(R.string.open_app_list),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }

            ListItem(
                modifier = Modifier.clickable {
                    localUriHandler.openUri("https://www.privacyguides.org")
                },
                headlineContent = { Text(stringResource(R.string.visit_privacy_guides_setting_name)) },
                supportingContent = { Text(stringResource(R.string.visit_privacy_guides_setting_description)) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier.clickable {
                    localUriHandler.openUri("https://www.privacyguides.org/donate")
                },
                headlineContent = { Text(stringResource(R.string.donation_setting_name)) },
                supportingContent = { Text(stringResource(R.string.donation_setting_description)) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier.clickable {
                    localUriHandler.openUri("https://github.com/privacyguides/verified-apps")
                },
                headlineContent = { Text(stringResource(R.string.database_setting_name)) },
                supportingContent = { Text(stringResource(R.string.database_setting_description)) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier.clickable {
                    localUriHandler.openUri("https://github.com/privacyguides/verified-apps-android")
                },
                headlineContent = { Text(stringResource(R.string.view_source_code_setting_name)) },
                supportingContent = { Text(stringResource(R.string.view_source_code_setting_description)) },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onLicenseIconButtonClicked),
                headlineContent = { Text(stringResource(R.string.license_setting_name)) },
                supportingContent = { Text(stringResource(R.string.license_setting_description)) },
                trailingContent = {
                    Icon(Icons.Filled.Info, contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onPrivacyPolicyIconButtonClicked),
                headlineContent = { Text(stringResource(R.string.privacy_policy_setting_name)) },
                supportingContent = { Text(stringResource(R.string.privacy_policy_setting_description)) },
                trailingContent = {
                    Icon(Icons.Filled.Info, contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier.clickable(onClick = onCreditsIconButtonClicked),
                headlineContent = { Text(stringResource(R.string.credits_setting_name)) },
                supportingContent = { Text(stringResource(R.string.credits_setting_description)) },
                trailingContent = {
                    Icon(Icons.Filled.Info, contentDescription = null)
                },
            )
        }
    }
}
