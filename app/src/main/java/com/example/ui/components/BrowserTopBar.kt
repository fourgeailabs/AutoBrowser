package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BrowserUiState
import com.example.ui.BrowserViewModel
import com.example.ui.SheetState

@Composable
fun BrowserTopBar(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel,
  modifier: Modifier = Modifier
) {
  val activeTab = uiState.activeTab
  var isEditingUrl by remember { mutableStateOf(false) }
  var urlText by remember(activeTab.currentUrl) { mutableStateOf(activeTab.currentUrl) }
  var menuExpanded by remember { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(isEditingUrl) {
    if (isEditingUrl) {
      urlText = activeTab.currentUrl
      focusRequester.requestFocus()
    }
  }

  val systemTopPadding = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
  val automotiveTopPadding = if (uiState.isCarMode || uiState.isAutomotiveDevice) {
    uiState.automotiveTopPaddingDp.dp
  } else {
    0.dp
  }
  val effectiveTopPadding = maxOf(systemTopPadding, automotiveTopPadding)

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        .padding(top = effectiveTopPadding)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        // Back Navigation Button
        IconButton(
          onClick = { viewModel.goBack() },
          enabled = activeTab.canGoBack,
          modifier = Modifier.size(38.dp).testTag("nav_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = if (activeTab.canGoBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            modifier = Modifier.size(20.dp)
          )
        }

        // Forward Navigation Button
        IconButton(
          onClick = { viewModel.goForward() },
          enabled = activeTab.canGoForward,
          modifier = Modifier.size(38.dp).testTag("nav_forward_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Forward",
            tint = if (activeTab.canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            modifier = Modifier.size(20.dp)
          )
        }

        // URL / Search Input Bar
        Box(
          modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { isEditingUrl = true }
            .padding(horizontal = 12.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          if (isEditingUrl) {
            TextField(
              value = urlText,
              onValueChange = { urlText = it },
              modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .testTag("url_input_field"),
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
              ),
              keyboardActions = KeyboardActions(
                onGo = {
                  isEditingUrl = false
                  focusManager.clearFocus()
                  viewModel.submitQueryOrUrl(urlText)
                }
              ),
              trailingIcon = {
                IconButton(
                  onClick = {
                    if (urlText.isNotBlank()) urlText = "" else isEditingUrl = false
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                  )
                }
              },
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
              ),
              textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
            )
          } else {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = if (activeTab.currentUrl.startsWith("https")) Icons.Default.Lock else Icons.Default.Search,
                contentDescription = "Security",
                tint = if (activeTab.currentUrl.startsWith("https")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
              )

              Text(
                text = formatDisplayUrl(activeTab.currentUrl.ifBlank { activeTab.title }),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
              )

              IconButton(
                onClick = { viewModel.reloadCurrentTab() },
                modifier = Modifier.size(28.dp).testTag("reload_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "Reload",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        // Auto / Automation Indicator & Quick Sheet Button
        val hasActiveAutomation = uiState.isAutoRefreshActive || uiState.isAutoScrollActive || uiState.isPlaylistActive
        IconButton(
          onClick = { viewModel.openSheet(SheetState.AUTOMATION) },
          modifier = Modifier.size(38.dp).testTag("automation_suite_button")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Automation Suite",
              tint = if (hasActiveAutomation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
            if (hasActiveAutomation) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .align(Alignment.TopEnd)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary)
              )
            }
          }
        }

        // In-Car Presets Toggle Button
        if (uiState.isCarMode) {
          IconButton(
            onClick = { viewModel.toggleCarPresets() },
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (uiState.isCarPresetsExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
              .testTag("car_presets_toggle_button")
          ) {
            Icon(
              imageVector = Icons.Default.ElectricCar,
              contentDescription = "In-Car Presets",
              tint = if (uiState.isCarPresetsExpanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Tabs Count Button
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { viewModel.openSheet(SheetState.TABS) }
            .testTag("tabs_counter_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = uiState.tabs.size.toString(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Overflow Menu Button
        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.size(38.dp).testTag("overflow_menu_button")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Menu",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text(if (uiState.isCarMode) "Exit Car Mode" else "In-Car Dashboard Mode") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.DirectionsCar,
                  contentDescription = null,
                  tint = if (uiState.isCarMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              onClick = {
                menuExpanded = false
                viewModel.toggleCarMode()
              }
            )

            DropdownMenuItem(
              text = { Text(if (uiState.isBookmarked) "Remove Bookmark" else "Add Bookmark") },
              leadingIcon = {
                Icon(
                  imageVector = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              },
              onClick = {
                menuExpanded = false
                viewModel.toggleBookmark()
              }
            )

            DropdownMenuItem(
              text = { Text("Bookmarks & History") },
              leadingIcon = {
                Icon(Icons.Default.History, contentDescription = null)
              },
              onClick = {
                menuExpanded = false
                viewModel.openSheet(SheetState.BOOKMARKS_HISTORY)
              }
            )

            DropdownMenuItem(
              text = { Text("Automation Suite") },
              leadingIcon = {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              onClick = {
                menuExpanded = false
                viewModel.openSheet(SheetState.AUTOMATION)
              }
            )

            DropdownMenuItem(
              text = { Text(if (activeTab.isDesktop) "Mobile View" else "Desktop Mode") },
              leadingIcon = {
                Icon(if (activeTab.isDesktop) Icons.Default.Smartphone else Icons.Default.Computer, contentDescription = null)
              },
              onClick = {
                menuExpanded = false
                viewModel.toggleDesktopMode()
              }
            )

            DropdownMenuItem(
              text = { Text("Settings") },
              leadingIcon = {
                Icon(Icons.Default.Settings, contentDescription = null)
              },
              onClick = {
                menuExpanded = false
                viewModel.openSheet(SheetState.SETTINGS)
              }
            )

            DropdownMenuItem(
              text = { Text("What's New") },
              leadingIcon = {
                Icon(Icons.Default.NewReleases, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              onClick = {
                menuExpanded = false
                viewModel.openSheet(SheetState.WHATS_NEW)
              }
            )

            DropdownMenuItem(
              text = { Text("About AutoBrowser") },
              leadingIcon = {
                Icon(Icons.Default.Info, contentDescription = null)
              },
              onClick = {
                menuExpanded = false
                viewModel.openSheet(SheetState.ABOUT)
              }
            )
          }
        }
      }

      // Page Loading Progress Indicator
      AnimatedVisibility(visible = activeTab.isLoading) {
        LinearProgressIndicator(
          progress = { activeTab.progress / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(2.5.dp),
          color = MaterialTheme.colorScheme.primary,
          trackColor = Color.Transparent
        )
      }

      // In-Car Quick Presets Expandable Bar
      AnimatedVisibility(visible = uiState.isCarMode && uiState.isCarPresetsExpanded) {
        CarQuickBar(uiState = uiState, viewModel = viewModel)
      }
    }
  }
}

private fun formatDisplayUrl(raw: String): String {
  if (raw.isBlank()) return "Search or type URL"
  return raw
    .removePrefix("https://")
    .removePrefix("http://")
    .removePrefix("www.")
}
