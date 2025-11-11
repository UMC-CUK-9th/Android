package com.example.umc_android_mission2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

data class LikedSong(
    val songIdx: Int,
    val title: String,
    val artist: String,
    val coverImg: Int?
)

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

    @Query("""
        SELECT s.songIdx, s.title, s.artist, a.coverImg
        FROM SongTable as s
        INNER JOIN AlbumTable as a ON s.albumIdx = a.albumIdx
        WHERE s.isLike = 1
    """)
    fun getLikedSongs(): List<LikedSong>

    // 1. Firebase에서 받아온 ID 목록으로 '좋아요' 노래 정보를 조회하는 함수 추가
    @Query("""
        SELECT s.songIdx, s.title, s.artist, a.coverImg
        FROM SongTable as s
        INNER JOIN AlbumTable as a ON s.albumIdx = a.albumIdx
        WHERE s.songIdx IN (:songIds)
    """)
    fun getLikedSongsByIds(songIds: List<Int>): List<LikedSong>
}
