@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package org.privacyguides.verifiedapps.ui

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.VerificationInfo

@Composable
fun AppListScreen(
    searchQuery: String,
    sortOrdinal: Int,
    onSortOrdinalChange: (Int) -> Unit,
    statusFilterMask: Int,
    onStatusFilterMaskChange: (Int) -> Unit,
    onClickAppItem: (
        name: String,
        packageName: String,
        hash: Hashes,
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
) {
    val context = LocalContext.current
    val packageManager: PackageManager = context.packageManager
    val appListViewModel: AppListViewModel = viewModel()
    val appListUiState by appListViewModel.uiState.collectAsState()

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

    val coroutineScope = rememberCoroutineScope()
    val sortOrder = AppListSort.entries[sortOrdinal]
    var selectedAppListTab by rememberSaveable { mutableIntStateOf(AppListTab.User.ordinal) }
    val innerPagerState = rememberPagerState(
        initialPage = selectedAppListTab,
        pageCount = { if (showSystemApps) 2 else 1 },
    )
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var searchFieldVisible by rememberSaveable { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    fun closeSearch() {
        onQueryChange("")
        onSearch("")
        searchFieldVisible = false
    }

    LaunchedEffect(searchFieldVisible) {
        onSearchActiveChange(searchFieldVisible)
        if (searchFieldVisible) {
            searchFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(showSystemApps) {
        if (!showSystemApps) {
            selectedAppListTab = AppListTab.User.ordinal
        }
    }

    LaunchedEffect(selectedAppListTab) {
        if (innerPagerState.currentPage != selectedAppListTab) {
            innerPagerState.animateScrollToPage(selectedAppListTab)
        }
    }

    LaunchedEffect(innerPagerState) {
        snapshotFlow { innerPagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (selectedAppListTab != page) {
                    selectedAppListTab = page
                }
            }
    }

    val selfPackageName = context.packageName

    LaunchedEffect(userPackages, getHashesFromPackageInfo, getInternalDatabaseInfoFromVerificationInfo) {
        appListViewModel.ensureUserEntriesLoaded(
            userPackages = userPackages,
            selfPackageName = selfPackageName,
            getHashesFromPackageInfo = getHashesFromPackageInfo,
            getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
        )
    }

    LaunchedEffect(
        showSystemApps,
        systemPackages,
        getHashesFromPackageInfo,
        getInternalDatabaseInfoFromVerificationInfo,
    ) {
        if (showSystemApps) {
            appListViewModel.ensureSystemEntriesLoaded(
                systemPackages = systemPackages,
                selfPackageName = selfPackageName,
                getHashesFromPackageInfo = getHashesFromPackageInfo,
                getInternalDatabaseInfoFromVerificationInfo = getInternalDatabaseInfoFromVerificationInfo,
            )
        }
    }

    val userVisibleEntries = remember(
        appListUiState.userEntries,
        searchQuery,
        statusFilterMask,
        sortOrder,
    ) {
        appListUiState.userEntries
            .filter { it.matchesStatusFilters(statusFilterMask) && it.matchesSearch(searchQuery) }
            .sortedWith { a, b -> compareAppListEntries(a, b, sortOrder) }
    }

    val systemVisibleEntries = remember(
        appListUiState.systemEntries,
        searchQuery,
        statusFilterMask,
        sortOrder,
    ) {
        appListUiState.systemEntries
            .filter { it.matchesStatusFilters(statusFilterMask) && it.matchesSearch(searchQuery) }
            .sortedWith { a, b -> compareAppListEntries(a, b, sortOrder) }
    }

    val userResultsCount = userVisibleEntries.size
    val systemResultsCount = systemVisibleEntries.size

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                navigationIcon = {
                    if (!searchFieldVisible) {
                        IconButton(onClick = { searchFieldVisible = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.app_list_search),
                                tint = if (searchQuery.isNotEmpty()) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                },
                title = {
                    if (searchFieldVisible) {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = onQueryChange,
                            onSearch = { query ->
                                onSearch(query)
                                searchFieldVisible = false
                            },
                            expanded = true,
                            onExpandedChange = { expanded ->
                                if (!expanded) {
                                    closeSearch()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(searchFocusRequester),
                            placeholder = { Text(stringResource(R.string.app_list_search_hint)) },
                            leadingIcon = null,
                            trailingIcon = null,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.app_list),
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                actions = {
                    if (searchFieldVisible) {
                        IconButton(onClick = { closeSearch() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.app_list_search_close),
                            )
                        }
                    } else {
                        Box {
                            IconButton(onClick = { sortMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = stringResource(R.string.app_list_sort),
                                )
                            }
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                            ) {
                                AppListSort.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(appListSortLabel(option)) },
                                        onClick = {
                                            onSortOrdinalChange(option.ordinal)
                                            sortMenuExpanded = false
                                        },
                                        leadingIcon = if (sortOrder == option) {
                                            {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                )
                                            }
                                        } else {
                                            null
                                        },
                                    )
                                }
                            }
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val resultsCount = if (selectedAppListTab == AppListTab.System.ordinal) {
                    systemResultsCount
                } else {
                    userResultsCount
                }
                Text(
                    text = stringResource(R.string.app_list_results_count, resultsCount),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppListFilter.entries.forEach { filter ->
                    val selected = isStatusFilterSelected(statusFilterMask, filter)
                    FilterChip(
                        selected = selected,
                        onClick = {
                            onStatusFilterMaskChange(toggleStatusFilter(statusFilterMask, filter))
                        },
                        label = { Text(appListFilterLabel(filter)) },
                        leadingIcon = {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )
                }
            }

            if (showSystemApps) {
                PrimaryTabRow(
                    selectedTabIndex = selectedAppListTab,
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    Tab(
                        selected = selectedAppListTab == AppListTab.User.ordinal,
                        onClick = {
                            coroutineScope.launch {
                                innerPagerState.animateScrollToPage(AppListTab.User.ordinal)
                            }
                        },
                        text = { Text(stringResource(R.string.user_apps_tab)) },
                    )
                    Tab(
                        selected = selectedAppListTab == AppListTab.System.ordinal,
                        onClick = {
                            coroutineScope.launch {
                                innerPagerState.animateScrollToPage(AppListTab.System.ordinal)
                            }
                        },
                        text = { Text(stringResource(R.string.system_apps_tab)) },
                    )
                }
            }

            val listPagerModifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .imePadding()
                .then(
                    if (showSystemApps) {
                        Modifier.nestedScroll(
                            PagerDefaults.pageNestedScrollConnection(
                                innerPagerState,
                                Orientation.Horizontal,
                            ),
                        )
                    } else {
                        Modifier
                    },
                )

            if (showSystemApps) {
                HorizontalPager(
                    state = innerPagerState,
                    modifier = listPagerModifier,
                    beyondViewportPageCount = 1,
                ) { page ->
                    when (page) {
                        AppListTab.User.ordinal -> AppListEntriesBody(
                            modifier = Modifier.fillMaxSize(),
                            visibleEntries = userVisibleEntries,
                            isLoading = appListUiState.isLoadingUser && userVisibleEntries.isEmpty(),
                            packageManager = packageManager,
                            isSystemApp = false,
                            onClickAppItem = onClickAppItem,
                        )
                        else -> AppListEntriesBody(
                            modifier = Modifier.fillMaxSize(),
                            visibleEntries = systemVisibleEntries,
                            isLoading = appListUiState.isLoadingSystem && systemVisibleEntries.isEmpty(),
                            packageManager = packageManager,
                            isSystemApp = true,
                            onClickAppItem = onClickAppItem,
                        )
                    }
                }
            } else {
                AppListEntriesBody(
                    modifier = listPagerModifier,
                    visibleEntries = userVisibleEntries,
                    isLoading = appListUiState.isLoadingUser && userVisibleEntries.isEmpty(),
                    packageManager = packageManager,
                    isSystemApp = false,
                    onClickAppItem = onClickAppItem,
                )
            }
        }
    }
}

@Composable
private fun AppListEntriesBody(
    visibleEntries: List<AppListEntry>,
    isLoading: Boolean,
    packageManager: PackageManager,
    isSystemApp: Boolean,
    onClickAppItem: (
        name: String,
        packageName: String,
        hash: Hashes,
        icon: Drawable,
        internalDatabaseInfo: InternalDatabaseInfo,
        isSystemApp: Boolean,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
        }
    } else if (visibleEntries.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.HelpOutline,
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
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp,
            ),
        ) {
            items(visibleEntries, key = { it.packageName }) { entry ->
                val status = entry.internalDatabaseInfo.internalDatabaseStatus
                ListItem(
                    modifier = Modifier.clickable {
                        onClickAppItem(
                            entry.name,
                            entry.packageName,
                            entry.hashes,
                            AppIconCache.get(packageManager, entry.packageName),
                            entry.internalDatabaseInfo,
                            isSystemApp,
                        )
                    },
                    leadingContent = {
                        AppListItemIcon(
                            packageName = entry.packageName,
                            modifier = Modifier.size(48.dp),
                        )
                    },
                    headlineContent = {
                        Text(
                            text = entry.name,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = entry.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = status.statusIcon(),
                            contentDescription = stringResource(status.labelRes()),
                            tint = status.contentColor(),
                        )
                    },
                )
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
    AppListFilter.VERIFIED -> stringResource(R.string.app_list_filter_verified)
    AppListFilter.NOT_IN_DATABASE -> stringResource(R.string.app_list_filter_not_in_database)
    AppListFilter.MISMATCH -> stringResource(R.string.app_list_filter_mismatch)
}

@Composable
private fun AppListItemIcon(
    packageName: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var drawable by remember(packageName) { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(packageName) {
        drawable = withContext(Dispatchers.IO) {
            AppIconCache.get(packageManager, packageName)
        }
    }

    if (drawable != null) {
        Image(
            painter = rememberDrawablePainter(drawable = drawable),
            contentDescription = null,
            modifier = modifier,
        )
    } else {
        Spacer(modifier = modifier)
    }
}
