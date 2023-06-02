package com.example.connectmelody.needToDo.json

import org.json.JSONException
import org.json.JSONObject

interface JSONDeserializable {
    @Throws(JSONException::class)
    fun fromJSONObject(obj: JSONObject?)
}