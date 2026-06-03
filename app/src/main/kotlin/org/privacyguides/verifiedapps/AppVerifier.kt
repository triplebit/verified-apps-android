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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.currentBackStackEntryAsState
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
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel
import org.privacyguides.verifiedapps.ui.AboutScreen
import org.privacyguides.verifiedapps.ui.AppListScreen
import org.privacyguides.verifiedapps.ui.CreditsScreen
import org.privacyguides.verifiedapps.ui.LicenseScreen
import org.privacyguides.verifiedapps.ui.PrivacyPolicyScreen
import org.privacyguides.verifiedapps.ui.SettingsScreen
import org.privacyguides.verifiedapps.ui.VerifyAppScreen
import org.privacyguides.verifiedapps.ui.VerifyAppViewModel

enum class AppVerifierScreens(@StringRes val title: Int) {
    AppList(title = R.string.app_list),
    VerifyApp(title = R.string.verify_app),
    Settings(title = R.string.settings),
    About(title = R.string.about),
    License(title = R.string.license),
    PrivacyPolicy(title = R.string.privacy_policy),
    Credits(title = R.string.credits),
}

private val bottomNavRoutes = setOf(
    AppVerifierScreens.AppList.name,
    AppVerifierScreens.Settings.name,
    AppVerifierScreens.About.name,
)

@OptIn(ExperimentalMaterial3Api::class)
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

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

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

    fun navigateToBottomNavDestination(screen: AppVerifierScreens) {
        navController.navigate(screen.name) {
            popUpTo(AppVerifierScreens.AppList.name) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == AppVerifierScreens.AppList.name,
                        onClick = { navigateToBottomNavDestination(AppVerifierScreens.AppList) },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = stringResource(R.string.app_list),
                            )
                        },
                        label = { Text(stringResource(R.string.app_list)) },
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            openApkFileLauncher.launch(
                                arrayOf("application/vnd.android.package-archive"),
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.FileOpen,
                                contentDescription = stringResource(R.string.nav_open_apk),
                            )
                        },
                        label = { Text(stringResource(R.string.nav_open_apk)) },
                    )
                    NavigationBarItem(
                        selected = currentRoute == AppVerifierScreens.Settings.name,
                        onClick = { navigateToBottomNavDestination(AppVerifierScreens.Settings) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings),
                            )
                        },
                        label = { Text(stringResource(R.string.settings)) },
                    )
                    NavigationBarItem(
                        selected = currentRoute == AppVerifierScreens.About.name,
                        onClick = { navigateToBottomNavDestination(AppVerifierScreens.About) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.about),
                            )
                        },
                        label = { Text(stringResource(R.string.about)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isActionSend || isActionView) {
                AppVerifierScreens.VerifyApp.name
            } else {
                AppVerifierScreens.AppList.name
            },
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
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
                    onLaunchedEffect = {
                        verifyAppViewModel.clearUiState()
                        searchQuery = ""
                    },
                    onQueryChange = { searchQuery = it },
                    onSearch = { },
                    onSearchActiveChange = { },
                    getHashesFromPackageInfo = { verifyAppViewModel.getHashesFromPackageInfo(it) },
                    getInternalDatabaseInfoFromVerificationInfo = {
                        verifyAppViewModel.getInternalDatabaseInfoFromVerificationInfo(it)
                    },
                    showSystemApps = preferencesUiState.value.showSystemApps.second.value,
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.VerifyApp) {
                VerifyAppScreen(
                    onNavigateUp = { navController.navigateUp() },
                    verifyAppUiState.value.icon.value,
                    verifyAppUiState.value.name.value,
                    verifyAppUiState.value.packageName.value,
                    verifyAppUiState.value.hashes.value,
                    { navController.navigateUp() },
                    verifyAppUiState.value.internalDatabaseInfo.value,
                    verifyAppUiState.value.apkFailedToParse.value,
                    preferencesUiState.value.showHasMultipleSigners.second.value,
                    preferencesUiState.value.showSharingTools.second.value,
                    preferencesUiState.value.alwaysShowGitHubSubmit.second.value,
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.Settings) {
                SettingsScreen(
                    preferencesViewModel = preferencesViewModel,
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.About) {
                AboutScreen(
                    onLicenseIconButtonClicked = {
                        navController.navigate(AppVerifierScreens.License.name)
                    },
                    onPrivacyPolicyIconButtonClicked = {
                        navController.navigate(AppVerifierScreens.PrivacyPolicy.name)
                    },
                    onCreditsIconButtonClicked = {
                        navController.navigate(AppVerifierScreens.Credits.name)
                    },
                )
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.License) {
                LicenseScreen(onNavigateUp = { navController.navigateUp() })
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.PrivacyPolicy) {
                PrivacyPolicyScreen(onNavigateUp = { navController.navigateUp() })
            }
            composableWithDefaultSlideTransitions(route = AppVerifierScreens.Credits) {
                CreditsScreen(onNavigateUp = { navController.navigateUp() })
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