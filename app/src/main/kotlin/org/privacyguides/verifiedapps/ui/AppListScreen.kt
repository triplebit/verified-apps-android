package org.privacyguides.verifiedapps.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.data.SimpleInternalDatabaseStatus
import org.privacyguides.verifiedapps.data.VerificationInfo

private enum class AppListTab {
    User,
    System,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    searchQuery: String,
    onClickAppItem: (
        name: String,
        packageName: String,
        hash: Hashes,
        icon: Drawable,
        internalDatabaseInfo: InternalDatabaseInfo,
    ) -> Unit,
    onLaunchedEffect: () -> Unit,
    onQueryChange: (query: String) -> Unit,
    onSearch: (query: String) -> Unit,
    onSearchActiveChange: (active: Boolean) -> Unit,
    getHashesFromPackageInfo: (packageInfo: PackageInfo) -> Hashes,
    getInternalDatabaseInfoFromVerificationInfo: (verification: VerificationInfo) -> InternalDatabaseInfo,
    showSystemApps: Boolean,
) {
    val context = LocalContext.current
    val packageManager: PackageManager = context.packageManager

    val systemPackageNames = remember {
        packageManager.getInstalledPackages(PackageManager.MATCH_SYSTEM_ONLY)
            .mapTo(HashSet()) { it.packageName }
    }

    val allInstalledPackages = remember {
        packageManager.getInstalledPackages(0)
    }

    val userPackages = remember(allInstalledPackages, systemPackageNames) {
        allInstalledPackages.filter { it.packageName !in systemPackageNames }
    }

    val systemPackages = remember(allInstalledPackages, systemPackageNames) {
        allInstalledPackages.filter { it.packageName in systemPackageNames }
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(AppListTab.User.ordinal) }

    LaunchedEffect(showSystemApps) {
        if (!showSystemApps) {
            selectedTab = AppListTab.User.ordinal
        }
    }

    val listPackages = if (showSystemApps) {
        when (AppListTab.entries[selectedTab]) {
            AppListTab.User -> userPackages
            AppListTab.System -> systemPackages
        }
    } else {
        userPackages
    }

    LaunchedEffect(key1 = Unit) {
        onLaunchedEffect()
    }

    Scaffold(
        topBar = {
            Column {
                val colors1 = SearchBarDefaults.colors()
                DockedSearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = onQueryChange,
                            onSearch = onSearch,
                            expanded = false,
                            onExpandedChange = onSearchActiveChange,
                            placeholder = { Text(stringResource(android.R.string.search_go)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            colors = colors1.inputFieldColors,
                        )
                    },
                    expanded = false,
                    onExpandedChange = onSearchActiveChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp),
                    colors = colors1,
                ) {}
                if (showSystemApps) {
                    PrimaryTabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == AppListTab.User.ordinal,
                            onClick = { selectedTab = AppListTab.User.ordinal },
                            text = { Text(stringResource(R.string.user_apps_tab)) },
                        )
                        Tab(
                            selected = selectedTab == AppListTab.System.ordinal,
                            onClick = { selectedTab = AppListTab.System.ordinal },
                            text = { Text(stringResource(R.string.system_apps_tab)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        AppListLazyColumn(
            packages = listPackages,
            searchQuery = searchQuery,
            packageManager = packageManager,
            selfPackageName = context.packageName,
            contentPadding = Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            ),
            onClickAppItem = onClickAppItem,
            getHashesFromPackageInfo = getHashesFromPackageInfo,
            getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
        )
    }
}

@Composable
private fun AppListLazyColumn(
    packages: List<PackageInfo>,
    searchQuery: String,
    packageManager: PackageManager,
    selfPackageName: String,
    contentPadding: Modifier,
    onClickAppItem: (
        name: String,
        packageName: String,
        hash: Hashes,
        icon: Drawable,
        internalDatabaseInfo: InternalDatabaseInfo,
    ) -> Unit,
    getHashesFromPackageInfo: (packageInfo: PackageInfo) -> Hashes,
    getInternalDatabaseInfoFromVerificationInfo: (verification: VerificationInfo) -> InternalDatabaseInfo,
) {
    LazyColumn(contentPadding) {
        items(packages, key = { it.packageName }) { installedPackage ->
            if (installedPackage.packageName == selfPackageName) return@items

            val packageInfo = packageManager.getPackageInfo(
                installedPackage.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES,
            )
            val name = packageInfo.applicationInfo?.let { applicationInfo ->
                packageManager.getApplicationLabel(applicationInfo).toString()
            } ?: ""

            if (searchQuery.isEmpty() || name.contains(searchQuery, true) ||
                installedPackage.packageName.contains(searchQuery, true)
            ) {
                val hashes = getHashesFromPackageInfo(packageInfo)
                val verificationInfo = VerificationInfo(packageInfo.packageName, hashes)

                AppItem(
                    name = name,
                    packageName = packageInfo.packageName,
                    hashes = hashes,
                    icon = packageManager.getApplicationIcon(
                        packageInfo.applicationInfo ?: ApplicationInfo(),
                    ),
                    onClickAppItem = onClickAppItem,
                    internalDatabaseInfo = getInternalDatabaseInfoFromVerificationInfo(verificationInfo),
                )
            }
        }
        item {
            Spacer(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
        }
    }
}

@Composable
fun AppItem(
    name: String,
    packageName: String,
    hashes: Hashes,
    icon: Drawable,
    onClickAppItem: (
        name: String,
        packageName: String,
        hash: Hashes,
        icon: Drawable,
        internalDatabaseInfo: InternalDatabaseInfo,
    ) -> Unit,
    internalDatabaseInfo: InternalDatabaseInfo,
) {
    ListItem(
        modifier = Modifier.clickable {
            onClickAppItem(name, packageName, hashes, icon, internalDatabaseInfo)
        },
        headlineContent = {
            Text(name)
        },
        overlineContent = {
            Text(packageName)
        },
        leadingContent = {
            Image(
                rememberDrawablePainter(drawable = icon),
                null,
                Modifier.size(50.dp),
            )
        },
        trailingContent = {
            when (internalDatabaseInfo.internalDatabaseStatus) {
                InternalDatabaseStatus.NOT_FOUND -> null
                InternalDatabaseStatus.MATCH -> Icon(
                    Icons.Filled.Verified,
                    "Verified successfully with internal database",
                    Modifier,
                    SimpleInternalDatabaseStatus.SUCCESS.color,
                )

                InternalDatabaseStatus.NOMATCH -> Icon(
                    Icons.Filled.Error,
                    "Verification with internal database NOT successful!",
                    Modifier,
                    SimpleInternalDatabaseStatus.FAILURE.color,
                )
            }
        },
    )
}
