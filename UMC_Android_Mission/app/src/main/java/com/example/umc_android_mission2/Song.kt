package com.example.umc_android_mission2

data class Song (
    val title: String ="",
    val singer: String ="",
    var second: Int =0,
    val playtime:Int =0,
    var isPlaying: Boolean = false,
    val coverImg: Int? = null
)