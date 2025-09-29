package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_android_mission2.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    private var _binding: FragmentLockerBinding? = null

    private val binding get() = _binding!!

    private val information = arrayListOf("저장한 곡", "음악파일","저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerVPAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerVPAdapter
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}