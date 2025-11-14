package com.example.umc_android_mission2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment: Fragment, private val albumId: Int) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("albumId", albumId)

        return when (position) {
            0 -> {
                val musicFileFragment = SongFragment()
                musicFileFragment.arguments = bundle
                musicFileFragment
            }
            1 -> {
                val detailFragment = DetailFragment()
                detailFragment.arguments = bundle
                detailFragment
            }
            else -> {
                val videoFragment = VideoFragment()
                videoFragment.arguments = bundle
                videoFragment
            }
        }
    }
}