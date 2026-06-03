@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.privacyguides.verifiedapps.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.privacyguides.verifiedapps.R

@Composable
fun StartupScreen(
    modifier: Modifier,
    onSettingsButtonClicked: () -> Unit,
    onAppListButtonClicked: () -> Unit,
    onVerifyApkFileButtonClicked: () -> Unit,
    onLaunchedEffect: () -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        onLaunchedEffect()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
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
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAppListButtonClicked() },
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.app_list))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onVerifyApkFileButtonClicked() },
            ) {
                Icon(Icons.Filled.FileOpen, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.startup_verify_apk))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSettingsButtonClicked() },
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.settings))
            }
        }

        Spacer(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
    }
}
