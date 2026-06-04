package org.privacyguides.verifiedapps.ui

import android.graphics.drawable.Drawable
import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.VerificationInfo
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel

enum class BottomNavPage {
    About,
    AppList,
    OpenApk,
    Settings,
}

@Composable
fun MainTabsScreen(
    pagerState: PagerState,
    searchQuery: String,
    sortOrdinal: Int,
    onSortOrdinalChange: (Int) -> Unit,
    statusFilterMask: Int,
    onStatusFilterMaskChange: (Int) -> Unit,
    onAppListItemClick: (
        name: String,
        packageName: String,
        hashes: Hashes,
        icon: Drawable,
        internalDatabaseInfo: InternalDatabaseInfo,
        isSystemApp: Boolean,
    ) -> Unit,
    onQueryChange: (query: String) -> Unit,
    onSearch: (query: String) -> Unit,
    onSearchActiveChange: (active: Boolean) -> Unit,
    getHashesFromPackageInfo: (packageInfo: PackageInfo) -> Hashes,
    getInternalDatabaseInfoFromVerificationInfo: (verification: VerificationInfo) -> InternalDatabaseInfo,
    showSystemApps: Boolean,
    onOpenApkFile: () -> Unit,
    preferencesViewModel: PreferencesViewModel,
    onOpenAppListClicked: () -> Unit,
    onLicenseIconButtonClicked: () -> Unit,
    onPrivacyPolicyIconButtonClicked: () -> Unit,
    onCreditsIconButtonClicked: () -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
    ) { page ->
        when (MainPagerLayout.pages[page]) {
            MainPagerPage.About -> AboutScreen(
                onOpenAppListClicked = onOpenAppListClicked,
                onLicenseIconButtonClicked = onLicenseIconButtonClicked,
                onPrivacyPolicyIconButtonClicked = onPrivacyPolicyIconButtonClicked,
                onCreditsIconButtonClicked = onCreditsIconButtonClicked,
            )
            MainPagerPage.AppList -> AppListScreen(
                searchQuery = searchQuery,
                sortOrdinal = sortOrdinal,
                onSortOrdinalChange = onSortOrdinalChange,
                statusFilterMask = statusFilterMask,
                onStatusFilterMaskChange = onStatusFilterMaskChange,
                onClickAppItem = onAppListItemClick,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onSearchActiveChange = onSearchActiveChange,
                getHashesFromPackageInfo = getHashesFromPackageInfo,
                getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
                showSystemApps = showSystemApps,
            )
            MainPagerPage.OpenApk -> OpenApkScreen(onOpenApkFile = onOpenApkFile)
            MainPagerPage.Settings -> SettingsScreen(preferencesViewModel = preferencesViewModel)
        }
    }
}
