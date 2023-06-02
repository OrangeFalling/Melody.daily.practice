package com.example.connectmelody.util

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.net.UnknownHostException

object IpUtil {
    /**
     * get ipAddress.
     */
    @Throws(UnknownHostException::class)
    fun getIpAddress(context: Context): InetAddress? {
        val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        val ip = wifiInfo.ipAddress
        return if (ip == 0) {
            null
        } else {
            val ipAddress = convertIpAddress(ip)
            InetAddress.getByAddress(ipAddress)
        }
    }

    /**
     * convert the int type ip to byte type.
     */
    private fun convertIpAddress(ip: Int): ByteArray? {
        return byteArrayOf(
            (ip and 0xFF).toByte(),
            (ip shr 8 and 0xFF).toByte(),
            (ip shr 16 and 0xFF).toByte(),
            (ip shr 24 and 0xFF).toByte()
        )
    }
}