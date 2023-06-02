package com.example.filelocal.video

import android.net.Uri
import com.example.filelocal.media.LocalMediaItem
import kotlinx.android.parcel.Parcelize

@Parcelize
open class VideoItem(
    override var id: Long,
    override var title: String,
    override var mimeType: String,
    override var size: Int,
    override var album: String,
    override var createDate: Int,
    override var year: Int,
    override var localUri: Uri,
    override var localPath: String,
    open var duration: Long,
    open var width: Int,
    open var height: Int,
    open var orientation: Int,
    open var videoCodec: String,
    open var audioCodec: String,
    open var frameRate:Float
): LocalMediaItem(id, title, mimeType, size, album, createDate, year, localUri, localPath, false, ""), Comparable<VideoItem> {
    companion object {
        private var comparator: Comparator<VideoItem>? = null
        fun setComparator(comparator: Comparator<VideoItem>) {
            this.comparator = comparator
        }
    }
    override fun compareTo(other: VideoItem): Int {
        return comparator?.compare(this, other)?:this.title.compareTo(other.title)
    }
}