package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BrowserRepository
import com.example.data.local.AppDatabase
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import com.example.data.model.PlaylistItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class SheetState {
  NONE,
  TABS,
  AUTOMATION,
  BOOKMARKS_HISTORY,
  SETTINGS,
  WHATS_NEW,
  ABOUT
}

enum class ScrollDirection {
  DOWN,
  UP
}

enum class SearchEngine(val displayName: String, val searchUrl: String) {
  GOOGLE("Google", "https://www.google.com/search?q="),
  DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q="),
  BING("Bing", "https://www.bing.com/search?q="),
  ECOSIA("Ecosia", "https://www.ecosia.org/search?q=")
}

data class TabState(
  val id: String = UUID.randomUUID().toString(),
  val title: String = "New Tab",
  val url: String = "https://www.google.com",
  val currentUrl: String = "https://www.google.com",
  val isLoading: Boolean = false,
  val progress: Int = 0,
  val canGoBack: Boolean = false,
  val canGoForward: Boolean = false,
  val isDesktop: Boolean = false
)

data class ScriptLog(
  val id: String = UUID.randomUUID().toString(),
  val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
  val scriptTitle: String,
  val output: String,
  val isError: Boolean = false
)

sealed interface WebViewCommand {
  data object GoBack : WebViewCommand
  data object GoForward : WebViewCommand
  data object Reload : WebViewCommand
  data object Stop : WebViewCommand
  data class LoadUrl(val url: String) : WebViewCommand
  data class EvaluateJs(val script: String, val callbackTitle: String) : WebViewCommand
  data class ScrollBy(val yPixels: Int) : WebViewCommand
  data class SetDesktop(val enabled: Boolean) : WebViewCommand
}

data class CarPreset(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val subtitle: String,
  val url: String,
  val category: String
)

