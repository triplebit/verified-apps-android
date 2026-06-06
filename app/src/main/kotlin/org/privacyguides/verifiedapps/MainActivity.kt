package org.privacyguides.verifiedapps

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel
import org.privacyguides.verifiedapps.ui.VerifyAppViewModel
import org.privacyguides.verifiedapps.ui.theme.AppVerifierTheme

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val verifyAppViewModel: VerifyAppViewModel = viewModel()

            val preferencesViewModel: PreferencesViewModel = viewModel(
                factory = PreferencesViewModel.PreferencesViewModelFactory(dataStore)
            )

            val isActionSend =
                (intent.action == Intent.ACTION_SEND)

            val isActionView =
                (intent.action == Intent.ACTION_VIEW)

            // Process the incoming APK only once now.
            var intentHandled by rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                if (!intentHandled) {
                    intentHandled = true
                    val apkUri: Uri? = when {
                        isActionSend -> IntentCompat.getParcelableExtra(
                            intent,
                            Intent.EXTRA_STREAM,
                            Uri::class.java,
                        )
                        isActionView -> intent.data
                        else -> null
                    }
                    if (apkUri != null) {
                        verifyAppViewModel.setApkVerificationInfoAndInternalDatabaseStatusFromUri(
                            contentResolver,
                            apkUri,
                            packageManager,
                        )
                    }
                }
            }

            val preferencesLoaded by preferencesViewModel.preferencesLoaded.collectAsState()

            AppVerifierTheme(
                preferencesViewModel = preferencesViewModel
            ) {
                if (!preferencesLoaded) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    AppVerifierApp(
                        modifier = Modifier,
                        verifyAppViewModel = verifyAppViewModel,
                        preferencesViewModel = preferencesViewModel,
                        isActionSend = isActionSend,
                        isActionView = isActionView,
                    )
                }
            }
        }
    }
}
