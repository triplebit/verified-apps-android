package org.privacyguides.verifiedapps.github

import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Maps Android install metadata to [app-submission.yml] `appSource` dropdown values.
 *
 * Detection is best-effort: the installer-of-record can be missing, spoofed, or reported as the
 * system package installer (common for F-Droid). Unmapped installers fall back to [APP_SOURCE_OTHER].
 */
object AppInstallSource {

    const val APP_SOURCE_GOOGLE_PLAY = "Google Play / Aurora Store"
    const val APP_SOURCE_F_DROID = "F-Droid"
    const val APP_SOURCE_ACCRESCENT = "Accrescent"
    const val APP_SOURCE_GITHUB = "GitHub"
    const val APP_SOURCE_GITLAB = "GitLab"
    const val APP_SOURCE_CODEBERG = "Codeberg"
    const val APP_SOURCE_DEV_WEBSITE = "Developer's Website"
    const val APP_SOURCE_OTHER = "Other"

    private val INSTALLER_TO_APP_SOURCE = mapOf(
        // Google Play and Play-related
        "com.android.vending" to APP_SOURCE_GOOGLE_PLAY,
        "com.google.android.feedback" to APP_SOURCE_GOOGLE_PLAY,
        // Aurora Store
        "com.aurora.store" to APP_SOURCE_GOOGLE_PLAY,
        "com.aurora.store.nightly" to APP_SOURCE_GOOGLE_PLAY,
        // F-Droid
        "org.fdroid.fdroid" to APP_SOURCE_F_DROID,
        "org.fdroid.basic" to APP_SOURCE_F_DROID,
        // Accrescent
        "app.accrescent.client" to APP_SOURCE_ACCRESCENT,
        // Git hosting / release clients (when they remain installer of record)
        "com.github.android" to APP_SOURCE_GITHUB,
        "com.gitlab.android" to APP_SOURCE_GITLAB,
    )

    private val INITIATING_BROWSER_INSTALLERS = setOf(
        "com.android.chrome",
        "com.google.android.apps.chrome",
        "org.mozilla.firefox",
        "org.mozilla.firefox_beta",
        "org.mozilla.fenix",
        "com.brave.browser",
        "com.sec.android.app.sbrowser",
    )

    fun detectAppSource(packageManager: PackageManager, packageName: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return detectFromInstallSourceInfo(packageManager, packageName)
        }
        @Suppress("DEPRECATION")
        mapInstaller(packageManager.getInstallerPackageName(packageName))?.let { return it }
        return APP_SOURCE_OTHER
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun detectFromInstallSourceInfo(
        packageManager: PackageManager,
        packageName: String,
    ): String {
        try {
            val sourceInfo = packageManager.getInstallSourceInfo(packageName)
            mapInstaller(sourceInfo.installingPackageName)?.let { return it }
            mapInstaller(sourceInfo.initiatingPackageName)?.let { return it }
            if (sourceInfo.initiatingPackageName in INITIATING_BROWSER_INSTALLERS) {
                return APP_SOURCE_DEV_WEBSITE
            }
            when (sourceInfo.packageSource) {
                PackageInstaller.PACKAGE_SOURCE_STORE -> return APP_SOURCE_GOOGLE_PLAY
                PackageInstaller.PACKAGE_SOURCE_LOCAL_FILE,
                PackageInstaller.PACKAGE_SOURCE_DOWNLOADED_FILE,
                -> return APP_SOURCE_DEV_WEBSITE
            }
        } catch (_: PackageManager.NameNotFoundException) {
            return APP_SOURCE_OTHER
        }
        return APP_SOURCE_OTHER
    }

    internal fun mapInstaller(installerPackage: String?): String? {
        if (installerPackage.isNullOrBlank()) {
            return null
        }
        INSTALLER_TO_APP_SOURCE[installerPackage]?.let { return it }
        return when {
            installerPackage.contains("fdroid", ignoreCase = true) -> APP_SOURCE_F_DROID
            installerPackage.contains("aurora", ignoreCase = true) -> APP_SOURCE_GOOGLE_PLAY
            installerPackage.contains("accrescent", ignoreCase = true) -> APP_SOURCE_ACCRESCENT
            installerPackage.contains("github", ignoreCase = true) -> APP_SOURCE_GITHUB
            installerPackage.contains("gitlab", ignoreCase = true) -> APP_SOURCE_GITLAB
            installerPackage.contains("codeberg", ignoreCase = true) -> APP_SOURCE_CODEBERG
            else -> null
        }
    }
}
