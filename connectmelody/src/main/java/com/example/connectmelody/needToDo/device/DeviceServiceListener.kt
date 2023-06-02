package com.example.connectmelody.needToDo.device

import com.example.connectmelody.service.DeviceService

interface DeviceServiceListener {
    fun onConnectionRequired(service: DeviceService)

}