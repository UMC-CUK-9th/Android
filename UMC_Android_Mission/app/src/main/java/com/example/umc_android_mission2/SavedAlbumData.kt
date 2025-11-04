package com.example.umc_android_mission2

data class SavedAlbumData(
    var title: String? = null,
    var artist: String? = null,
    var coverImg: Int? = null,
    var info: String? = null, // 앨범 발매 정보 (날짜, 타입 등)
    var isPlaying: Boolean = false // 재생 상태를 저장
)