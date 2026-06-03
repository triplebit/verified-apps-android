package org.privacyguides.verifiedapps

import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.SubmissionUiState
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel
import org.privacyguides.verifiedapps.ui.AppListScreen
import org.privacyguides.verifiedapps.ui.CreditsScreen
import org.privacyguides.verifiedapps.ui.LicenseScreen
import org.privacyguides.verifiedapps.ui.PrivacyPolicyScreen
import org.privacyguides.verifiedapps.ui.SettingsScreen
import org.privacyguides.verifiedapps.ui.StartupScreen
import org.privacyguides.verifiedapps.ui.VerifyAppScreen
import org.privacyguides.verifiedapps.ui.VerifyAppViewModel

enum class AppVerifierScreens(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    AppList(title = R.string.app_list),
    VerifyApp(title = R.string.verify_app),
    Settings(title = R.string.settings),
    License(title = R.string.license),
    PrivacyPolicy(title = R.string.privacy_policy),
    Credits(title = R.string.credits),
}

@Composable
fun AppVerifierAppBar(
) {

}

@Composable
fun AppVerifierApp(
    modifier: Modifier,
    verifyAppViewModel: VerifyAppViewModel,
    preferencesViewModel: PreferencesViewModel,
    isActionSend: Boolean,
    isActionView: Boolean,
) {
    val preferencesUiState = preferencesViewModel.uiState.collectAsState()

    val verifyAppUiState = verifyAppViewModel.uiState.collectAsState()
    val submissionState = verifyAppViewModel.submissionState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val navController = rememberNavController()

//    val backStackEntry by navController.currentBackStackEntryAsState()

//    val currentScreen = AppVerifierScreens.valueOf(
//        backStackEntry?.destination?.route ?: AppVerifierScreens.Start.name
//    )

    val context = LocalContext.current

    val openApkFileLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                verifyAppViewModel.setApkVerificationInfoAndInternalDatabaseStatusFromUri(
                    context.contentResolver,
                    uri,
                    context.packageManager,
                )
                navController.navigate(AppVerifierScreens.VerifyApp.name)
            }
        }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppVerifierAppBar()
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isActionSend || isActionView) {
                AppVerifierScreens.VerifyApp.name
            } else {
                AppVerifierScreens.Start.name
            },
            modifier = modifier.padding(
                innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            ),
        ) {
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.Start) {
                StartupScreen(
                    modifier = modifier,
                    onSettingsButtonClicked = {
                        navController.navigate(AppVerifierScreens.Settings.name)
                    },
                    onAppListButtonClicked = {
                        navController.navigate(AppVerifierScreens.AppList.name)
                    },
                    onVerifyApkFileButtonClicked = {
                        openApkFileLauncher.launch(arrayOf("application/vnd.android.package-archive"))
                    },
                    onLaunchedEffect = {
                        // clear VerifyAppUiState when exiting VerifyAppScreen from opening an apk and going back to StartupScreen.
                        verifyAppViewModel.clearUiState()
                        // clear searchQuery when going back to StartupScreen.
                        searchQuery = ""
                    }
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.AppList) {
                AppListScreen(
                    searchQuery,
                    { name: String, packageName: String, hashes: Hashes, icon: Drawable, internalDatabaseInfo:
                    InternalDatabaseInfo ->
                        verifyAppViewModel.setAppVerificationInfo(
                            name,
                            packageName,
                            hashes,
                            internalDatabaseInfo
                        )
                        verifyAppViewModel.setAppIcon(icon)
                        navController.navigate(AppVerifierScreens.VerifyApp.name)
                    },
                    { verifyAppViewModel.clearUiState() },
                    { searchQuery = it },
                    { },
                    { },
                    { verifyAppViewModel.getHashesFromPackageInfo(it) },
                    { verifyAppViewModel.getInternalDatabaseInfoFromVerificationInfo(it) },
                    preferencesUiState.value.showSystemApps.second.value,
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.VerifyApp) {
                LaunchedEffect(submissionState.value) {
                    when (val state = submissionState.value) {
                        SubmissionUiState.Success -> {
                            snackbarHostState.showSnackbar("App submitted successfully. Thank you!")
                            verifyAppViewModel.clearSubmissionMessage()
                        }
                        is SubmissionUiState.Error -> {
                            snackbarHostState.showSnackbar(state.message)
                            verifyAppViewModel.clearSubmissionMessage()
                        }
                        else -> Unit
                    }
                }
                VerifyAppScreen(
                    verifyAppUiState.value.icon.value,
                    verifyAppUiState.value.name.value,
                    verifyAppUiState.value.packageName.value,
                    verifyAppUiState.value.hashes.value,
                    { navController.navigateUp() },
                    verifyAppUiState.value.internalDatabaseInfo.value,
                    verifyAppUiState.value.apkFailedToParse.value,
                    preferencesUiState.value.showHasMultipleSigners.second.value,
                    preferencesUiState.value.showSharingTools.second.value,
                    submissionState.value,
                    { verifyAppViewModel.submitAppForDatabaseInclusion() },
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.Settings) {
                SettingsScreen(
                    onLicenseIconButtonClicked = {
                        navController.navigate(AppVerifierScreens.License.name)
                    },
                    onPrivacyPolicyIconButtonClicked = {
                        navController.navigate(AppVerifierScreens.PrivacyPolicy.name)
                    },
                    onCreditsIconButtonClicked = {
                        navController.navigate(AppVerifierScreens.Credits.name)
                    },
                    preferencesViewModel = preferencesViewModel,
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.License) {
                LicenseScreen()
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.PrivacyPolicy) {
                PrivacyPolicyScreen()
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.Credits) {
                CreditsScreen()
            }
        }
    }
}

fun getStateDestinationRoute(state: NavBackStackEntry): AppVerifierScreens? {
    state.destination.route?.let { return AppVerifierScreens.valueOf(it) }
    return null
}

fun getEnterTransition(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry,
): EnterTransition {
    val initialNavBarRoute = getStateDestinationRoute(initialState)
    val targetNavBarRoute = getStateDestinationRoute(targetState)

    return if ((initialNavBarRoute != null) && (targetNavBarRoute != null)) {
        slideIn {
            IntOffset(
                if (initialNavBarRoute.ordinal > targetNavBarRoute.ordinal) {
                    -it.width
                } else {
                    it.width
                }, 0
            )
        } + fadeIn()
    } else {
        EnterTransition.None
    }
}

fun getExitTransition(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry,
): ExitTransition {
    val initialNavBarRoute = getStateDestinationRoute(initialState)
    val targetNavBarRoute = getStateDestinationRoute(targetState)

    return if ((initialNavBarRoute != null) && (targetNavBarRoute != null)) {
        slideOut {
            IntOffset(
                if (initialNavBarRoute.ordinal > targetNavBarRoute.ordinal) {
                    it.width
                } else {
                    -it.width
                }, 0
            )
        } + fadeOut()
    } else {
        ExitTransition.None
    }
}

fun NavGraphBuilder.composableWithDefaultSlideTransitions(
    route: AppVerifierScreens,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    sizeTransform: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null,
    content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit),
) {
    composable(route.name, arguments, deepLinks, if (enterTransition == null) {
        {
            getEnterTransition(initialState, targetState)
        }
    } else {
        null
    }, if (exitTransition == null) {
        {
            getExitTransition(initialState, targetState)
        }
    } else {
        null
    }, if (popEnterTransition == null) {
        {
            getEnterTransition(initialState, targetState)
        }
    } else {
        null
    }, if (popExitTransition == null) {
        {
            getExitTransition(initialState, targetState)
        }
    } else {
        null
    }, sizeTransform, content)
}

fun NavGraphBuilder.navigationWithDefaultSlideTransitions(
    startDestination: String,
    route: AppVerifierScreens,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    sizeTransform: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null,
    builder: NavGraphBuilder.() -> Unit,
) {
    navigation(startDestination, route.name, arguments, deepLinks, if (enterTransition == null) {
        {
            getEnterTransition(initialState, targetState)
        }
    } else {
        null
    }, if (exitTransition == null) {
        {
            getExitTransition(initialState, targetState)
        }
    } else {
        null
    }, if (popEnterTransition == null) {
        {
            getEnterTransition(initialState, targetState)
        }
    } else {
        null
    }, if (popExitTransition == null) {
        {
            getExitTransition(initialState, targetState)
        }
    } else {
        null
    }, sizeTransform, builder)
}