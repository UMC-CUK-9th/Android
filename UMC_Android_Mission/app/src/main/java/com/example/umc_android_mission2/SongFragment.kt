package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_android_mission2.databinding.FragmentSongBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongFragment: Fragment() {

    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!

    private var albumId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // AlbumVPAdapter로부터 전달받은 albumId를 변수에 저장
        arguments?.let {
            albumId = it.getInt("albumId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AlbumDatabase.getInstance(requireContext())!!

        // 코루틴을 사용해 DB에서 해당 앨범의 수록곡 목록 가져오기
        viewLifecycleOwner.lifecycleScope.launch {
            val songs = withContext(Dispatchers.IO) {
                db.songDao().getSongsInAlbum(albumId)
            }

            // 가져온 정보로 RecyclerView 업데이트
            withContext(Dispatchers.Main) {
                val songRVAdapter = SongRVAdapter(songs)
                binding.songListRv.adapter = songRVAdapter
                binding.songListRv.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}