package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.BrowserViewModel
import com.example.ui.TabState
import com.example.ui.WebViewCommand
import kotlinx.coroutines.flow.collectLatest

private const val DESKTOP_USER_AGENT =
  "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
@Composable
fun WebViewContainer(
  activeTab: TabState,
  viewModel: BrowserViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val webView = remember {
    WebView(context).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        loadWithOverviewMode = true
        useWideViewPort = true
        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        mediaPlaybackRequiresUserGesture = false
      }
    }
  }

  // Handle User-Agent Desktop Mode toggle
  LaunchedEffect(activeTab.isDesktop) {
    if (activeTab.isDesktop) {
      webView.settings.userAgentString = DESKTOP_USER_AGENT
      webView.settings.useWideViewPort = true
      webView.settings.loadWithOverviewMode = true
    } else {
      webView.settings.userAgentString = null // Reset to default mobile UA
      webView.settings.useWideViewPort = false
      webView.settings.loadWithOverviewMode = false
    }
  }

  // Setup WebView Clients
  LaunchedEffect(activeTab.id) {
    webView.webViewClient = object : WebViewClient() {
      override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        viewModel.updateTabLoading(activeTab.id, isLoading = true, progress = 15)
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val current = url ?: activeTab.url
        viewModel.updateTabNavState(
          tabId = activeTab.id,
          currentUrl = current,
          title = view?.title,
          canBack = view?.canGoBack() ?: false,
          canForward = view?.canGoForward() ?: false
        )
        viewModel.updateTabLoading(activeTab.id, isLoading = false, progress = 100)
      }

      override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
      ) {
        super.onReceivedError(view, request, error)
        if (request?.isForMainFrame == true) {
          viewModel.updateTabLoading(activeTab.id, isLoading = false, progress = 100)
        }
      }

      override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
      ): Boolean {
        return false // Handle within WebView
      }
    }

    webView.webChromeClient = object : WebChromeClient() {
      override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        viewModel.updateTabLoading(
          activeTab.id,
          isLoading = newProgress < 100,
          progress = newProgress
        )
      }

      override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        if (!title.isNullOrBlank()) {
          viewModel.updateTabNavState(
            tabId = activeTab.id,
            currentUrl = view?.url ?: activeTab.currentUrl,
            title = title,
            canBack = view?.canGoBack() ?: false,
            canForward = view?.canGoForward() ?: false
          )
        }
      }
    }

    // Touch event listener to pause auto-scroll gracefully during user interaction
    webView.setOnTouchListener { _, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          viewModel.setScrollPaused(true)
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          // Resume auto scroll after a short hesitation
          webView.postDelayed({
            viewModel.setScrollPaused(false)
          }, 1200)
        }
      }
      false
    }
  }

  // Collect incoming webview commands from the ViewModel
  LaunchedEffect(activeTab.id) {
    viewModel.commandFlow.collectLatest { command ->
      when (command) {
        is WebViewCommand.GoBack -> {
          if (webView.canGoBack()) webView.goBack()
        }
        is WebViewCommand.GoForward -> {
          if (webView.canGoForward()) webView.goForward()
        }
        is WebViewCommand.Reload -> {
          webView.reload()
        }
        is WebViewCommand.Stop -> {
          webView.stopLoading()
        }
        is WebViewCommand.LoadUrl -> {
          webView.loadUrl(command.url)
        }
        is WebViewCommand.EvaluateJs -> {
          webView.evaluateJavascript(command.script) { result ->
            viewModel.recordScriptOutput(command.callbackTitle, result ?: "Completed (no return)")
          }
        }
        is WebViewCommand.ScrollBy -> {
          webView.evaluateJavascript(
            "window.scrollBy({ top: ${command.yPixels}, behavior: 'instant' });",
            null
          )
        }
        is WebViewCommand.SetDesktop -> {
          if (command.enabled) {
            webView.settings.userAgentString = DESKTOP_USER_AGENT
          } else {
            webView.settings.userAgentString = null
          }
          webView.reload()
        }
      }
    }
  }

  // Load initial URL for the tab if empty
  LaunchedEffect(activeTab.url) {
    if (webView.url != activeTab.url && activeTab.url.isNotBlank()) {
      webView.loadUrl(activeTab.url)
    }
  }

  AndroidView(
    factory = { webView },
    modifier = modifier.fillMaxSize()
  )

  DisposableEffect(Unit) {
    onDispose {
      // Keep webView clean
    }
  }
}
