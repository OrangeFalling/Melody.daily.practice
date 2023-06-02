package com.example.connectmelody.needToDo.discovery

interface DiscoveryProvider {
    companion object {
        public const val RESCAN_INTERVAL: Long = 10000
        public const val RESCAN_ATTEMPTS = 6
        public const val TIMEOUT = RESCAN_INTERVAL * RESCAN_ATTEMPTS
    }
    fun start()
    fun stop()
    fun restart()
    fun rescan()
    fun reset()
    fun addListener()
    fun removeListener()
    fun addDeviceFilter()
    fun removeDeviceFilter()
    fun setFilter()
    fun isEmpty()
}