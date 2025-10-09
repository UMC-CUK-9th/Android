package com.example.umc_android_mission2

//세가지 정보를 하나의 묶음으로 다룰 수 있게 해준다.
data class AlbumData(
    var title: String? = null,
    var artist: String? = null,
    var coverImg: Int? = null
)
