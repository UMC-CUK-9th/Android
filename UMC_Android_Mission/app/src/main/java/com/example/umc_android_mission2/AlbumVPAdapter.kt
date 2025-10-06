package com.example.umc_android_mission2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(
    fragment: Fragment,
    private val albumTitleFromFragment: String, // 생성자에서 받은 값을 저장할 프로퍼티
    private val artistNameFromFragment: String  // 생성자에서 받은 값을 저장할 프로퍼티
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongFragment()
            // DetailFragment 생성 시 저장된 프로퍼티 값을 newInstance를 통해 전달
            1 -> DetailFragment.newInstance(albumTitleFromFragment, artistNameFromFragment)
            else -> VideoFragment()
        }
    }
}