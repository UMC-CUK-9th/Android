package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.umc_android_mission2.databinding.FragmentSongBinding

class SongFragment: Fragment() {

    private var _binding: FragmentSongBinding? = null
    private val binding get() = _binding!!
    private var songDatas = ArrayList<SongData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSongBinding.inflate(inflater, container, false)

        // 여기에 수록곡 데이터를 추가합니다.
        songDatas.apply {
            add(SongData(1, "LILAC", "아이유 (IU)"))
            add(SongData(2, "Flu", "아이유 (IU)"))
            add(SongData(3, "Coin", "아이유 (IU)"))
            add(SongData(4, "봄 안녕 봄", "아이유 (IU)"))
            add(SongData(5, "Celebrity", "아이유 (IU)"))
            add(SongData(6, "돌림노래", "아이유 (IU)"))
        }

        // 어댑터와 레이아웃 매니저 설정
        val songRVAdapter = SongRVAdapter(songDatas)
        binding.songListRv.adapter = songRVAdapter
        binding.songListRv.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}