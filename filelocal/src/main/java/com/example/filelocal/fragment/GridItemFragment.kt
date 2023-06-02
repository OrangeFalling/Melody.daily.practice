package com.example.filelocal.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.filelocal.image.ImageItem
import com.example.filelocal.media.MediaItem
import com.example.filelocal.R
import com.example.filelocal.adapter.BaseRcvAdapter
import com.example.filelocal.objects.SharedDataCache
import com.example.filelocal.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.fragment_grid_item.*
import kotlinx.android.synthetic.main.view_grid_item.view.*
import java.util.concurrent.CopyOnWriteArrayList

class GridItemFragment<T: MediaItem>: TabFragment() {
    companion object {
        private const val ITEM_LIST_KEY = "ITEM_LIST_KEY"
        fun <T: MediaItem> newInstance(listKey: String): GridItemFragment<T> {
            val fragment = GridItemFragment<T>()
            val bundle = Bundle()
            bundle.putString(ITEM_LIST_KEY, listKey)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val itemList = CopyOnWriteArrayList<T>()
    private val imageAdapter = BaseRcvAdapter(mapOf(ImageViewHolder::class.java to R.layout.view_grid_item))


    override fun getLayoutRes(): Int {
        return R.layout.fragment_grid_item
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList.clear()
        if (arguments != null) {
            val listKey = arguments?.getString(ITEM_LIST_KEY)?:""
            val list = SharedDataCache.getObject(listKey)
            if (list != null) {
                itemList.addAll(list as Collection<T>)
            }
        }
        initViews()
    }

    private fun initViews() {
        if (itemList.isEmpty()) {
            return
        }

        if (itemList[0] is ImageItem) {
            imageAdapter.setDatas(itemList)
            imageAdapter.setOnItemClickListener { view, data ->
//                onItemClickListener?.invoke(data as T)
            }
            rv_grid.adapter = imageAdapter
        }
        rv_grid.layoutManager = GridLayoutManager(context, 3)
    }

    class ImageViewHolder(itemView: View): BaseViewHolder<ImageItem>(itemView) {
        override fun bindView(data: ImageItem) {
            Glide.with(itemView.context)
                .asBitmap()
                .apply(RequestOptions())
                .load(data.localUri)
                .into(itemView.iv_pic)
            itemView.tv_create_time.visibility = View.VISIBLE
            itemView.tv_create_time.text = data.title
        }
    }
}