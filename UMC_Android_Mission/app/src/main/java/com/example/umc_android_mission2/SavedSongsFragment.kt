package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_android_mission2.databinding.FragmentSavedSongsBinding
// import com.google.firebase.Firebase // 주석 처리
// import com.google.firebase.database.database // 주석 처리
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedSongsFragment : Fragment() {
    private var _binding: FragmentSavedSongsBinding? = null
    private val binding get() = _binding!!

    private lateinit var songDB: AlbumDatabase
    private val songsAdapter = SavedSongsRVAdapter()

    // Firebase Database 참조 (주석 처리)
    // private val database = Firebase.database
    // private val userLikedSongsRef = database.getReference("users/testUser/likedSongs")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedSongsBinding.inflate(inflater, container, false)
        songDB = AlbumDatabase.getInstance(requireContext())!!

        setupRecyclerView()
        setupClickListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Room DB에서 '좋아요' 목록을 불러옵니다.
        loadLikedSongsFromDB()
    }

    private fun setupRecyclerView() {
        binding.lockerSavedSongListRv.adapter = songsAdapter
        binding.lockerSavedSongListRv.layoutManager = LinearLayoutManager(context)
    }

    // RoomDB에서 '좋아요'한 노래 목록을 가져오는 함수
    private fun loadLikedSongsFromDB() {
        lifecycleScope.launch {
            val likedSongs = withContext(Dispatchers.IO) {
                songDB.songDao().getLikedSongs()
            }
            songsAdapter.addSongs(likedSongs)
        }
    }

    /*
    // Firebase에서 노래 목록을 가져오던 기존 로직 (주석 처리)
    private fun loadLikedSongs() {
        userLikedSongsRef.get().addOnSuccessListener { dataSnapshot ->
            val likedSongs = ArrayList<Song>()
            for (songSnapshot in dataSnapshot.children) {
                val song = songSnapshot.getValue(Song::class.java)
                if (song != null) {
                    likedSongs.add(song)
                }
            }
            songsAdapter.addSongs(likedSongs)
        }
    }
    */

    private fun setupClickListeners() {
        songsAdapter.setMyItemClickListener(object : SavedSongsRVAdapter.OnItemClickListener {
            override fun onMoreClick(song: Song) {
                // '더보기' 클릭 시 Room DB의 isLike 상태를 false로 변경
                lifecycleScope.launch(Dispatchers.IO) {
                    songDB.songDao().updateLike(song.songIdx, false)
                    // UI 갱신을 위해 메인 스레드에서 목록을 다시 로드
                    withContext(Dispatchers.Main) {
                        loadLikedSongsFromDB()
                    }
                }
            }
        })

        /*
        // Firebase의 '더보기' 클릭 리스너 로직 (주석 처리)
        songsAdapter.setMyItemClickListener(object : SavedSongsRVAdapter.OnItemClickListener {
            override fun onMoreClick(song: Song) {
                // Firebase에서 해당 노래를 삭제
                userLikedSongsRef.child(song.songIdx.toString()).removeValue()
                    .addOnSuccessListener {
                        loadLikedSongs() // Firebase 목록 다시 로드
                    }
            }
        })
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
