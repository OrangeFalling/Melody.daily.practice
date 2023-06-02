package com.example.filelocal.media

import java.util.concurrent.CopyOnWriteArrayList

class CategorizedMediaData<T> {
    private val dataList = CopyOnWriteArrayList<Pair<String, ArrayList<T>>>()

    fun addOnTopCategory(category: String) {
        if (dataList.isEmpty())
            dataList.add(Pair(category, arrayListOf()))
    }

    fun putItem(category: String, item: T) {
        var hasCategory = false
        dataList.forEach {
            if (it.first == category) {
                it.second.add(item)
                hasCategory = true
            }
        }
        if (!hasCategory) {
            dataList.add(Pair(category, arrayListOf(item)))
        }
    }

    fun getAllList(): ArrayList<List<T>> {
        val outList = arrayListOf<List<T>>()
        synchronized(dataList) {
            dataList.forEach {
                outList.add(it.second)
            }
        }
        return outList
    }

    fun getCategories(): ArrayList<String> {
        val outList = arrayListOf<String>()
        synchronized(dataList) {
            dataList.forEach {
                outList.add(it.first)
            }
        }
        return outList
    }

    fun getCategoryList(category: String): ArrayList<T> {
        synchronized(dataList) {
            dataList.forEach {
                if (it.first == category) {
                    return it.second
                }
            }
        }
        return arrayListOf()
    }

}