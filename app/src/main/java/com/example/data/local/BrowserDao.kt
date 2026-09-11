package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserDao {
  @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity): Long

  @Delete
  suspend fun deleteBookmark(bookmark: BookmarkEntity)

  @Query("DELETE FROM bookmarks WHERE url = :url")
  suspend fun deleteBookmarkByUrl(url: String)

  @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url LIMIT 1)")
  fun isBookmarked(url: String): Flow<Boolean>

  @Query("SELECT * FROM history ORDER BY visitedAt DESC LIMIT 100")
  fun getRecentHistory(): Flow<List<HistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(history: HistoryEntity): Long

  @Query("DELETE FROM history")
  suspend fun clearHistory()

  @Delete
  suspend fun deleteHistoryItem(history: HistoryEntity)
}
