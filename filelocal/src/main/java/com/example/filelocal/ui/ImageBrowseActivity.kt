package com.example.filelocal.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.filelocal.media.CategorizedMediaData
import com.example.filelocal.viewmodel.ImageBrowseViewModel
import com.example.filelocal.image.ImageItem
import com.example.filelocal.R
import com.example.filelocal.adapter.CustomFragmentPageAdapter
import com.example.filelocal.fragment.GridItemFragment
import kotlinx.android.synthetic.main.activity_image_browse.*

class ImageBrowseActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this)[ImageBrowseViewModel::class.java] }
    private val fragmentMap = hashMapOf<String, GridItemFragment<ImageItem>>()

    override fun getContentRes(): Int {
        return R.layout.activity_image_browse
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
        bindData()
        viewModel.loadImages()
        tv_no_content.setOnClickListener {
            VideoBrowseActivity.start(this)
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val keyList = arrayListOf<String>()
        fragmentMap.entries.forEach {
            if (it.value.isAdded) {
                val key = "GridFragment-${it.key}"
                supportFragmentManager.putFragment(outState, key, it.value)
                keyList.add(key)
            }
        }
        viewModel.putFragmentKeyList(keyList)
    }

    private fun checkPermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            tv_no_permission.visibility = View.GONE
            cl_content.visibility = View.VISIBLE
            return true
        } else {
            tv_no_permission.visibility = View.VISIBLE
            cl_content.visibility = View.GONE
            return false
        }
    }

    private fun bindData() {
        viewModel.registerImageDataResult(this, { result ->
            updateImageData(result)
        })
    }

    private fun loadFragments(savedInstanceState: Bundle?) {
        fragmentMap.clear()
        if (savedInstanceState != null && viewModel.getLastData()?.getAllList()?.size?:0 > 0) {
            viewModel.getFragmentKeyList().forEachIndexed { index, key ->
                val fragment = supportFragmentManager.getFragment(savedInstanceState, key)
                fragmentMap[key.split("-")[1]] = fragment as GridItemFragment<ImageItem>
            }
        }
    }

    private fun updateImageData(imageData: CategorizedMediaData<ImageItem>) {
        val titles = imageData.getCategories()
        if (titles.isEmpty()) {
            cl_content.visibility = View.GONE
            tv_no_content.visibility = View.VISIBLE
            return
        } else {
            cl_content.visibility = View.VISIBLE
            tv_no_content.visibility = View.GONE
        }

        val fragmentList = arrayListOf<GridItemFragment<ImageItem>>()
        titles.forEach {
            if (!fragmentMap.containsKey(it)) {
                fragmentMap[it] = GridItemFragment.newInstance("image-${it}")
            }
            fragmentList.add(fragmentMap[it]!!)
        }
        vp_fragments.adapter = CustomFragmentPageAdapter(supportFragmentManager, fragmentList)
        tl_category.setViewPager(vp_fragments, titles.toTypedArray())

        //make sure tl_category onPageSelected is called to make first tab text Bold
        tl_category.setCurrentTab(1, false)
        tl_category.setCurrentTab(0, false)
    }
}