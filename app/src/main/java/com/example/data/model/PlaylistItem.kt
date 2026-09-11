package com.example.data.model

import java.util.UUID

data class PlaylistItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val url: String,
  val durationSeconds: Int = 15
)
