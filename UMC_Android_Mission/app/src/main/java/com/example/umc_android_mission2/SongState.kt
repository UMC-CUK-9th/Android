package com.example.umc_android_mission2

data class SongState (
    var songIdx: Int = 0,
    val title: String = "",
    val artist: String = "",
    var second: Int = 0,
    val playtime: Int = 0,
    var isPlaying: Boolean = false,
    val coverImg: Int? = null,
    var isLike: Boolean = false // 좋아요 상태 추가
)
