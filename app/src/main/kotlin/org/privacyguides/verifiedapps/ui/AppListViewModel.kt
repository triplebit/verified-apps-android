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
)

class AppListViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager: PackageManager = application.packageManager

    private val _uiState = MutableStateFlow(AppListUiState())
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    private var userLoadStarted = false
    private var systemLoadStarted = false

    fun ensureUserEntriesLoaded(
        userPackages: List<PackageInfo>,
        selfPackageName: String,
        getHashesFromPackageInfo: (PackageInfo) -> Hashes,
        getInternalDatabaseInfoFromVerificationInfo: (VerificationInfo) -> InternalDatabaseInfo,
    ) {
        if (userLoadStarted || _uiState.value.userEntries.isNotEmpty()) return
        userLoadStarted = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }
            val entries = withContext(Dispatchers.Default) {
                buildAppListEntries(
                    packages = userPackages,
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
        systemPackages: List<PackageInfo>,
        selfPackageName: String,
        getHashesFromPackageInfo: (PackageInfo) -> Hashes,
        getInternalDatabaseInfoFromVerificationInfo: (VerificationInfo) -> InternalDatabaseInfo,
    ) {
        if (systemLoadStarted || _uiState.value.systemEntries.isNotEmpty()) return
        systemLoadStarted = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSystem = true) }
            val entries = withContext(Dispatchers.Default) {
                buildAppListEntries(
                    packages = systemPackages,
                    packageManager = packageManager,
                    selfPackageName = selfPackageName,
                    getHashesFromPackageInfo = getHashesFromPackageInfo,
                    getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
                )
            }
            _uiState.update { it.copy(systemEntries = entries, isLoadingSystem = false) }
        }
    }
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
