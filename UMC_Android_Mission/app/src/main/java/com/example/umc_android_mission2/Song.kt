package com.example.umc_android_mission2

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SongTable",
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = ["albumIdx"],
            childColumns = ["albumIdx"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["albumIdx"])]
)
data class Song(
    @PrimaryKey(autoGenerate = true) var songIdx: Int = 0,
    var title: String = "",
    var artist: String = "",
    var playtime: Int = 0,
    var music: String = "",
    var isTitleSong: Boolean = false,
    var isLike: Boolean = false,
    var albumIdx: Int = 0
)
