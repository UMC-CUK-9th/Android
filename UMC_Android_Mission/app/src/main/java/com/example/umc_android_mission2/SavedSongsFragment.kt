package com.example.umc_android_mission2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.umc_android_mission2.databinding.FragmentSavedSongsBinding


class SavedSongsFragment: Fragment() {
    private var _binding: FragmentSavedSongsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedSongsBinding.inflate(inflater, container, false)
        return binding.root
    }
}