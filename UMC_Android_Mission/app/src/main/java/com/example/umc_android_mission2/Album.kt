package com.example.umc_android_mission2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey(autoGenerate = true) var albumIdx: Int = 0,
    var title: String? = "",
    var artist: String? = "",
    var isLike: Boolean = false,
    var coverImg: Int? = null
)
