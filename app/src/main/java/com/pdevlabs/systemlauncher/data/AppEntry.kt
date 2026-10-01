package com.pdevlabs.systemlauncher.data

import android.graphics.drawable.Drawable

/** One launchable app. Icon is loaded lazily and cached — never on the UI thread. */
data class AppEntry(
    val packageName: String,
    val className: String,
    val label: String,
    val icon: Drawable? = null
) {
    val key: String get() = "$packageName/$className"
    fun launchIntentLabel(alias: String?): String = alias?.takeIf { it.isNotBlank() } ?: label
}
