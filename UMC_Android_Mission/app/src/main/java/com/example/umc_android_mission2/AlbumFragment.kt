package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_android_mission2.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        // fragment_album.xml에서 앨범 제목과 아티스트 이름을 가져옴
        val currentAlbumTitle = binding.albumTitleTv.text.toString()
        val currentArtistName = binding.albumArtistTv.text.toString()

        // AlbumVPAdapter 생성 시 앨범 제목과 아티스트 이름을 전달
        val albumAdapter = AlbumVPAdapter(this, currentAlbumTitle, currentArtistName)
        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
                tab, position ->
            tab.text = information[position]
        }.attach()
        //.attach()는 탭레이아웃과 뷰페이저2를 붙이는 메서드

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
