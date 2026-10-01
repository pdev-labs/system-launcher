package com.pdevlabs.systemlauncher.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdevlabs.systemlauncher.data.AppEntry

/**
 * Nova-style grid cell: icon logo on top, label below.
 * Respects showLabels + icon size prefs.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppGridIcon(
    entry: AppEntry,
    displayName: String,
    showIcon: Boolean,
    showLabel: Boolean,
    iconSizeDp: Int,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(84.dp)
            .combinedClickable(onClick = onTap, onLongClick = onLongPress)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showIcon) {
            AppIcon(drawable = entry.icon, sizeDp = iconSizeDp, contentDesc = displayName)
        } else {
            // Text-only fallback keeps Last Launcher spirit even in grid mode.
            Text("•", fontSize = 28.sp)
        }
        if (showLabel) {
            Text(
                text = displayName,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderGridIcon(
    name: String,
    count: Int,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(84.dp)
            .combinedClickable(onClick = onTap, onLongClick = onLongPress)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📁", fontSize = 40.sp)
        Text(
            text = "$name ($count)",
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
