package com.example.filelocal.media

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class MediaLoader<T>(private val appContext: Context) {
    var filter: ((item: T)->Boolean)? = null

    fun setItemFilter(filter: (T) -> Boolean) {
        this.filter = filter
    }

    @Synchronized
    protected suspend fun loadLocalMedia(columnTypeMap: Map<String, Class<*>>,
                               orderColumn: String,
                               desc: Boolean,
                               mediaLOadResultCallback: MediaLoadResultCallback<T>
    ) {
        /**
         * withContext
         * 会阻塞线程
         * 指定运行的线程
         * 返回最后一行的值
         * 必须在协程或者suspend函数中调用
         */
        withContext(Dispatchers.IO) {
            val itemList = mutableListOf<T>()
            val uri = getUri()
            val sortOrder = "$orderColumn ${if (desc) "DESC" else "ASC"}"
            val query = appContext.contentResolver.query(uri, null,
                null, null, sortOrder)
            query?.use { cursor ->
                val columnIndexTypeMap = mutableMapOf<Int, Class<*>>()
                val columnIndexColumnMap = mutableMapOf<Int, String>()
                columnTypeMap.entries.forEach {
                    val index = cursor.getColumnIndex(it.key)
                    if (index >= 0) {
                        columnIndexColumnMap[index] = it.key
                        columnIndexTypeMap[index] = it.value
                    }
                }
                val dataMap = mutableMapOf<String, Any?>()
                while (cursor.moveToNext()) {
                    columnIndexTypeMap.entries.forEach {
                        val value = when (it.value) {
                            Int::class.java -> cursor.getInt(it.key)
                            String::class.java -> cursor.getString(it.key)
                            Long::class.java -> cursor.getLong(it.key)
                            else -> null
                        }
                        val column = columnIndexColumnMap[it.key]
                        dataMap[column?:""] = value
                    }
                    val item = createItem(dataMap)
                    if (filter == null || filter?.invoke(item) == false) {
                        itemList.add(item)
                    }
                }
                mediaLOadResultCallback.onMediaLoadFinish(itemList)
            }
        }
    }

    abstract fun createItem(dataMap: Map<String, Any?>): T
    abstract fun getUri(): Uri

    interface MediaLoadResultCallback<T> {
        fun onMediaLoadFinish(result: List<T>)
    }
}
