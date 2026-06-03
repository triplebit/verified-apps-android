package org.privacyguides.verifiedapps.ui

import android.content.pm.PackageInfo
import org.privacyguides.verifiedapps.data.Hashes
import org.privacyguides.verifiedapps.data.InternalDatabaseInfo
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus

data class AppListEntry(
    val name: String,
    val packageName: String,
    val packageInfo: PackageInfo,
    val hashes: Hashes,
    val internalDatabaseInfo: InternalDatabaseInfo,
)

enum class AppListSort {
    NAME_ASC,
    NAME_DESC,
    PACKAGE_ASC,
    STATUS,
}

enum class AppListFilter {
    VERIFIED,
    NOT_IN_DATABASE,
    MISMATCH,
}

fun defaultStatusFilterMask(): Int =
    AppListFilter.entries.fold(0) { mask, filter -> mask or statusFilterBit(filter) }

fun statusFilterBit(filter: AppListFilter): Int = 1 shl filter.ordinal

fun isStatusFilterSelected(mask: Int, filter: AppListFilter): Boolean =
    mask and statusFilterBit(filter) != 0

fun toggleStatusFilter(mask: Int, filter: AppListFilter): Int {
    val bit = statusFilterBit(filter)
    return if (mask and bit != 0) {
        mask and bit.inv()
    } else {
        mask or bit
    }
}

fun AppListEntry.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    return name.contains(query, ignoreCase = true) ||
        packageName.contains(query, ignoreCase = true)
}

fun AppListEntry.matchesStatusFilter(filter: AppListFilter): Boolean = when (filter) {
    AppListFilter.VERIFIED ->
        internalDatabaseInfo.internalDatabaseStatus == InternalDatabaseStatus.MATCH
    AppListFilter.NOT_IN_DATABASE ->
        internalDatabaseInfo.internalDatabaseStatus == InternalDatabaseStatus.NOT_FOUND
    AppListFilter.MISMATCH ->
        internalDatabaseInfo.internalDatabaseStatus == InternalDatabaseStatus.NOMATCH
}

fun AppListEntry.matchesStatusFilters(mask: Int): Boolean =
    AppListFilter.entries.any { filter ->
        isStatusFilterSelected(mask, filter) && matchesStatusFilter(filter)
    }

fun compareAppListEntries(a: AppListEntry, b: AppListEntry, sort: AppListSort): Int = when (sort) {
    AppListSort.NAME_ASC -> a.name.compareTo(b.name, ignoreCase = true)
    AppListSort.NAME_DESC -> b.name.compareTo(a.name, ignoreCase = true)
    AppListSort.PACKAGE_ASC -> a.packageName.compareTo(b.packageName, ignoreCase = true)
    AppListSort.STATUS -> {
        val order = statusSortOrder(a.internalDatabaseInfo.internalDatabaseStatus) -
            statusSortOrder(b.internalDatabaseInfo.internalDatabaseStatus)
        if (order != 0) order else a.name.compareTo(b.name, ignoreCase = true)
    }
}

private fun statusSortOrder(status: InternalDatabaseStatus): Int = when (status) {
    InternalDatabaseStatus.MATCH -> 0
    InternalDatabaseStatus.NOT_FOUND -> 1
    InternalDatabaseStatus.NOMATCH -> 2
}
