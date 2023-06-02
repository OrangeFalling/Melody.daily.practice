package com.example.filelocal.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CustomFragmentPageAdapter(fm: FragmentManager?, fragmentList: List<Fragment>) : FragmentPagerAdapter(fm!!) {
    private val fragmentList = fragmentList
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}