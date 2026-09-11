package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.BrowserUiState
import com.example.ui.BrowserViewModel
import com.example.ui.SheetState
import com.example.ui.components.AboutDialog
import com.example.ui.components.AutomotiveSafetyOverlay
import com.example.ui.components.AutomationHudBar
import com.example.ui.components.AutomationSheet
import com.example.ui.components.BookmarksHistoryDialog
import com.example.ui.components.BrowserTopBar
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TabsSheet
import com.example.ui.components.WebViewContainer
import com.example.ui.components.WhatsNewDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: BrowserViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AutoBrowserApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun AutoBrowserApp(
  viewModel: BrowserViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val activeTab = uiState.activeTab

  // Intelligent back navigation handler
  BackHandler(enabled = true) {
    when {
      uiState.activeSheet != SheetState.NONE -> viewModel.closeSheet()
      activeTab.canGoBack -> viewModel.goBack()
      uiState.tabs.size > 1 -> viewModel.closeTab(activeTab.id)
      else -> {
        // Allow standard system back
      }
    }
  }

  Scaffold(
    topBar = {
      BrowserTopBar(
        uiState = uiState,
        viewModel = viewModel
      )
    },
    modifier = modifier.fillMaxSize().testTag("autobrowser_root_scaffold")
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Main Web Engine Viewport
      WebViewContainer(
        activeTab = activeTab,
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
      )

      // Floating Live Automation HUD
      AutomationHudBar(
        uiState = uiState,
        viewModel = viewModel,
        modifier = Modifier.align(Alignment.BottomCenter)
      )

      // Automotive OS Distraction Safety Lockout (active only if drive testing is disabled)
      if (uiState.isCarMode && !uiState.isVehicleParked && !uiState.isDriveTestingEnabled) {
        AutomotiveSafetyOverlay(
          viewModel = viewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }

    // Modal Sheets & Navigation Dialogs
    when (uiState.activeSheet) {
      SheetState.TABS -> {
        TabsSheet(
          uiState = uiState,
          viewModel = viewModel,
          onDismiss = { viewModel.closeSheet() }
        )
      }
      SheetState.AUTOMATION -> {
        AutomationSheet(
          uiState = uiState,
          viewModel = viewModel,
          onDismiss = { viewModel.closeSheet() }
        )
      }
      SheetState.BOOKMARKS_HISTORY -> {
        BookmarksHistoryDialog(
          uiState = uiState,
          viewModel = viewModel,
          onDismiss = { viewModel.closeSheet() }
        )
      }
      SheetState.SETTINGS -> {
        SettingsDialog(
          uiState = uiState,
          viewModel = viewModel,
          onDismiss = { viewModel.closeSheet() }
        )
      }
      SheetState.WHATS_NEW -> {
        WhatsNewDialog(
          uiState = uiState,
          viewModel = viewModel,
          onDismiss = { viewModel.closeSheet() }
        )
      }
      SheetState.ABOUT -> {
        AboutDialog(
          uiState = uiState,
          viewModel = viewModel,
          onDismiss = { viewModel.closeSheet() }
        )
      }
      SheetState.NONE -> {
        // No dialog active
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "AutoBrowser $name", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
