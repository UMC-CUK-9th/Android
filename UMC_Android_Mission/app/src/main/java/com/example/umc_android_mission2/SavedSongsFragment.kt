package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_android_mission2.databinding.FragmentSavedSongsBinding
import com.google.firebase.Firebase
import com.google.firebase.database.database


class SavedSongsFragment : Fragment() {
    private var _binding: FragmentSavedSongsBinding? = null
    private val binding get() = _binding!!

    private val songsAdapter = SavedSongsRVAdapter()

    // Firebase Database 참조
    private val database = Firebase.database
    private val userLikedSongsRef = database.getReference("users/testUser/likedSongs")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedSongsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupClickListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadLikedSongs()
    }

    private fun setupRecyclerView() {
        binding.lockerSavedSongListRv.adapter = songsAdapter
        binding.lockerSavedSongListRv.layoutManager = LinearLayoutManager(context)
    }

    private fun loadLikedSongs() {
        // RoomDB에서만 '좋아요' 목록을 가져오던 방식
        // CoroutineScope(Dispatchers.IO).launch {
        //     val likedSongs = songDB.songDao().getLikedSongs()
        //     withContext(Dispatchers.Main) {
        //         songsAdapter.addSongs(likedSongs)
        //     }
        // }

        // Firebase에서 노래 객체 목록을 직접 가져와 화면에 표시
        userLikedSongsRef.get().addOnSuccessListener { dataSnapshot ->
            val likedSongs = ArrayList<LikedSong>()
            for (songSnapshot in dataSnapshot.children) {
                // Firebase 데이터를 LikedSong 객체로 변환
                val songIdx = songSnapshot.child("songIdx").getValue(Long::class.java)?.toInt() ?: continue
                val title = songSnapshot.child("title").getValue(String::class.java) ?: ""
                val artist = songSnapshot.child("artist").getValue(String::class.java) ?: ""
                val coverImg = songSnapshot.child("coverImg").getValue(Long::class.java)?.toInt()

                val song = LikedSong(
                    songIdx = songIdx,
                    title = title,
                    artist = artist,
                    coverImg = coverImg
                )
                likedSongs.add(song)
            }
            songsAdapter.addSongs(likedSongs)
        }
    }

    private fun setupClickListeners() {
        songsAdapter.setMyItemClickListener(object : SavedSongsRVAdapter.OnItemClickListener {
            override fun onMoreClick(song: LikedSong) {
                // CoroutineScope(Dispatchers.IO).launch {
                //     songDB.songDao().updateLike(song.songIdx, false)
                //     loadLikedSongs()
                // }

                // Firebase에서 해당 노래를 삭제
                userLikedSongsRef.child(song.songIdx.toString()).removeValue()
                    .addOnSuccessListener {
                        loadLikedSongs()
                    }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