data class BrowserUiState(
  val tabs: List<TabState> = listOf(TabState()),
  val activeTabId: String = "",
  val activeSheet: SheetState = SheetState.NONE,
  val searchEngine: SearchEngine = SearchEngine.GOOGLE,
  val isBookmarked: Boolean = false,

  // Android Automotive OS & In-Car Parked Mode
  val isAutomotiveDevice: Boolean = false,
  val isCarMode: Boolean = false,
  val isVehicleParked: Boolean = true,
  val isDriveTestingEnabled: Boolean = true, // Enabled for testing while in drive
  val automotiveTopPaddingDp: Int = 64, // Default 64dp for Chevrolet Equinox EV notification bar clearance
  val isCarPresetsExpanded: Boolean = false,
  val carPresets: List<CarPreset> = listOf(
    CarPreset(title = "PlugShare EV", subtitle = "Find charging stations & plugs", url = "https://www.plugshare.com", category = "Charging"),
    CarPreset(title = "Windy Radar", subtitle = "Live weather radar & forecasts", url = "https://www.windy.com", category = "Weather"),
    CarPreset(title = "Google Maps", subtitle = "Live traffic & route navigation", url = "https://www.google.com/maps", category = "Traffic"),
    CarPreset(title = "Radio Garden", subtitle = "Global in-car radio stream tuner", url = "https://radio.garden", category = "Audio"),
    CarPreset(title = "Google News", subtitle = "Live headlines & road news", url = "https://news.google.com", category = "News"),
    CarPreset(title = "FourgeAI LABS", subtitle = "Creator GitHub repository & tools", url = "https://github.com/fourgeailabs", category = "Developer")
  ),

  // Auto-Refresh
  val isAutoRefreshActive: Boolean = false,
  val refreshIntervalSeconds: Int = 15,
  val refreshRemainingSeconds: Int = 15,

  // Auto-Scroll
  val isAutoScrollActive: Boolean = false,
  val scrollSpeed: Int = 3,
  val scrollDirection: ScrollDirection = ScrollDirection.DOWN,
  val isScrollPaused: Boolean = false,

  // Playlist Cycling
  val isPlaylistActive: Boolean = false,
  val playlistItems: List<PlaylistItem> = listOf(
    PlaylistItem(title = "Hacker News", url = "https://news.ycombinator.com", durationSeconds = 12),
    PlaylistItem(title = "Wikipedia Featured", url = "https://en.wikipedia.org/wiki/Main_Page", durationSeconds = 15),
    PlaylistItem(title = "GitHub Trending", url = "https://github.com/trending", durationSeconds = 15)
  ),
  val currentPlaylistIndex: Int = 0,
  val playlistRemainingSeconds: Int = 15,
  val isPlaylistLooping: Boolean = true,

  // Scripts
  val scriptLogs: List<ScriptLog> = emptyList(),
  val customScript: String = "",

  // What's New accordion state
  val whatsNewExpandedIndex: Int? = null,

  // Bookmarks & History
  val bookmarksList: List<BookmarkEntity> = emptyList(),
  val historyList: List<HistoryEntity> = emptyList(),
  val bookmarksTabSelected: Int = 0 // 0: Bookmarks, 1: History
) {
  val activeTab: TabState
    get() = tabs.find { it.id == activeTabId } ?: tabs.firstOrNull() ?: TabState()
}

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: BrowserRepository

  private val _uiState = MutableStateFlow(BrowserUiState())
  val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

  private val _commandFlow = MutableSharedFlow<WebViewCommand>(extraBufferCapacity = 16)
  val commandFlow: SharedFlow<WebViewCommand> = _commandFlow.asSharedFlow()

  private var autoRefreshJob: Job? = null
  private var autoScrollJob: Job? = null
  private var playlistJob: Job? = null

  init {
    val database = AppDatabase.getDatabase(application)
    repository = BrowserRepository(database.browserDao())

    val initialTab = TabState(id = UUID.randomUUID().toString())
    _uiState.update { it.copy(tabs = listOf(initialTab), activeTabId = initialTab.id) }

    viewModelScope.launch {
      repository.bookmarks.collect { list ->
        _uiState.update { it.copy(bookmarksList = list) }
        checkActiveBookmark()
      }
    }

    viewModelScope.launch {
      repository.history.collect { list ->
        _uiState.update { it.copy(historyList = list) }
      }
    }

    // Detect Android Automotive OS or Car UI Mode
    val packageManager = application.packageManager
    val hasAutomotiveFeature = packageManager.hasSystemFeature("android.hardware.type.automotive")
    val uiMode = application.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK
    val isCarModeUi = uiMode == android.content.res.Configuration.UI_MODE_TYPE_CAR
    val isAuto = hasAutomotiveFeature || isCarModeUi
    _uiState.update {
      it.copy(
        isAutomotiveDevice = isAuto,
        isCarMode = isAuto // Default to Car Mode on automotive head units
      )
    }
  }

  // --- TAB MANAGEMENT ---

  fun openNewTab(url: String = "https://www.google.com") {
    val newTab = TabState(id = UUID.randomUUID().toString(), url = url, currentUrl = url)
    _uiState.update {
      it.copy(
        tabs = it.tabs + newTab,
        activeTabId = newTab.id,
        activeSheet = SheetState.NONE
      )
    }
    _commandFlow.tryEmit(WebViewCommand.LoadUrl(url))
    checkActiveBookmark()
  }

  fun switchTab(tabId: String) {
    _uiState.update { it.copy(activeTabId = tabId, activeSheet = SheetState.NONE) }
    checkActiveBookmark()
  }

  fun closeTab(tabId: String) {
    val currentTabs = _uiState.value.tabs
    if (currentTabs.size <= 1) {
      // Don't close last tab, just reset to home
      val resetTab = TabState(id = UUID.randomUUID().toString())
      _uiState.update { it.copy(tabs = listOf(resetTab), activeTabId = resetTab.id) }
      _commandFlow.tryEmit(WebViewCommand.LoadUrl(resetTab.url))
      return
    }

    val newTabs = currentTabs.filter { it.id != tabId }
    val newActiveId = if (_uiState.value.activeTabId == tabId) {
      newTabs.last().id
    } else {
      _uiState.value.activeTabId
    }

    _uiState.update { it.copy(tabs = newTabs, activeTabId = newActiveId) }
    checkActiveBookmark()
  }

  // --- NAVIGATION ---

  fun submitQueryOrUrl(input: String) {
    val trimmed = input.trim()
    if (trimmed.isBlank()) return

    val resolvedUrl = resolveUrl(trimmed, _uiState.value.searchEngine)
    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == state.activeTabId) tab.copy(url = resolvedUrl) else tab
      }
      state.copy(tabs = updatedTabs)
    }
    _commandFlow.tryEmit(WebViewCommand.LoadUrl(resolvedUrl))
  }

  private fun resolveUrl(input: String, engine: SearchEngine): String {
    return when {
      input.startsWith("http://") || input.startsWith("https://") -> input
      input.startsWith("file://") || input.startsWith("about:") -> input
      input.contains(" ") -> "${engine.searchUrl}${input.replace(" ", "+")}"
      input.contains(".") && !input.contains(" ") -> "https://$input"
      else -> "${engine.searchUrl}${input.replace(" ", "+")}"
    }
  }

  fun goBack() = _commandFlow.tryEmit(WebViewCommand.GoBack)
  fun goForward() = _commandFlow.tryEmit(WebViewCommand.GoForward)
  fun reloadCurrentTab() = _commandFlow.tryEmit(WebViewCommand.Reload)
  fun stopCurrentTab() = _commandFlow.tryEmit(WebViewCommand.Stop)

  fun goHome() {
    submitQueryOrUrl("https://www.google.com")
  }

  fun updateTabNavState(tabId: String, currentUrl: String, title: String?, canBack: Boolean, canForward: Boolean) {
    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == tabId) {
          tab.copy(
            currentUrl = currentUrl,
            title = title ?: tab.title,
            canGoBack = canBack,
            canGoForward = canForward
          )
        } else tab
      }
      state.copy(tabs = updatedTabs)
    }

    if (tabId == _uiState.value.activeTabId) {
      viewModelScope.launch {
        repository.recordHistory(title ?: currentUrl, currentUrl)
      }
      checkActiveBookmark()
    }
  }

  fun updateTabLoading(tabId: String, isLoading: Boolean, progress: Int) {
    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == tabId) {
          tab.copy(isLoading = isLoading, progress = progress)
        } else tab
      }
      state.copy(tabs = updatedTabs)
    }
  }

  fun toggleDesktopMode() {
    val currentMode = _uiState.value.activeTab.isDesktop
    val newMode = !currentMode
    _uiState.update { state ->
      val updatedTabs = state.tabs.map { tab ->
        if (tab.id == state.activeTabId) tab.copy(isDesktop = newMode) else tab
      }
      state.copy(tabs = updatedTabs)
    }
    _commandFlow.tryEmit(WebViewCommand.SetDesktop(newMode))
  }

  // --- AUTO-REFRESH ENGINE ---

  fun setRefreshInterval(seconds: Int) {
    _uiState.update { it.copy(refreshIntervalSeconds = seconds, refreshRemainingSeconds = seconds) }
    if (_uiState.value.isAutoRefreshActive) {
      startAutoRefresh()
    }
  }

  fun toggleAutoRefresh() {
    if (_uiState.value.isAutoRefreshActive) {
      stopAutoRefresh()
    } else {
      startAutoRefresh()
    }
  }

  private fun startAutoRefresh() {
    autoRefreshJob?.cancel()
    _uiState.update {
      it.copy(
        isAutoRefreshActive = true,
        refreshRemainingSeconds = it.refreshIntervalSeconds
      )
    }

    autoRefreshJob = viewModelScope.launch {
      while (_uiState.value.isAutoRefreshActive) {
        delay(1000)
        val remaining = _uiState.value.refreshRemainingSeconds - 1
        if (remaining <= 0) {
          reloadCurrentTab()
          _uiState.update { it.copy(refreshRemainingSeconds = it.refreshIntervalSeconds) }
        } else {
          _uiState.update { it.copy(refreshRemainingSeconds = remaining) }
        }
      }
    }
  }

  fun stopAutoRefresh() {
    autoRefreshJob?.cancel()
    autoRefreshJob = null
    _uiState.update { it.copy(isAutoRefreshActive = false) }
  }

  // --- AUTO-SCROLL ENGINE ---

  fun toggleAutoScroll() {
    if (_uiState.value.isAutoScrollActive) {
      stopAutoScroll()
    } else {
      startAutoScroll()
    }
  }

  fun setScrollSpeed(speed: Int) {
    _uiState.update { it.copy(scrollSpeed = speed.coerceIn(1, 10)) }
  }

  fun setScrollDirection(direction: ScrollDirection) {
    _uiState.update { it.copy(scrollDirection = direction) }
  }

  fun setScrollPaused(isPaused: Boolean) {
    _uiState.update { it.copy(isScrollPaused = isPaused) }
  }

  private fun startAutoScroll() {
    autoScrollJob?.cancel()
    _uiState.update { it.copy(isAutoScrollActive = true, isScrollPaused = false) }

    autoScrollJob = viewModelScope.launch {
      while (_uiState.value.isAutoScrollActive) {
        val speed = _uiState.value.scrollSpeed
        val intervalMs = (60 - (speed * 4L)).coerceAtLeast(15L)
        delay(intervalMs)

        if (!_uiState.value.isScrollPaused) {
          val step = (speed * 2)
          val pixelStep = if (_uiState.value.scrollDirection == ScrollDirection.DOWN) step else -step
          _commandFlow.tryEmit(WebViewCommand.ScrollBy(pixelStep))
        }
      }
    }
  }

  fun stopAutoScroll() {
    autoScrollJob?.cancel()
    autoScrollJob = null
    _uiState.update { it.copy(isAutoScrollActive = false, isScrollPaused = false) }
  }

  // --- URL PLAYLIST CYCLING ENGINE ---

  fun addPlaylistItem(title: String, url: String, duration: Int) {
    val newItem = PlaylistItem(
      title = title.ifBlank { url },
      url = if (url.startsWith("http")) url else "https://$url",
      durationSeconds = duration.coerceAtLeast(3)
    )
    _uiState.update { it.copy(playlistItems = it.playlistItems + newItem) }
  }

  fun removePlaylistItem(id: String) {
    _uiState.update { it.copy(playlistItems = it.playlistItems.filter { item -> item.id != id }) }
  }

  fun togglePlaylistCycling() {
    if (_uiState.value.isPlaylistActive) {
      stopPlaylistCycling()
    } else {
      startPlaylistCycling()
    }
  }

  private fun startPlaylistCycling() {
    val items = _uiState.value.playlistItems
    if (items.isEmpty()) return

    playlistJob?.cancel()
    val initialIndex = 0
    val initialDuration = items[initialIndex].durationSeconds

    _uiState.update {
      it.copy(
        isPlaylistActive = true,
        currentPlaylistIndex = initialIndex,
        playlistRemainingSeconds = initialDuration
      )
    }

    // Load first item
    _commandFlow.tryEmit(WebViewCommand.LoadUrl(items[initialIndex].url))

    playlistJob = viewModelScope.launch {
      while (_uiState.value.isPlaylistActive) {
        delay(1000)
        val remaining = _uiState.value.playlistRemainingSeconds - 1
        if (remaining <= 0) {
          advancePlaylist(forward = true)
        } else {
          _uiState.update { it.copy(playlistRemainingSeconds = remaining) }
        }
      }
    }
  }

  fun advancePlaylist(forward: Boolean = true) {
    val items = _uiState.value.playlistItems
    if (items.isEmpty()) return

    val currentIndex = _uiState.value.currentPlaylistIndex
    val nextIndex = if (forward) {
      if (currentIndex + 1 < items.size) currentIndex + 1 else if (_uiState.value.isPlaylistLooping) 0 else currentIndex
    } else {
      if (currentIndex - 1 >= 0) currentIndex - 1 else if (_uiState.value.isPlaylistLooping) items.size - 1 else 0
    }

    if (nextIndex == currentIndex && !forward && currentIndex == 0) return
    if (nextIndex == currentIndex && forward && !_uiState.value.isPlaylistLooping) {
      stopPlaylistCycling()
      return
    }

    val nextItem = items[nextIndex]
    _uiState.update {
      it.copy(
        currentPlaylistIndex = nextIndex,
        playlistRemainingSeconds = nextItem.durationSeconds
      )
    }
    _commandFlow.tryEmit(WebViewCommand.LoadUrl(nextItem.url))
  }

  fun stopPlaylistCycling() {
    playlistJob?.cancel()
    playlistJob = null
    _uiState.update { it.copy(isPlaylistActive = false) }
  }

  // --- SCRIPT & AUTOMATION RUNNER ---

  fun executeScript(script: String, scriptTitle: String) {
    _commandFlow.tryEmit(WebViewCommand.EvaluateJs(script, scriptTitle))
  }

  fun recordScriptOutput(title: String, output: String, isError: Boolean = false) {
    val cleanOutput = output.trim().removeSurrounding("\"")
    val log = ScriptLog(scriptTitle = title, output = cleanOutput, isError = isError)
    _uiState.update {
      it.copy(scriptLogs = listOf(log) + it.scriptLogs.take(30))
    }
  }

  fun setCustomScript(script: String) {
    _uiState.update { it.copy(customScript = script) }
  }

  fun clearScriptLogs() {
    _uiState.update { it.copy(scriptLogs = emptyList()) }
  }

  // --- BOOKMARKS & HISTORY ---

  fun toggleBookmark() {
    val active = _uiState.value.activeTab
    val currentUrl = active.currentUrl
    if (currentUrl.isBlank()) return

    viewModelScope.launch {
      if (_uiState.value.isBookmarked) {
        repository.removeBookmarkByUrl(currentUrl)
        _uiState.update { it.copy(isBookmarked = false) }
      } else {
        repository.addBookmark(active.title, currentUrl)
        _uiState.update { it.copy(isBookmarked = true) }
      }
    }
  }

  private fun checkActiveBookmark() {
    val currentUrl = _uiState.value.activeTab.currentUrl
    val isBookmarked = _uiState.value.bookmarksList.any { it.url == currentUrl }
    _uiState.update { it.copy(isBookmarked = isBookmarked) }
  }

  fun deleteBookmark(bookmark: BookmarkEntity) {
    viewModelScope.launch {
      repository.removeBookmark(bookmark)
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      repository.clearHistory()
    }
  }

  fun deleteHistoryItem(item: HistoryEntity) {
    viewModelScope.launch {
      repository.deleteHistoryItem(item)
    }
  }

  // --- SHEET & DIALOG NAVIGATION ---

  fun openSheet(sheet: SheetState) {
    _uiState.update { it.copy(activeSheet = sheet) }
  }

  fun closeSheet() {
    _uiState.update { it.copy(activeSheet = SheetState.NONE) }
  }

  fun setSearchEngine(engine: SearchEngine) {
    _uiState.update { it.copy(searchEngine = engine) }
  }

  fun setBookmarksTab(tabIndex: Int) {
    _uiState.update { it.copy(bookmarksTabSelected = tabIndex) }
  }

  // Accordion toggle: starts closed; opening one closes any previously opened one
  fun toggleWhatsNewAccordion(index: Int) {
    _uiState.update {
      val current = it.whatsNewExpandedIndex
      val next = if (current == index) null else index
      it.copy(whatsNewExpandedIndex = next)
    }
  }

  // --- ANDROID AUTOMOTIVE & CAR MODE METHODS ---

  fun setCarMode(enabled: Boolean) {
    _uiState.update { it.copy(isCarMode = enabled) }
  }

  fun toggleCarMode() {
    _uiState.update { it.copy(isCarMode = !it.isCarMode) }
  }

  fun setVehicleParked(parked: Boolean) {
    _uiState.update { it.copy(isVehicleParked = parked) }
    if (!parked && !_uiState.value.isDriveTestingEnabled) {
      // Vehicle in motion and testing mode not active: pause auto-scrolling
      if (_uiState.value.isAutoScrollActive && !_uiState.value.isScrollPaused) {
        setScrollPaused(true)
      }
    }
  }

  fun setDriveTestingEnabled(enabled: Boolean) {
    _uiState.update { it.copy(isDriveTestingEnabled = enabled) }
  }

  fun toggleDriveTesting() {
    _uiState.update { it.copy(isDriveTestingEnabled = !it.isDriveTestingEnabled) }
  }

  fun toggleCarPresets() {
    _uiState.update { it.copy(isCarPresetsExpanded = !it.isCarPresetsExpanded) }
  }

  fun setAutomotiveTopPadding(paddingDp: Int) {
    _uiState.update { it.copy(automotiveTopPaddingDp = paddingDp) }
  }

  fun openCarPreset(preset: CarPreset) {
    submitQueryOrUrl(preset.url)
    _uiState.update { it.copy(isCarPresetsExpanded = false) }
  }
}
