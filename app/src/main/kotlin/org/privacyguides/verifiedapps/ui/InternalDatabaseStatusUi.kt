package org.privacyguides.verifiedapps.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.privacyguides.verifiedapps.R
import org.privacyguides.verifiedapps.data.InternalDatabaseStatus

@StringRes
fun InternalDatabaseStatus.labelRes(): Int = when (this) {
    InternalDatabaseStatus.MATCH -> R.string.app_list_status_verified
    InternalDatabaseStatus.NOMATCH -> R.string.app_list_status_mismatch
    InternalDatabaseStatus.NOT_FOUND -> R.string.app_list_status_unknown
}

@StringRes
fun InternalDatabaseStatus.infoRes(): Int = when (this) {
    InternalDatabaseStatus.NOT_FOUND -> R.string.internal_database_status_not_found_info
    InternalDatabaseStatus.MATCH -> R.string.internal_database_status_match_info
    InternalDatabaseStatus.NOMATCH -> R.string.internal_database_status_nomatch_info
}

fun InternalDatabaseStatus.statusIcon(): ImageVector = when (this) {
    InternalDatabaseStatus.MATCH -> Icons.Default.CheckCircle
    InternalDatabaseStatus.NOMATCH -> Icons.Default.Error
    InternalDatabaseStatus.NOT_FOUND -> Icons.Default.HelpOutline
}

@Composable
fun InternalDatabaseStatus.contentColor(): Color = when (this) {
    InternalDatabaseStatus.MATCH -> MaterialTheme.colorScheme.primary
    InternalDatabaseStatus.NOMATCH -> MaterialTheme.colorScheme.error
    InternalDatabaseStatus.NOT_FOUND -> MaterialTheme.colorScheme.outline
}
