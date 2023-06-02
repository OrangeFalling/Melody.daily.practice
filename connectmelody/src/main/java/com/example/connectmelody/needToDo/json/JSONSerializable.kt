package com.example.connectmelody.needToDo.json

import org.json.JSONException
import org.json.JSONObject

/**
 * function: toJSONObject()
 */
interface JSONSerializable {
    @Throws(JSONException::class)
    fun toJSONObject(): JSONObject?
}