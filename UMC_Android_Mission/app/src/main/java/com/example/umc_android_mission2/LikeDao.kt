package com.example.umc_android_mission2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LikeDao {
    @Insert
    fun insert(like: Like)

    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun delete(userId: Int, albumId: Int)

    // 사용자가 특정 앨범을 '좋아요' 했는지 확인
    @Query("SELECT * FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun isLiked(userId: Int, albumId: Int): Like?

    // 특정 사용자가 '좋아요'한 모든 앨범의 ID 목록을 가져옴
    @Query("SELECT albumId FROM LikeTable WHERE userId = :userId")
    fun getLikedAlbumIds(userId: Int): List<Int>
}
