package com.example.filelocal.media

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class MediaItem(
    open var id: Long,
    open var title: String,
    open var mimeType: String,
    open var size: Int,
): Parcelable