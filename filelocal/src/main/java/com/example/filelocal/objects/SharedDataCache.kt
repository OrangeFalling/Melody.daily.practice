package com.example.filelocal.objects

object SharedDataCache {
    private val data = hashMapOf<String, Any>()

    fun putObject(key: String, obj: Any) {
        data[key] = obj
    }

    fun removeObject(key: String) {
        if (data.containsKey(key)) {
            data.remove(key)
        }
    }

    fun getObject(key: String): Any? {
        return data[key]
    }
}