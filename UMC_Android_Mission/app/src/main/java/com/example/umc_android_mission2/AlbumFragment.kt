package com.example.umc_android_mission2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.umc_android_mission2.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var albumId: Int = 0
    private var isLiked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            albumId = it.getInt("albumId", 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AlbumDatabase.getInstance(requireContext())!!
        val userId = getMemberId(requireContext())

        // --- 1. 사용자별 '좋아요' 상태 확인 ---
        // 로그인한 경우에만 '좋아요' 상태를 확인
        if (userId != 0) {
            lifecycleScope.launch(Dispatchers.IO) {
                val liked = db.likeDao().isLiked(userId, albumId) != null
                withContext(Dispatchers.Main) {
                    isLiked = liked
                    setLikeStatus(isLiked)
                }
            }
        }

        // DB에서 앨범 정보를 가져와 UI에 적용
        lifecycleScope.launch(Dispatchers.IO) {
            val album = db.albumDao().getAlbum(albumId)
            withContext(Dispatchers.Main) {
                if (album != null) {
                    binding.albumTitleTv.text = album.title
                    binding.albumArtistTv.text = album.artist
                    album.coverImg?.let { binding.albumCoverIv.setImageResource(it) }
                }
            }
        }

        val albumAdapter = AlbumVPAdapter(this@AlbumFragment, albumId)
        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        binding.albumBackIv.setOnClickListener {
            findNavController().popBackStack()
        }

        // --- 2. '좋아요' 클릭 리스너 로직 변경 ---
        binding.albumLikeIv.setOnClickListener {
            // 로그아웃 상태(userId가 0)이면 아무것도 하지 않고 리스너를 종료
            if (userId == 0) {
                return@setOnClickListener
            }

            isLiked = !isLiked
            setLikeStatus(isLiked)

            lifecycleScope.launch(Dispatchers.IO) {
                if (isLiked) {
                    db.likeDao().insert(Like(userId, albumId))
                } else {
                    db.likeDao().delete(userId, albumId)
                }
            }
        }
    }

    // 새로운 로그인 방식에 맞는 사용자 ID를 가져오는 함수
    private fun getMemberId(context: Context): Int {
        val spf = context.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf.getInt("memberId", 0)
    }

    private fun setLikeStatus(isLiked: Boolean) {
        if (isLiked) {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}