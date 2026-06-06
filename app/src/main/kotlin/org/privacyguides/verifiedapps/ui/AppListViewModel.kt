package org.privacyguides.verifiedapps.ui

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.VerificationInfo

data class AppListUiState(
    val userEntries: List<AppListEntry> = emptyList(),
    val systemEntries: List<AppListEntry> = emptyList(),
    val isLoadingUser: Boolean = false,
    val isLoadingSystem: Boolean = false,
    val isRefreshing: Boolean = false,
)

class AppListViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager: PackageManager = application.packageManager

    private val _uiState = MutableStateFlow(AppListUiState())
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    private var userLoadStarted = false
    private var systemLoadStarted = false

    fun ensureUserEntriesLoaded(
        selfPackageName: String,
        getHashesFromPackageInfo: (PackageInfo) -> Hashes,
        getInternalDatabaseInfoFromVerificationInfo: (VerificationInfo) -> InternalDatabaseInfo,
    ) {
        if (userLoadStarted) return
        userLoadStarted = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }
            val entries = withContext(Dispatchers.Default) {
                buildAppListEntries(
                    packages = queryInstalledPackages().user,
                    packageManager = packageManager,
                    selfPackageName = selfPackageName,
                    getHashesFromPackageInfo = getHashesFromPackageInfo,
                    getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
                )
            }
            _uiState.update { it.copy(userEntries = entries, isLoadingUser = false) }
        }
    }

    fun ensureSystemEntriesLoaded(
        selfPackageName: String,
        getHashesFromPackageInfo: (PackageInfo) -> Hashes,
        getInternalDatabaseInfoFromVerificationInfo: (VerificationInfo) -> InternalDatabaseInfo,
    ) {
        if (systemLoadStarted) return
        systemLoadStarted = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSystem = true) }
            val entries = withContext(Dispatchers.Default) {
                buildAppListEntries(
                    packages = queryInstalledPackages().system,
                    packageManager = packageManager,
                    selfPackageName = selfPackageName,
                    getHashesFromPackageInfo = getHashesFromPackageInfo,
                    getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
                )
            }
            _uiState.update { it.copy(systemEntries = entries, isLoadingSystem = false) }
        }
    }

    /**
     * Re-reads the set of installed packages and rebuilds the list entries. This lets newly
     * installed (or removed) apps show up without restarting the app. The user list is always
     * refreshed; the system list is only refreshed when it is being shown.
     */
    fun refresh(
        showSystemApps: Boolean,
        selfPackageName: String,
        getHashesFromPackageInfo: (PackageInfo) -> Hashes,
        getInternalDatabaseInfoFromVerificationInfo: (VerificationInfo) -> InternalDatabaseInfo,
    ) {
        if (_uiState.value.isRefreshing) return
        // Mark both lists as loaded so the one-shot loaders don't race the refreshed data.
        userLoadStarted = true
        if (showSystemApps) systemLoadStarted = true
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val installed = withContext(Dispatchers.Default) { queryInstalledPackages() }
            val userEntries = withContext(Dispatchers.Default) {
                buildAppListEntries(
                    packages = installed.user,
                    packageManager = packageManager,
                    selfPackageName = selfPackageName,
                    getHashesFromPackageInfo = getHashesFromPackageInfo,
                    getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
                )
            }
            val systemEntries = if (showSystemApps) {
                withContext(Dispatchers.Default) {
                    buildAppListEntries(
                        packages = installed.system,
                        packageManager = packageManager,
                        selfPackageName = selfPackageName,
                        getHashesFromPackageInfo = getHashesFromPackageInfo,
                        getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
                    )
                }
            } else {
                null
            }
            _uiState.update { state ->
                state.copy(
                    userEntries = userEntries,
                    systemEntries = systemEntries ?: state.systemEntries,
                    isRefreshing = false,
                )
            }
        }
    }

    private fun queryInstalledPackages(): InstalledPackages {
        val systemPackageNames = packageManager.getInstalledPackages(PackageManager.MATCH_SYSTEM_ONLY)
            .mapTo(HashSet()) { it.packageName }
        val allPackages = packageManager.getInstalledPackages(0)
        return InstalledPackages(
            user = allPackages.filter { it.packageName !in systemPackageNames },
            system = allPackages.filter { it.packageName in systemPackageNames },
        )
    }

    private data class InstalledPackages(
        val user: List<PackageInfo>,
        val system: List<PackageInfo>,
    )
}

internal fun buildAppListEntries(
    packages: List<PackageInfo>,
    packageManager: PackageManager,
    selfPackageName: String,
    getHashesFromPackageInfo: (PackageInfo) -> Hashes,
    getInternalDatabaseInfoFromVerificationInfo: (verification: VerificationInfo) -> InternalDatabaseInfo,
): List<AppListEntry> = packages.mapNotNull { installedPackage ->
    if (installedPackage.packageName == selfPackageName) return@mapNotNull null

    val packageInfo = packageManager.getPackageInfo(
        installedPackage.packageName,
        PackageManager.GET_SIGNING_CERTIFICATES,
    )
    val applicationInfo = packageInfo.applicationInfo ?: return@mapNotNull null
    val name = packageManager.getApplicationLabel(applicationInfo).toString()
    val hashes = getHashesFromPackageInfo(packageInfo)
    val verificationInfo = VerificationInfo(packageInfo.packageName, hashes)

    AppListEntry(
        name = name,
        packageName = packageInfo.packageName,
        packageInfo = packageInfo,
        hashes = hashes,
        internalDatabaseInfo = getInternalDatabaseInfoFromVerificationInfo(verificationInfo),
    )
}
