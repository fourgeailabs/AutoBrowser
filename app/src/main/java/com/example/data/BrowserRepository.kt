package com.example.data

import com.example.data.local.BrowserDao
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val dao: BrowserDao) {
  val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
  val history: Flow<List<HistoryEntity>> = dao.getRecentHistory()

  suspend fun addBookmark(title: String, url: String) {
    dao.insertBookmark(BookmarkEntity(title = title, url = url))
  }

  suspend fun removeBookmark(bookmark: BookmarkEntity) {
    dao.deleteBookmark(bookmark)
  }

  suspend fun removeBookmarkByUrl(url: String) {
    dao.deleteBookmarkByUrl(url)
  }

  fun isBookmarked(url: String): Flow<Boolean> = dao.isBookmarked(url)

  suspend fun recordHistory(title: String, url: String) {
    if (url.isNotBlank() && !url.startsWith("about:") && !url.startsWith("data:")) {
      dao.insertHistory(HistoryEntity(title = title.ifBlank { url }, url = url))
    }
  }

  suspend fun clearHistory() {
    dao.clearHistory()
  }

  suspend fun deleteHistoryItem(item: HistoryEntity) {
    dao.deleteHistoryItem(item)
  }
}
