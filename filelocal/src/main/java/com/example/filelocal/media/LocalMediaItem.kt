package com.example.filelocal.media

import android.net.Uri
import com.example.filelocal.media.MediaItem
import kotlinx.android.parcel.Parcelize

@Parcelize
open class LocalMediaItem(override var id: Long,
                          override var title: String,
                          override var mimeType: String,
                          override var size: Int,
                          open var album: String,
                          open var createDate: Int,
                          open val year: Int,
                          open var localUri: Uri,
                          open var localPath: String,
                          open var isTranscoded: Boolean,
                          open var transcodedPath: String,
): MediaItem(id, title, mimeType, size)