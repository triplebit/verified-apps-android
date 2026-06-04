@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.privacyguides.verifiedapps.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi

import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.codeberg.CodebergAppSubmission
import org.privacyguides.verifiedapps.github.GitHubAppSubmission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyAppScreen(
    onNavigateUp: () -> Unit,
    icon: Drawable?,
    name: String,
    packageName: String,
    hashes: Hashes,
    onLaunchedEffectHashEmpty: () -> Unit,
    internalDatabaseInfo: InternalDatabaseInfo,
    apkFailedToParse: Boolean,
    showHasMultipleSigners: Boolean,
    showSharingTools: Boolean,
    alwaysShowGitHubSubmit: Boolean,
    showCodebergSubmit: Boolean,
    isSystemApp: Boolean,
) {
    val context = LocalContext.current
    val verticalScroll = rememberScrollState()
    var showMoreInfoAboutInternalDatabaseStatusDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (hashes.hashes.isEmpty()) {
            onLaunchedEffectHashEmpty()
        }
    }

    val databaseStatus = internalDatabaseInfo.internalDatabaseStatus

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.verify_app)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(verticalScroll),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        if (apkFailedToParse) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "APK failed to parse",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Text(
                        "Make sure you provided a valid APK file.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (icon != null) {
                        Image(
                            rememberDrawablePainter(drawable = icon),
                            contentDescription = null,
                            modifier = Modifier.size(96.dp),
                        )
                    }
                    Text(text = name, style = MaterialTheme.typography.headlineSmallEmphasized)
                    Text(
                        text = packageName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = hashes.hashes.joinToString("\n"),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                    )
                    if (showHasMultipleSigners) {
                        Text(
                            text = "hasMultipleSigners: ${hashes.hasMultipleSigners}",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMoreInfoAboutInternalDatabaseStatusDialog = true
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = databaseStatus.statusIcon(),
                            contentDescription = stringResource(databaseStatus.labelRes()),
                            tint = databaseStatus.contentColor(),
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.verify_internal_database_status),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = stringResource(databaseStatus.labelRes()),
                                style = MaterialTheme.typography.titleLargeEmphasized,
                                color = databaseStatus.contentColor(),
                            )
                        }
                    }
                    val showGitHubSubmit = if (isSystemApp) {
                        alwaysShowGitHubSubmit
                    } else {
                        databaseStatus == InternalDatabaseStatus.NOT_FOUND ||
                            databaseStatus == InternalDatabaseStatus.NOMATCH ||
                            alwaysShowGitHubSubmit
                    }
                    if (showGitHubSubmit) {
                        val clipboardManager = LocalClipboardManager.current
                        val verificationData =
                            GitHubAppSubmission.buildVerificationInfo(packageName, hashes)
                        when (databaseStatus) {
                            InternalDatabaseStatus.NOT_FOUND -> {
                                Text(
                                    text = stringResource(R.string.not_found_submit_message),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            InternalDatabaseStatus.NOMATCH -> {
                                Text(
                                    text = stringResource(R.string.nomatch_github_submit_message),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            InternalDatabaseStatus.MATCH -> Unit
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val issueUri = GitHubAppSubmission.newIssueUri(
                                    packageManager = context.packageManager,
                                    packageName = packageName,
                                    appLabel = name,
                                    hashes = hashes,
                                )
                                openGitHubSubmission(context, issueUri)
                            },
                        ) {
                            Text(stringResource(R.string.submit_on_github))
                        }
                        if (showCodebergSubmit) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    copyVerificationInfoToClipboard(
                                        context = context,
                                        clipboardManager = clipboardManager,
                                        verificationData = verificationData,
                                    )
                                    val issueUri = CodebergAppSubmission.newIssueUri(
                                        packageName = packageName,
                                        appLabel = name,
                                        hashes = hashes,
                                    )
                                    openCodebergSubmission(context, issueUri)
                                },
                            ) {
                                Text(stringResource(R.string.submit_to_codeberg))
                            }
                        }
                    }
                }
            }

            if (showSharingTools) {
                val clipboardManager = LocalClipboardManager.current
                val verificationData = GitHubAppSubmission.buildVerificationInfo(packageName, hashes)
                val mimeType = "text/plain"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, verificationData)
                                type = mimeType
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            startActivity(context, shareIntent, ActivityOptions.makeBasic().toBundle())
                        },
                    ) {
                        Text("Share verification info")
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val clip = ClipData.newPlainText(mimeType, verificationData)
                            clipboardManager.setClip(ClipEntry(clip))
                        },
                    ) {
                        Text("Copy verification info")
                    }
                }
            }
        }

            Spacer(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
        }
    }

    if (showMoreInfoAboutInternalDatabaseStatusDialog) {
        AlertDialog(
            onDismissRequest = { showMoreInfoAboutInternalDatabaseStatusDialog = false },
            confirmButton = {
                TextButton(
                    { showMoreInfoAboutInternalDatabaseStatusDialog = false }
                ) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = databaseStatus.statusIcon(),
                        contentDescription = stringResource(databaseStatus.labelRes()),
                        tint = databaseStatus.contentColor(),
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(databaseStatus.labelRes()),
                        style = MaterialTheme.typography.headlineSmall,
                        color = databaseStatus.contentColor(),
                    )
                }
            },
            text = {
                LazyColumn {
                    item {
                        Text(stringResource(internalDatabaseInfo.internalDatabaseStatus.infoRes()))
                    }
                    item {
                        if (internalDatabaseInfo.internalDatabaseStatus == InternalDatabaseStatus.MATCH) {
                            Text("\nThe matched database entry for this app is from the following sources:\n")
                            Text(
                                text = internalDatabaseInfo.sources.joinToString("\n") { it.displayName },
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                "\nThis information can be useful if you distrust a specific source and want to make" +
                                        " sure the app isn't from them."
                            )
                        }
                    }
                }
            }
        )
    }
}

private fun copyVerificationInfoToClipboard(
    context: android.content.Context,
    clipboardManager: ClipboardManager,
    verificationData: String,
) {
    val clip = ClipData.newPlainText("text/plain", verificationData)
    clipboardManager.setClip(ClipEntry(clip))
    Toast.makeText(
        context,
        context.getString(R.string.verification_info_copied_toast),
        Toast.LENGTH_SHORT,
    ).show()
}

private fun openGitHubSubmission(context: android.content.Context, issueUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, issueUri)
    try {
        startActivity(context, intent, ActivityOptions.makeBasic().toBundle())
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No browser found to open the GitHub submission page.",
            Toast.LENGTH_LONG,
        ).show()
    }
}

private fun openCodebergSubmission(context: android.content.Context, issueUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, issueUri)
    try {
        startActivity(context, intent, ActivityOptions.makeBasic().toBundle())
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.codeberg_submission_no_browser),
            Toast.LENGTH_LONG,
        ).show()
    }
}
