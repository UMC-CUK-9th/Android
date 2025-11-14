package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_android_mission2.databinding.FragmentVideoBinding

class VideoFragment: Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    // 다른 프래그먼트들과의 데이터 흐름 일관성을 위해 추가
    private var albumId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            albumId = it.getInt("albumId", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(inflater,container,false)
        return binding.root

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}