package com.example.filelocal.image

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.filelocal.media.MediaLoader

class ImageLoader(appContext: Context): MediaLoader<ImageItem>(appContext) {

    suspend fun loadImages(mediaLoadResultCallback: MediaLoadResultCallback<ImageItem>) {
        loadLocalMedia(mapOf(
            MediaStore.Images.Media.DATA to String::class.java,
            MediaStore.Images.Media._ID to Long::class.java,
            MediaStore.Images.Media.DISPLAY_NAME to String::class.java,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME to String::class.java,
            MediaStore.Images.Media.MIME_TYPE to String::class.java,
            MediaStore.Images.Media.SIZE to Int::class.java,
            MediaStore.Images.Media.DATE_ADDED to Int::class.java,
            MediaStore.Audio.Media.YEAR to Int::class.java,
            MediaStore.Images.Media.WIDTH to Int::class.java,
            MediaStore.Images.Media.HEIGHT to Int::class.java,
            MediaStore.Images.Media.HEIGHT to Int::class.java,
            MediaStore.Images.Media.ORIENTATION to Int::class.java
        ), MediaStore.Images.Media.DATE_ADDED, true, mediaLoadResultCallback)
    }

    override fun createItem(dataMap: Map<String, Any?>): ImageItem {
        val id = dataMap[MediaStore.Images.Media._ID] as Long?
        val dataUri = ContentUris.withAppendedId(getUri(), id?:0)
        return ImageItem(
            id?:0,
            (dataMap[MediaStore.Images.Media.DISPLAY_NAME]?:"") as String,
            (dataMap[MediaStore.Images.Media.MIME_TYPE]?:"") as String,
            (dataMap[MediaStore.Images.Media.SIZE]?:0) as Int,
            (dataMap[MediaStore.Images.Media.BUCKET_DISPLAY_NAME]?:"") as String,
            (dataMap[MediaStore.Images.Media.DATE_ADDED]?:0) as Int,
            (dataMap[MediaStore.Audio.Media.YEAR]?:0) as Int,
            dataUri,
            (dataMap[MediaStore.Images.Media.DATA]?:"") as String,
            (dataMap[MediaStore.Images.Media.WIDTH]?:0) as Int,
            (dataMap[MediaStore.Images.Media.HEIGHT]?:0) as Int,
            (dataMap[MediaStore.Images.Media.ORIENTATION] ?: 0) as Int
        )
    }

    override fun getUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }
}