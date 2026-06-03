package org.privacyguides.verifiedapps.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus
import org.privacyguides.verifiedapps.data.VerificationInfo
import org.privacyguides.verifiedapps.ui.theme.MismatchRed
import org.privacyguides.verifiedapps.ui.theme.UnknownGray
import org.privacyguides.verifiedapps.ui.theme.VerifiedGreen

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
    var sortOrdinal by rememberSaveable { mutableIntStateOf(AppListSort.NAME_ASC.ordinal) }
    var filterOrdinal by rememberSaveable { mutableIntStateOf(AppListFilter.ALL.ordinal) }
    val sortOrder = AppListSort.entries[sortOrdinal]
    val statusFilter = AppListFilter.entries[filterOrdinal]
    var sortMenuExpanded by remember { mutableStateOf(false) }

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

    val selfPackageName = context.packageName

    val allEntries = remember(
        listPackages,
        getHashesFromPackageInfo,
        getInternalDatabaseInfoFromVerificationInfo,
    ) {
        buildAppListEntries(
            packages = listPackages,
            packageManager = packageManager,
            selfPackageName = selfPackageName,
            getHashesFromPackageInfo = getHashesFromPackageInfo,
            getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
        )
    }

    val visibleEntries = remember(allEntries, searchQuery, statusFilter, sortOrder) {
        allEntries
            .filter { it.matchesFilter(statusFilter) && it.matchesSearch(searchQuery) }
            .sortedWith { a, b -> compareAppListEntries(a, b, sortOrder) }
    }

    LaunchedEffect(key1 = Unit) {
        onLaunchedEffect()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.app_list_search_hint)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch(searchQuery)
                        onSearchActiveChange(false)
                    },
                ),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.app_list_results_count, visibleEntries.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    IconButton(onClick = { sortMenuExpanded = true }) {
                        Icon(Icons.Default.Sort, stringResource(R.string.app_list_sort))
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false },
                    ) {
                        AppListSort.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(appListSortLabel(option)) },
                                onClick = {
                                    sortOrdinal = option.ordinal
                                    sortMenuExpanded = false
                                },
                                leadingIcon = if (sortOrder == option) {
                                    { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                                } else {
                                    null
                                },
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppListFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = statusFilter == filter,
                        onClick = { filterOrdinal = filter.ordinal },
                        label = { Text(appListFilterLabel(filter)) },
                    )
                }
            }

            if (showSystemApps) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
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

            if (visibleEntries.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.app_list_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(visibleEntries, key = { it.packageName }) { entry ->
                        AppItem(
                            name = entry.name,
                            packageName = entry.packageName,
                            hashes = entry.hashes,
                            icon = entry.icon,
                            onClickAppItem = onClickAppItem,
                            internalDatabaseInfo = entry.internalDatabaseInfo,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun appListSortLabel(sort: AppListSort): String = when (sort) {
    AppListSort.NAME_ASC -> stringResource(R.string.app_list_sort_name_asc)
    AppListSort.NAME_DESC -> stringResource(R.string.app_list_sort_name_desc)
    AppListSort.PACKAGE_ASC -> stringResource(R.string.app_list_sort_package)
    AppListSort.STATUS -> stringResource(R.string.app_list_sort_status)
}

@Composable
private fun appListFilterLabel(filter: AppListFilter): String = when (filter) {
    AppListFilter.ALL -> stringResource(R.string.app_list_filter_all)
    AppListFilter.VERIFIED -> stringResource(R.string.app_list_filter_verified)
    AppListFilter.NOT_IN_DATABASE -> stringResource(R.string.app_list_filter_not_in_database)
    AppListFilter.MISMATCH -> stringResource(R.string.app_list_filter_mismatch)
}

private fun buildAppListEntries(
    packages: List<PackageInfo>,
    packageManager: PackageManager,
    selfPackageName: String,
    getHashesFromPackageInfo: (packageInfo: PackageInfo) -> Hashes,
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
        icon = packageManager.getApplicationIcon(applicationInfo),
        hashes = hashes,
        internalDatabaseInfo = getInternalDatabaseInfoFromVerificationInfo(verificationInfo),
    )
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
    val statusColor = when (internalDatabaseInfo.internalDatabaseStatus) {
        InternalDatabaseStatus.MATCH -> VerifiedGreen
        InternalDatabaseStatus.NOMATCH -> MismatchRed
        InternalDatabaseStatus.NOT_FOUND -> UnknownGray
    }

    Card(
        onClick = {
            onClickAppItem(name, packageName, hashes, icon, internalDatabaseInfo)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = rememberDrawablePainter(drawable = icon),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            when (internalDatabaseInfo.internalDatabaseStatus) {
                InternalDatabaseStatus.NOT_FOUND -> {
                    Icon(
                        Icons.Default.HelpOutline,
                        stringResource(R.string.app_list_status_unknown),
                        tint = statusColor,
                    )
                }
                InternalDatabaseStatus.MATCH -> {
                    Icon(
                        Icons.Default.CheckCircle,
                        stringResource(R.string.app_list_status_verified),
                        tint = statusColor,
                    )
                }
                InternalDatabaseStatus.NOMATCH -> {
                    Icon(
                        Icons.Default.Error,
                        stringResource(R.string.app_list_status_mismatch),
                        tint = statusColor,
                    )
                }
            }
        }
    }
}
