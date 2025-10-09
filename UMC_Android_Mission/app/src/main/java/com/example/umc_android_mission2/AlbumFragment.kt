package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.umc_android_mission2.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        // HomeFragment에서 전달받은 데이터 꺼내기
        val title = arguments?.getString("title")
        val artist = arguments?.getString("artist")
        val coverImg = arguments?.getInt("coverImg")

        // 전달받은 데이터로 UI 업데이트
        binding.albumTitleTv.text = title
        binding.albumArtistTv.text = artist
        coverImg?.let { binding.albumCoverIv.setImageResource(it) }

        // 뒤로가기 버튼 로직 수정
        binding.albumBackIv.setOnClickListener {
            findNavController().popBackStack()
        }

        // AlbumVPAdapter 생성 시 전달받은 앨범 제목과 아티스트 이름을 전달
        val albumAdapter = AlbumVPAdapter(this, title ?: "", artist ?: "")
        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
            tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}