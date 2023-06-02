package com.example.filelocal.image

import android.net.Uri
import com.example.filelocal.media.LocalMediaItem
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ImageItem(
    override var id: Long,
    override var title: String,
    override var mimeType: String,
    override var size: Int,
    override var album: String,
    override var createDate: Int,
    override var year: Int,
    override var localUri: Uri,
    override var localPath: String,
    open var width: Int,
    open var height: Int,
    open var orientation: Int,
): LocalMediaItem(id, title, mimeType, size, album, createDate, year, localUri, localPath, false, ""), Comparable<ImageItem> {
    companion object {
        private var comparator: Comparator<ImageItem>? = null
        fun setComparator(comparator: Comparator<ImageItem>) {
            Companion.comparator = comparator
        }
    }
    override fun compareTo(other: ImageItem): Int {
        return comparator?.compare(this, other)?:this.title.compareTo(other.title)
    }
}
