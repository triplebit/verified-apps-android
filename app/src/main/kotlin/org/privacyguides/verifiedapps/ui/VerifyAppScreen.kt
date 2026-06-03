package org.privacyguides.verifiedapps.ui

import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
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
import org.privacyguides.verifiedapps.github.GitHubAppSubmission

@Composable
fun VerifyAppScreen(
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
) {
    val context = LocalContext.current
    val verticalScroll = rememberScrollState()
    var showMoreInfoAboutInternalDatabaseStatusDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (hashes.hashes.isEmpty()) {
            onLaunchedEffectHashEmpty()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(verticalScroll),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (apkFailedToParse) {
            Text("APK FAILED TO PARSE")
            Text(
                "Make sure you provided a valid apk file."
            )
        } else {
            Text(
                "Internal Database Status:"
            )
            Row {
                FilledTonalButton(
                    onClick = { showMoreInfoAboutInternalDatabaseStatusDialog = true },
                ) {
                    Text(
                        internalDatabaseInfo.internalDatabaseStatus.simpleInternalDatabaseStatus.name.replace('_', ' '),
                        style = typography.headlineLarge
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Info,
                        "More info about internal database status",
                        tint = internalDatabaseInfo.internalDatabaseStatus.simpleInternalDatabaseStatus.color,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            val databaseStatus = internalDatabaseInfo.internalDatabaseStatus
            val showGitHubSubmit =
                databaseStatus == InternalDatabaseStatus.NOT_FOUND ||
                    databaseStatus == InternalDatabaseStatus.NOMATCH ||
                    alwaysShowGitHubSubmit
            if (showGitHubSubmit) {
                when (databaseStatus) {
                    InternalDatabaseStatus.NOT_FOUND -> {
                        Text(
                            "Not in database — submit fingerprints for review on GitHub.",
                            style = typography.bodyMedium,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    InternalDatabaseStatus.NOMATCH -> {
                        Text(
                            text = stringResource(R.string.nomatch_github_submit_message),
                            style = typography.bodyMedium,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    InternalDatabaseStatus.MATCH -> Unit
                }
                Button(
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
                    Text("Submit on GitHub")
                }
                Spacer(Modifier.height(8.dp))
            }
            if (icon != null) {
                Image(
                    rememberDrawablePainter(drawable = icon),
                    null,
                    Modifier.size(150.dp),
                )
            }
            Text(
                text = name,
                style = typography.titleLarge
            )
            Text(text = packageName)
            Text(
                text = hashes.hashes.joinToString("\n"),
                fontFamily = FontFamily.Monospace
            )
            if (showHasMultipleSigners) {
                Text(
                    "hasMultipleSigners: "
                )
                Text(
                    hashes.hasMultipleSigners.toString(),
                    fontWeight = FontWeight.Black
                )
            }
            if (showSharingTools) {
                val clipboardManager = LocalClipboardManager.current
                val verificationData = GitHubAppSubmission.buildVerificationInfo(packageName, hashes)
                val mimeType = "text/plain"
                Button(onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, verificationData)
                        type = mimeType
                    }

                    val shareIntent = Intent.createChooser(
                        sendIntent,
                        null,
                    )

                    startActivity(context, shareIntent, ActivityOptions.makeBasic().toBundle())
                }) {
                    Text("Share Verification Info")
                }
                Button(onClick = {
                    val clip: ClipData = ClipData.newPlainText(mimeType, verificationData)
                    clipboardManager.setClip(ClipEntry(clip))
                }) {
                    Text("Copy Verification Info")
                }
            }
        }

        Spacer(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        internalDatabaseInfo.internalDatabaseStatus.name,
                        style = typography.headlineSmall,
                        color = internalDatabaseInfo.internalDatabaseStatus.simpleInternalDatabaseStatus.color,
                    )
                }
            },
            text = {
                LazyColumn {
                    item {
                        Text(internalDatabaseInfo.internalDatabaseStatus.info)
                    }
                    item {
                        if (internalDatabaseInfo.internalDatabaseStatus == InternalDatabaseStatus.MATCH) {
                            Text("\nThe matched database entry for this app is from the following sources:\n")
                            Text(
                                text = internalDatabaseInfo.sources.joinToString("\n") { it.displayName },
                                style = typography.headlineSmall,
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
