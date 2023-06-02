package com.example.filelocal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.filelocal.R
import com.example.filelocal.adapter.CustomFragmentPageAdapter
import com.example.filelocal.fragment.GridItemFragment
import com.example.filelocal.video.VideoItem
import kotlinx.android.synthetic.main.activity_image_browse.*
import kotlinx.android.synthetic.main.activity_image_browse.tl_category
import kotlinx.android.synthetic.main.activity_video_browse.*

/**
 * vp_fragments: is a viewpager to put fragments. So fragmentMap is needed.
 * tl_category: slideTabLayout need set viewpager for it.
 */
class VideoBrowseActivity: BaseActivity() {
    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, VideoBrowseActivity::class.java))
        }
    }
    private val fragmentMap = hashMapOf<String, GridItemFragment<VideoItem>>()
    override fun getContentRes(): Int {
        return R.layout.activity_video_browse
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAnimations(
            R.anim.slide_x_1_0,
            R.anim.slide_x_0_0,
            R.anim.slide_x_0_0,
            R.anim.slide_x_0_1
        )
        loadFragments(savedInstanceState)
        val pageList = arrayListOf<String>()
        pageList.add("1")
        val fragmentList = arrayListOf<GridItemFragment<VideoItem>>()
        if (savedInstanceState != null) {
            val a = GridItemFragment.newInstance<VideoItem>("image")
            supportFragmentManager.putFragment(savedInstanceState, "ALL VIDEO", a)
            val m = supportFragmentManager.getFragment(
                savedInstanceState,
                "ALL VIDEO"
            )
            fragmentList.add(m as GridItemFragment<VideoItem>)
            vp_fragments_video.adapter = CustomFragmentPageAdapter(supportFragmentManager, fragmentList)
            tl_category.setViewPager(vp_fragments_video, pageList.toTypedArray())
//            tl_category.setCurrentTab(1, false)
//            tl_category.addNewTab("12121")
        }
    }

    private fun loadFragments(savedInstanceState: Bundle?) {
        fragmentMap.clear()
        val key = "ALL VIDEO"
        if (savedInstanceState!= null) {
            fragmentMap[key] = supportFragmentManager.getFragment(
                savedInstanceState,
                key
            ) as GridItemFragment<VideoItem>
        }
    }
}