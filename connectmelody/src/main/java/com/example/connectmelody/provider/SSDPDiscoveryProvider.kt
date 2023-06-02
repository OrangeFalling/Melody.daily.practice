package com.example.connectmelody.provider

import android.content.Context
import com.example.connectmelody.needToDo.discovery.DiscoveryProvider
import com.example.connectmelody.needToDo.discovery.DiscoveryProvider.Companion.RESCAN_INTERVAL
import com.example.connectmelody.needToDo.discovery.DiscoveryProvider.Companion.TIMEOUT
import com.example.connectmelody.needToDo.discovery.DiscoveryProviderListener
import com.example.connectmelody.service.ServiceDescription
import com.example.connectmelody.ssdp.SSDPClient
import com.example.connectmelody.ssdp.SSDPPacket
import com.example.connectmelody.util.IpUtil
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern

class SSDPDiscoveryProvider(private var context: Context): DiscoveryProvider {
    private var uuidReg: Pattern = Pattern.compile("(?<=uuid:)(.+?)(?=(::)|$)")
    private var serviceListeners = CopyOnWriteArrayList<DiscoveryProviderListener>()
    private var ssdpClient: SSDPClient? = null
    private var isRunning = false
    private var scanTimer: Timer? = null
    private var responseThread: Thread? = null
    private var notifyThread: Thread? = null
    private var foundServices = ConcurrentHashMap<String, ServiceDescription>()

    private val mResponseHandler: Runnable = Runnable {
        while (ssdpClient != null) {
            try {
                handleSSDPPacket(SSDPPacket(ssdpClient!!.responseReceive()))
            } catch (e: IOException) {
                e.printStackTrace()
                break
            } catch (e: RuntimeException) {
                e.printStackTrace()
                break
            }
        }
    }
    private val mRespNotifyHandler: Runnable = Runnable {
        while (ssdpClient != null) {
            try {
                handleSSDPPacket(SSDPPacket(ssdpClient!!.multicastReceive()))
            } catch (e: IOException) {
                e.printStackTrace()
                break
            } catch (e: RuntimeException) {
                e.printStackTrace()
                break
            }
        }
    }

    override fun start() {
        if (isRunning) return
        isRunning = true
        openSocket()
        scanTimer = Timer()
        scanTimer?.schedule(object : TimerTask() {
            override fun run() {
                sendSearch()
            }

        }, 100, RESCAN_INTERVAL)
        responseThread = Thread(mResponseHandler)
        notifyThread = Thread(mRespNotifyHandler)
        responseThread?.start()
        notifyThread?.start()
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun restart() {
        TODO("Not yet implemented")
    }

    override fun rescan() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override fun addListener() {
        TODO("Not yet implemented")
    }

    override fun removeListener() {
        TODO("Not yet implemented")
    }

    override fun addDeviceFilter() {
        TODO("Not yet implemented")
    }

    override fun removeDeviceFilter() {
        TODO("Not yet implemented")
    }

    override fun setFilter() {
        TODO("Not yet implemented")
    }

    override fun isEmpty() {
        TODO("Not yet implemented")
    }

    private fun handleSSDPPacket(ssdpPacket: SSDPPacket?) {
        if (ssdpPacket == null || ssdpPacket.getData().isEmpty() || ssdpPacket.getType() == null)
            return
        val serviceFilter =
            ssdpPacket.getData()[if (ssdpPacket.getType() != (SSDPClient.NOTIFY)) "ST" else "NT"]
        if (serviceFilter == null || ssdpPacket.getType() == SSDPClient.MSEARCH)
            return

    }

    /**
     *
     */
    fun sendSearch() {
        val killKeys: MutableList<String> = ArrayList()
        val killPoint = Date().time - TIMEOUT
        for (key in foundServices.keys) {
            val service: ServiceDescription? = foundServices[key]
            if (service == null || service.lastDetection < killPoint) {
                killKeys.add(key)
            }
        }
        for (key in killKeys) {
            val service = foundServices[key]
            if (service != null) {

            }
            if (foundServices.contains(key)) {
                foundServices.remove(key)
            }
        }
        rescan()
    }

    /**
     * init new a instance of ssdpClient.
     */
    private fun openSocket() {
        if (ssdpClient != null && ssdpClient!!.isConnected()) return
        try {
            val source: InetAddress = IpUtil.getIpAddress(context) ?: return
            ssdpClient = createSocket(source)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * new a ssdpClient.
     */
    @Throws(IOException::class)
    fun createSocket(source: InetAddress): SSDPClient {
        return SSDPClient(source)
    }
}