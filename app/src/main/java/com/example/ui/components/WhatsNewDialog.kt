package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BrowserUiState
import com.example.ui.BrowserViewModel

data class ReleaseUpdate(
  val version: String,
  val buildNumber: Int,
  val releaseDate: String,
  val isCurrent: Boolean,
  val headline: String,
  val changes: List<String>
)

val RELEASE_HISTORY = listOf(
  ReleaseUpdate(
    version = "2.03.00",
    buildNumber = 5,
    releaseDate = "September 2026",
    isCurrent = true,
    headline = "Chevrolet Equinox EV Notification Bar Clearance & System Top Inset Support",
    changes = listOf(
      "Positioned the browser header and all interactive controls safely below the Equinox EV notification bar and system top bar.",
      "Added intelligent top inset handling combining WindowInsets.safeDrawing with a dedicated automotive display offset.",
      "Configured 64dp default clearance for the Equinox EV 17.7-inch infotainment touchscreen with full zero-obscuration layout.",
      "Added customizable Notification Bar Clearance controls in Settings (Auto, 48dp, 56dp, 64dp, 72dp, 80dp, 96dp).",
      "Added one-tap top clearance cycle badge directly to the In-Car Quick Bar for immediate on-road adjustments."
    )
  ),
  ReleaseUpdate(
    version = "2.02.00",
    buildNumber = 4,
    releaseDate = "September 2026",
    isCurrent = false,
    headline = "Google Play Automotive Compliance & Manifest Synchronization",
    changes = listOf(
      "Resolved Play Console publication restriction by removing redundant Android Auto projected metadata.",
      "Maintained native Android Automotive OS (AAOS) standalone compatibility with android.hardware.type.automotive.",
      "Bumped versionCode to 4 to ensure smooth upgrade progression over previous Play Console release.",
      "Preserved distraction-optimized in-drive testing capabilities and parked mode safety features."
    )
  ),
  ReleaseUpdate(
    version = "2.01.00",
    buildNumber = 3,
    releaseDate = "September 2026",
    isCurrent = false,
    headline = "Drive Testing Mode & Distraction Optimization for In-Motion Testing",
    changes = listOf(
      "Added Drive Testing Mode toggle allowing uninterrupted web browsing, live dashboard monitoring, and auto-scrolling while in Drive.",
      "Added distractionOptimized manifest meta-data flag allowing Android Automotive OS to run the activity continuously in motion.",
      "Auto-scroller and URL playlist cycles remain active without being auto-paused when in Drive testing mode.",
      "Added quick Drive Testing toggle badge directly inside the In-Car Presets toolbar for rapid status switching.",
      "Added Drive Testing Mode controls inside Settings dialog under Android Automotive & In-Car Mode.",
      "Safety lockout overlay now includes one-tap 'Enable Drive Testing Mode' bypass action for streamlined road trials."
    )
  ),
  ReleaseUpdate(
    version = "2.00.00",
    buildNumber = 2,
    releaseDate = "September 2026",
    isCurrent = false,
    headline = "Android Automotive OS Compatibility & In-Car Parked Mode",
    changes = listOf(
      "Full Android Automotive OS (AAOS) platform compatibility with hardware descriptors and automotive manifest declarations.",
      "In-Car Parked Browsing Mode adhering strictly to Google Play Automotive driver distraction guidelines.",
      "Driver Distraction Safety Lockout that automatically halts web browsing and video playback while vehicle is in motion.",
      "In-Car Quick Presets bar providing instant one-tap access to EV Charging (PlugShare), Live Radar (Windy), Traffic, Radio, and GitHub.",
      "Dashboard-optimized controls with oversized touch targets (56dp+) and high contrast designed for center console displays.",
      "Rotary controller, D-pad, and touch input support for vehicle head units.",
      "In-app Automotive simulation switch in Settings for multi-device testing on phones, tablets, and emulators."
    )
  ),
  ReleaseUpdate(
    version = "1.00.00",
    buildNumber = 1,
    releaseDate = "September 2026",
    isCurrent = false,
    headline = "Initial Official Release with Complete Automation Suite",
    changes = listOf(
      "Intelligent Auto-Refresh engine with preset chips (5s, 10s, 15s, 30s, 60s, 2m, 5m) and countdown indicator.",
      "Hands-Free Auto-Scroller with 10-level speed increments, bidirectional scrolling, and touch-pause safety.",
      "Multi-URL Playlist Cycling for continuous kiosk and multi-dashboard rotation loops.",
      "Preloaded script automations: Force High-Contrast Dark Mode, Sticky Overlay Cleaner, and Reader Clean View.",
      "Custom JavaScript executor console with live output logs and inspection.",
      "Multi-tab browsing with quick grid manager, desktop mode toggle, and search engine selection.",
      "SQLite Room persistence for bookmarks, browsing history, and automated playlist presets.",
      "Dedicated What's New accordion release log and FourgeAI LABS About section."
    )
  ),
  ReleaseUpdate(
    version = "0.90.00",
    buildNumber = 0,
    releaseDate = "August 2026",
    isCurrent = false,
    headline = "Beta Architecture & Core Engine Preview",
    changes = listOf(
      "Initial hardware-accelerated WebKit WebView container integration.",
      "Basic URL bar navigation and SSL lock certificate indicators.",
      "Foundational background coroutine timing loops for auto-reloading.",
      "Theme styling setup with Material 3 dynamic color scheme foundation."
    )
  )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsNewDialog(
  uiState: BrowserUiState,
  viewModel: BrowserViewModel,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("whats_new_bottom_sheet")
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
        Icon(
          imageVector = Icons.Default.NewReleases,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(26.dp)
        )
        Column {
          Text(
            text = "What's New",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Version history and release notes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        itemsIndexed(RELEASE_HISTORY) { index, update ->
          val isExpanded = uiState.whatsNewExpandedIndex == index

          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
              width = if (update.isCurrent) 1.5.dp else 1.dp,
              color = if (update.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .clickable { viewModel.toggleWhatsNewAccordion(index) }
              .testTag("whats_new_item_${update.version}")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text(
                      text = "Version ${update.version}",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )

                    if (update.isCurrent) {
                      Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                      ) {
                        Text("Current", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                      }
                    }
                  }

                  Text(
                    text = "${update.releaseDate} • ${update.headline}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                  )
                }

                Icon(
                  imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                  contentDescription = if (isExpanded) "Collapse" else "Expand",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(24.dp)
                )
              }

              // Accordion Dropdown Content
              AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                ) {
                  HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(bottom = 10.dp)
                  )

                  update.changes.forEach { change ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                      verticalAlignment = Alignment.Top,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                          .size(16.dp)
                          .padding(top = 2.dp)
                      )
                      Text(
                        text = change,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
