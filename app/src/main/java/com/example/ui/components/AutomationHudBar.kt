package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BrowserUiState
import com.example.ui.BrowserViewModel
import com.example.ui.ScrollDirection

@Composable
fun AutomationHudBar(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel,
  modifier: Modifier = Modifier
) {
  val hasActive = uiState.isAutoRefreshActive || uiState.isAutoScrollActive || uiState.isPlaylistActive

  AnimatedVisibility(
    visible = hasActive,
    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    modifier = modifier
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f),
      tonalElevation = 6.dp,
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .testTag("automation_hud_surface")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // --- AUTO-REFRESH HUD ITEM ---
        if (uiState.isAutoRefreshActive) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              val progress = (uiState.refreshRemainingSeconds.toFloat() / uiState.refreshIntervalSeconds.toFloat()).coerceIn(0f, 1f)
              CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(28.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              )
              Text(
                text = "${uiState.refreshRemainingSeconds}s",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Text(
              text = "Auto-Refresh",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
              onClick = { viewModel.reloadCurrentTab() },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Now",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
            }

            IconButton(
              onClick = { viewModel.stopAutoRefresh() },
              modifier = Modifier.size(32.dp).testTag("stop_auto_refresh_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Stop Auto-Refresh",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        // --- AUTO-SCROLL HUD ITEM ---
        if (uiState.isAutoScrollActive) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (uiState.scrollDirection == ScrollDirection.DOWN) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = "Scroll Direction",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp)
              )
            }

            Text(
              text = "Spd ${uiState.scrollSpeed}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            // Speed up / down toggle
            IconButton(
              onClick = {
                val nextSpeed = if (uiState.scrollSpeed >= 10) 1 else uiState.scrollSpeed + 2
                viewModel.setScrollSpeed(nextSpeed)
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = "Change Speed",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp)
              )
            }

            // Direction reverse
            IconButton(
              onClick = {
                val nextDir = if (uiState.scrollDirection == ScrollDirection.DOWN) ScrollDirection.UP else ScrollDirection.DOWN
                viewModel.setScrollDirection(nextDir)
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = if (uiState.scrollDirection == ScrollDirection.DOWN) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = "Reverse Direction",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
            }

            // Pause / Resume
            IconButton(
              onClick = { viewModel.setScrollPaused(!uiState.isScrollPaused) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = if (uiState.isScrollPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = if (uiState.isScrollPaused) "Resume" else "Pause",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }

            IconButton(
              onClick = { viewModel.stopAutoScroll() },
              modifier = Modifier.size(32.dp).testTag("stop_auto_scroll_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Stop Auto-Scroll",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        // --- PLAYLIST HUD ITEM ---
        if (uiState.isPlaylistActive) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val total = uiState.playlistItems.size
            val current = (uiState.currentPlaylistIndex + 1).coerceAtMost(total)

            Text(
              text = "Loop $current/$total (${uiState.playlistRemainingSeconds}s)",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )

            IconButton(
              onClick = { viewModel.advancePlaylist(forward = true) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next Site",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
              )
            }

            IconButton(
              onClick = { viewModel.stopPlaylistCycling() },
              modifier = Modifier.size(32.dp).testTag("stop_playlist_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Stop Playlist",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}
