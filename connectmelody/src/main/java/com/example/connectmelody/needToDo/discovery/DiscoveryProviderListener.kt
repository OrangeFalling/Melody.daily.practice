package com.example.connectmelody.needToDo.discovery

interface DiscoveryProviderListener {
    fun onServiceAdded()
    fun onServiceRemoved()
    fun onServiceDiscoveryFailed()
}