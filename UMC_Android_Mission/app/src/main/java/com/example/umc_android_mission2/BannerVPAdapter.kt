package com.example.umc_android_mission2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList: ArrayList<Fragment> = ArrayList()

    // 배너 뷰페이저의 총 아이템 수 반환
    override fun getItemCount(): Int = fragmentList.size

    // 위치에 따라 해당 Fragment 반환
    override fun createFragment(position: Int): Fragment = fragmentList[position]

    // 어댑터에 Fragment 추가
    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.size - 1)
    }
}
