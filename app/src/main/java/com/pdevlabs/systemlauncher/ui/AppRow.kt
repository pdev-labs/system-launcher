package com.pdevlabs.systemlauncher.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.pdevlabs.systemlauncher.data.AppEntry

/**
 * Text-first row WITH icon logo. When [showIcon] is false this is a pure
 * Last-Launcher-style text row.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppRow(
    entry: AppEntry,
    displayName: String,
    showIcon: Boolean,
    iconSizeDp: Int,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onTap, onLongClick = onLongPress)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            AppIcon(drawable = entry.icon, sizeDp = iconSizeDp, contentDesc = displayName)
            Spacer(Modifier.width(14.dp))
        }
        Text(text = displayName, fontSize = 18.sp, maxLines = 1)
    }
}

@Composable
fun AppIcon(drawable: Drawable?, sizeDp: Int, contentDesc: String) {
    val size = sizeDp.dp
    if (drawable == null) {
        Spacer(Modifier.size(size))
        return
    }
    val bitmap = remember(drawable) {
        try {
            drawable.toBitmap(
                width = with(LocalDensityProvider) { sizePx(sizeDp) },
                height = with(LocalDensityProvider) { sizePx(sizeDp) }
            ).asImageBitmap()
        } catch (_: Exception) { null }
    }
    if (bitmap != null) {
        Image(bitmap = bitmap, contentDescription = contentDesc, modifier = Modifier.size(size))
    } else {
        Spacer(Modifier.size(size))
    }
}

// Small helper to avoid LocalDensity capture inside remember.
private object LocalDensityProvider {
    fun sizePx(dp: Int): Int = (dp * 2.5).toInt().coerceIn(64, 192)
}
