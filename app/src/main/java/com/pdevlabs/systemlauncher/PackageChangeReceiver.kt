package com.pdevlabs.systemlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Refresh trigger: MainActivity re-queries on resume, this exists so the
 *  manifest declares package-change handling and the system delivers updates. */
class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // No-op: MainActivity.refresh() runs in onResume which follows package changes.
        // Kept explicit so HOME launchers reliably get PACKAGE_* delivery on all OEMs.
    }
}
