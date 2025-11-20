package com.example.umc_android_mission2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(songs: List<Song>)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE albumIdx = :albumId")
    fun getSongsInAlbum(albumId: Int): List<Song>

    @Query("SELECT * FROM SongTable WHERE songIdx = :songId")
    fun getSong(songId: Int): Song?

    @Query("UPDATE SongTable SET isLike = :isLike WHERE songIdx = :songId")
    fun updateLike(songId: Int, isLike: Boolean)

    // isLike가 true인 모든 Song 객체를 가져오는 쿼리로 변경
    // 이제 Song 객체에 coverImg가 포함되어 있으므로 JOIN이 필요 없음
    @Query("SELECT * FROM SongTable WHERE isLike = 1")
    fun getLikedSongs(): List<Song>
}
