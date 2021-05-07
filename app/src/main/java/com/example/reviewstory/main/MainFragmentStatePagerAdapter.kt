package com.example.reviewstory.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.reviewstory.mypage.MypageFragment
import com.example.reviewstory.search.SearchFragment
import com.example.reviewstory.settings.SettingsFragment
import com.example.reviewstory.timeline.StampsFragment
import com.example.reviewstory.timeline.TimelineFragment

class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return MypageFragment()
            1 -> return SearchFragment()
            2 -> return TimelineFragment()
            3 -> return SettingsFragment()
            else -> return TimelineFragment()
        }
    }

    override fun getCount(): Int = fragmentCount // 자바에서는 { return fragmentCount }

}