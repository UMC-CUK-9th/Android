package com.example.umc_android_mission2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Album::class, Song::class], version = 3)
abstract class AlbumDatabase: RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AlbumDatabase? = null

        fun getInstance(context: Context): AlbumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlbumDatabase::class.java,
                    "umc-android-mission-db"
                )
                .addCallback(DatabaseCallback(context.applicationContext))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getInstance(context)
                    prepopulateDatabase(database.albumDao(), database.songDao())
                }
            }

            suspend fun prepopulateDatabase(albumDao: AlbumDao, songDao: SongDao) {
                // --- 초기 데이터 구조 정의 (앨범과 수록곡) ---
                val initialData = mapOf(
                    Album(title = "LILAC", artist = "아이유(IU)", coverImg = R.drawable.img_album_exp2) to listOf(
                        Song(title = "LILAC", artist = "아이유 (IU)", isTitleSong = true, playtime = 214, music="music_lilac"),
                        Song(title = "Flu", artist= "아이유 (IU)", playtime = 188, music="music_flu"),
                        Song(title = "Coin", artist = "아이유 (IU)", playtime = 202, music="music_coin")
                    ),
                    Album(title = "Butter", artist = "방탄소년단(BTS)", coverImg = R.drawable.img_album_exp) to listOf(
                        Song(title = "Butter", artist = "방탄소년단(BTS)", isTitleSong = true, playtime = 164, music="music_butter"),
                        Song(title = "Permission to Dance", artist= "방탄소년단(BTS)", playtime = 187, music="music_permission_to_dance")
                    ),
                    Album(title = "Lost corner", artist = "Kenshi Yonezu", coverImg = R.drawable.img_album_lost) to listOf(
                        Song(title = "Lost corner", artist = "Kenshi Yonezu", isTitleSong = true, playtime = 240, music="music_lost_corner"),
                        Song(title = "ゆめうつつ", artist= "Kenshi Yonezu", playtime = 245, music="music_yumeutsutsu")
                    )
                    // 여기에 다른 앨범과 수록곡을 계속 추가할 수 있습니다.
                )

                // --- 자동화된 데이터 삽입 로직 ---
                initialData.forEach { (album, songs) ->
                    // 앨범을 먼저 삽입하고, 생성된 고유 ID를 받아옴
                    val albumId = albumDao.insert(album)

                    // 받아온 앨범 ID를 모든 수록곡에 주입
                    val songsWithAlbumId = songs.map { song ->
                        song.albumIdx = albumId.toInt()
                        song
                    }

                    // ID가 주입된 수록곡들을 DB에 삽입
                    songDao.insertAll(songsWithAlbumId)
                }
            }
        }
    }
}