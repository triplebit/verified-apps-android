package org.privacyguides.verifiedapps.ui

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.LruCache

/** In-memory cache for [PackageManager] app icons loaded on demand. */
object AppIconCache {
    private const val MAX_ENTRIES = 256

    private val cache = LruCache<String, Drawable>(MAX_ENTRIES)

    fun get(packageManager: PackageManager, packageName: String): Drawable =
        cache.get(packageName)
            ?: packageManager.getApplicationIcon(packageName).also { cache.put(packageName, it) }
}
