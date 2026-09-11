package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultAutomations
import com.example.ui.BrowserUiState
import com.example.ui.BrowserViewModel
import com.example.ui.ScrollDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationSheet(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Auto-Refresh", "Auto-Scroll", "URL Playlist", "Scripts & JS")

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("automation_suite_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.85f)
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
        Column {
          Text(
            text = "Automation Suite",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Configure hands-free routines and automated workflows",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      PrimaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      when (selectedTab) {
        0 -> AutoRefreshSection(uiState, viewModel)
        1 -> AutoScrollSection(uiState, viewModel)
        2 -> PlaylistSection(uiState, viewModel)
        3 -> ScriptsSection(uiState, viewModel)
      }
    }
  }
}

@Composable
private fun AutoRefreshSection(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel
) {
  val intervals = listOf(5, 10, 15, 30, 60, 120, 300)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Text(
      text = "Reload Frequency Preset",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      intervals.take(4).forEach { sec ->
        FilterChip(
          selected = uiState.refreshIntervalSeconds == sec,
          onClick = { viewModel.setRefreshInterval(sec) },
          label = { Text("${sec}s") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          )
        )
      }
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      intervals.drop(4).forEach { sec ->
        val label = if (sec >= 60) "${sec / 60}m" else "${sec}s"
        FilterChip(
          selected = uiState.refreshIntervalSeconds == sec,
          onClick = { viewModel.setRefreshInterval(sec) },
          label = { Text(label) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Current Status",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = if (uiState.isAutoRefreshActive) "RUNNING (${uiState.refreshRemainingSeconds}s remaining)" else "IDLE",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (uiState.isAutoRefreshActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = { viewModel.toggleAutoRefresh() },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (uiState.isAutoRefreshActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("toggle_auto_refresh_button")
        ) {
          Icon(
            imageVector = if (uiState.isAutoRefreshActive) Icons.Default.Stop else Icons.Default.Refresh,
            contentDescription = null
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(if (uiState.isAutoRefreshActive) "Stop Auto-Refresh" else "Start Auto-Refresh (Every ${uiState.refreshIntervalSeconds}s)")
        }
      }
    }
  }
}

@Composable
private fun AutoScrollSection(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Text(
      text = "Scroll Speed: Level ${uiState.scrollSpeed}",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Slider(
      value = uiState.scrollSpeed.toFloat(),
      onValueChange = { viewModel.setScrollSpeed(it.toInt()) },
      valueRange = 1f..10f,
      steps = 8,
      modifier = Modifier.fillMaxWidth().testTag("scroll_speed_slider")
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("1 (Gentle Reading)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text("10 (Fast Scan)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Scroll Direction",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      FilterChip(
        selected = uiState.scrollDirection == ScrollDirection.DOWN,
        onClick = { viewModel.setScrollDirection(ScrollDirection.DOWN) },
        leadingIcon = { Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp)) },
        label = { Text("Scroll Down") }
      )
      FilterChip(
        selected = uiState.scrollDirection == ScrollDirection.UP,
        onClick = { viewModel.setScrollDirection(ScrollDirection.UP) },
        leadingIcon = { Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp)) },
        label = { Text("Scroll Up") }
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = { viewModel.toggleAutoScroll() },
      colors = ButtonDefaults.buttonColors(
        containerColor = if (uiState.isAutoScrollActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
      ),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("toggle_auto_scroll_button")
    ) {
      Icon(
        imageVector = if (uiState.isAutoScrollActive) Icons.Default.Stop else Icons.Default.PlayArrow,
        contentDescription = null
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(if (uiState.isAutoScrollActive) "Stop Auto-Scroll" else "Start Auto-Scroll")
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "💡 Tip: Touching the screen will automatically pause scrolling until released.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun PlaylistSection(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel
) {
  var newTitle by remember { mutableStateOf("") }
  var newUrl by remember { mutableStateOf("") }
  var newDuration by remember { mutableStateOf("15") }

  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "URL Rotation Loop (${uiState.playlistItems.size} sites)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Button(
          onClick = { viewModel.togglePlaylistCycling() },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (uiState.isPlaylistActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(if (uiState.isPlaylistActive) Icons.Default.Stop else Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (uiState.isPlaylistActive) "Stop" else "Start Loop")
        }
      }
    }

    items(uiState.playlistItems, key = { it.id }) { item ->
      val isCurrent = uiState.isPlaylistActive && uiState.playlistItems.getOrNull(uiState.currentPlaylistIndex)?.id == item.id
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.title,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${item.url} • ${item.durationSeconds}s display",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(onClick = { viewModel.removePlaylistItem(item.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Add Site to Playlist",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))

      OutlinedTextField(
        value = newTitle,
        onValueChange = { newTitle = it },
        label = { Text("Site Name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(4.dp))

      OutlinedTextField(
        value = newUrl,
        onValueChange = { newUrl = it },
        label = { Text("URL (e.g. https://github.com)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = newDuration,
          onValueChange = { newDuration = it },
          label = { Text("Duration (seconds)") },
          singleLine = true,
          modifier = Modifier.weight(1f)
        )

        Button(
          onClick = {
            if (newUrl.isNotBlank()) {
              val sec = newDuration.toIntOrNull() ?: 15
              viewModel.addPlaylistItem(newTitle, newUrl, sec)
              newTitle = ""
              newUrl = ""
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add")
        }
      }
    }
  }
}

@Composable
private fun ScriptsSection(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel
) {
  var customScript by remember { mutableStateOf(uiState.customScript) }

  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "Instant Quick Automations",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Tap any preset to run directly on the active webpage",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    items(DefaultAutomations.list) { preset ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.executeScript(preset.script, preset.title) }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            val icon = getPresetIcon(preset.iconName)
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(20.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = preset.title,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = preset.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Run",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Custom JavaScript Console",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )

      OutlinedTextField(
        value = customScript,
        onValueChange = {
          customScript = it
          viewModel.setCustomScript(it)
        },
        label = { Text("JavaScript snippet") },
        placeholder = { Text("document.title or custom JS") },
        modifier = Modifier
          .fillMaxWidth()
          .height(90.dp),
        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Button(
          onClick = {
            if (customScript.isNotBlank()) {
              viewModel.executeScript(customScript, "Custom Script")
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Run Code")
        }

        if (uiState.scriptLogs.isNotEmpty()) {
          Button(
            onClick = { viewModel.clearScriptLogs() },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Clear Console")
          }
        }
      }
    }

    if (uiState.scriptLogs.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Execution Output Logs",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      items(uiState.scriptLogs, key = { it.id }) { log ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = log.scriptTitle,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = log.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = log.output,
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }
  }
}

private fun getPresetIcon(name: String): ImageVector {
  return when (name) {
    "dark_mode" -> Icons.Default.DarkMode
    "cleaning_services" -> Icons.Default.CleaningServices
    "menu_book" -> Icons.Default.MenuBook
    "link" -> Icons.Default.Link
    "vertical_align_top" -> Icons.Default.VerticalAlignTop
    "vertical_align_bottom" -> Icons.Default.VerticalAlignBottom
    else -> Icons.Default.AutoAwesome
  }
}
