package com.pdevlabs.systemlauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Queries launchable apps and caches icons.
 * Inspired by Last Launcher (text-first) but WITH icon logos as requested.
 */
class AppRepository(private val context: Context) {

    // ~100 icons is plenty; each adaptive icon is small once cached as Drawable.
    private val iconCache = object : LruCache<String, Drawable>(100) {}

    suspend fun loadApps(): List<AppEntry> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val infos = pm.queryIntentActivities(intent, 0)
        infos.mapNotNull { ri ->
            val pkg = ri.activityInfo.packageName ?: return@mapNotNull null
            val cls = ri.activityInfo.name ?: return@mapNotNull null
            // Skip ourselves to avoid relaunch loop confusion (still launchable via settings).
            val label = ri.loadLabel(pm)?.toString() ?: pkg
            val icon = getIcon(pkg, ri.activityInfo.applicationInfo?.uid ?: pkg.hashCode()) {
                try { ri.loadIcon(pm) } catch (_: Exception) { null }
            }
            AppEntry(pkg, cls, label, icon)
        }.sortedBy { it.label.lowercase() }
    }

    private fun getIcon(key: String, @Suppress("UNUSED_PARAMETER") uid: Int, loader: () -> Drawable?): Drawable? {
        // NOTE: keyed by packageName; class-level icons share the app icon which is what we want.
        iconCache.get(key)?.let { return it }
        val d = loader() ?: return null
        // ConstantState.newDrawable() avoids sharing mutable state across rows.
        val safe = d.constantState?.newDrawable()?.mutate() ?: d
        iconCache.put(key, safe)
        return safe
    }

    fun launch(packageName: String, className: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setClassName(packageName, className)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun appInfoIntent(packageName: String): Intent =
        Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(android.net.Uri.parse("package:$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    fun uninstallIntent(packageName: String): Intent =
        Intent(Intent.ACTION_DELETE)
            .setData(android.net.Uri.parse("package:$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    fun clearIconCache() = iconCache.evictAll()

    companion object {
        fun fresh(context: Context): AppRepository = AppRepository(context.applicationContext)
    }
}

// Unused import guard for_pm direct access in UI previews
@Suppress("unused")
private fun PackageManager.iconFor(pkg: String): Drawable? =
    try { getApplicationIcon(pkg) } catch (_: Exception) { null }
