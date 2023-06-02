package com.example.filelocal.viewmodel

import androidx.lifecycle.*
import com.example.filelocal.*
import com.example.filelocal.image.ImageItem
import com.example.filelocal.image.ImageLoader
import com.example.filelocal.media.CategorizedMediaData
import com.example.filelocal.media.MediaLoader
import com.example.filelocal.objects.SharedDataCache
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class ImageBrowseViewModel: ViewModel() {
    private val imageLoader = ImageLoader(LocalApp.get())
    private val fragmentKeyList = CopyOnWriteArrayList<String>()
    private var lastData: CategorizedMediaData<ImageItem>? = null

    private val imageDataLiveData = MutableLiveData<CategorizedMediaData<ImageItem>>()

    init {
        imageLoader.setItemFilter {
            it.size == 0
        }
        ImageItem.setComparator { o1, o2 ->
            o2.createDate.compareTo(o1.createDate)
        }
    }

    override fun onCleared() {
        super.onCleared()
        lastData?.getCategories()?.forEach {
            SharedDataCache.removeObject("image-${it}")
        }
    }

    fun loadImages() {
        viewModelScope.launch {
            imageLoader.loadImages(object : MediaLoader.MediaLoadResultCallback<ImageItem> {
                override fun onMediaLoadFinish(result: List<ImageItem>) {
                    val splitList = splitImageList(result.toMutableList())
                    splitList.getCategories().forEach {
                        SharedDataCache.putObject("image-${it}", splitList.getCategoryList(it))
                    }
                    lastData = splitList
                    imageDataLiveData.postValue(splitList)
                }
            })
        }
    }

    private fun splitImageList(imageList: MutableList<ImageItem>): CategorizedMediaData<ImageItem> {
        sortLists(imageList)
        val imageData = CategorizedMediaData<ImageItem>()
        val allKeyStr = "ALL PHOTOS"
        imageList.forEach {
            if (it.size == 0 || it.width == 0
                || it.height == 0
                || it.localPath.endsWith(".jp2", true)
                || it.localPath.endsWith(".tiff", true)) {
                return@forEach
            }
            imageData.addOnTopCategory(allKeyStr)
            imageData.putItem(allKeyStr, it)
            val album = it.album
            if (album.isNotEmpty()) {
                imageData.putItem(album, it)
            }
        }
        return imageData
    }

    private fun sortLists(imageList: MutableList<ImageItem>) {
        imageList.sortWith { item1, item2 ->
            item1.compareTo(item2)
        }
    }

    fun getFragmentKeyList(): List<String> {
        return fragmentKeyList
    }

    fun getLastData(): CategorizedMediaData<ImageItem>? {
        return lastData
    }

    fun registerImageDataResult(lifecycleOwner: LifecycleOwner, observer: Observer<CategorizedMediaData<ImageItem>>) {
        imageDataLiveData.observe(lifecycleOwner, observer)
    }

    fun putFragmentKeyList(keyList: List<String>) {
        fragmentKeyList.clear()
        fragmentKeyList.addAll(keyList)
    }
}