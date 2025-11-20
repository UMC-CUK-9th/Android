package com.example.umc_android_mission2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_android_mission2.databinding.FragmentSavedAlbumsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedAlbumsFragment: Fragment(), SavedAlbumRVAdapter.OnItemClickListener {
    private var _binding: FragmentSavedAlbumsBinding? = null
    private val binding get() = _binding!!
    private lateinit var savedAlbumRVAdapter: SavedAlbumRVAdapter
    private lateinit var albumDB: AlbumDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedAlbumsBinding.inflate(inflater, container, false)
        albumDB = AlbumDatabase.getInstance(requireContext())!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadLikedAlbums()
    }

    private fun initRecyclerView() {
        savedAlbumRVAdapter = SavedAlbumRVAdapter()
        savedAlbumRVAdapter.setMyItemClickListener(this)
        binding.lockerSavedAlbumListRv.adapter = savedAlbumRVAdapter
        binding.lockerSavedAlbumListRv.layoutManager = LinearLayoutManager(context)
    }

    private fun loadLikedAlbums() {
        val userId = getJwt(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            // 현재 사용자가 '좋아요'한 앨범의 ID 목록을 가져옴
            val likedAlbumIds = albumDB.likeDao().getLikedAlbumIds(userId)
            // ID 목록을 사용해 실제 앨범 정보 목록을 가져옴
            val likedAlbums = albumDB.albumDao().getAlbumsByIds(likedAlbumIds)
            
            withContext(Dispatchers.Main) {
                savedAlbumRVAdapter.addAlbums(likedAlbums)
            }
        }
    }

    override fun onRemoveAlbum(albumId: Int) {
        val userId = getJwt(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            // LikeTable에서 해당 데이터를 삭제
            albumDB.likeDao().delete(userId, albumId)
        }
    }

    private fun getJwt(context: Context): Int {
        val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf.getInt("jwt", 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}