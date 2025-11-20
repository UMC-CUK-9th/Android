package com.example.umc_android_mission2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album): Long

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE albumIdx = :albumId")
    fun getAlbum(albumId: Int): Album?

    // 앨범 ID 목록을 받아 해당하는 앨범 목록을 반환하는 쿼리
    @Query("SELECT * FROM AlbumTable WHERE albumIdx IN (:albumIds)")
    fun getAlbumsByIds(albumIds: List<Int>): List<Album>
}
