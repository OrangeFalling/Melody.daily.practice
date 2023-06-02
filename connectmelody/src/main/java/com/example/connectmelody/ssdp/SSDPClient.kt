package com.example.connectmelody.ssdp

import java.io.IOException
import java.net.*

class SSDPClient {
    lateinit var datagramSocket: DatagramSocket
    lateinit var multicastSocket: MulticastSocket
    lateinit var multicastGroup: SocketAddress
    lateinit var networkInterface: NetworkInterface
    lateinit var localInetAddress: InetAddress
    private var timeout: Int = 0
    companion object {
        const val PORT = 1900
        // TODO: why should it be set to it?
        const val MULTICAST_ADDRESS = "239.255.255.250"
        const val MSEARCH = "M-SEARCH * HTTP/1.1"
        const val NEWLINE = "\r\n"
        const val MX = 5
        const val NOTIFY = "NOTIFY * HTTP/1.1"
        fun getSSDPSearchMessage(st: String): String {
            val sb = StringBuilder()
            sb.append(MSEARCH + NEWLINE)
            sb.append("HOST: $MULTICAST_ADDRESS:$PORT$NEWLINE")
            sb.append("MAN: \"ssdp:discover\"$NEWLINE")
            sb.append("ST: ").append(st).append(NEWLINE)
            sb.append("MX: ").append(MX).append(NEWLINE)
            if (st.contains("udap")) {
                sb.append("USER-AGENT: UDAP/2.0$NEWLINE")
            }
            sb.append(NEWLINE)
            return sb.toString()
        }
    }


    @Throws(IOException::class)
    constructor(source: InetAddress): this(source, MulticastSocket(PORT), DatagramSocket(null)) {}

    constructor(source: InetAddress, mcSocket: MulticastSocket, dgSocket: DatagramSocket) {
        localInetAddress = source
        multicastSocket = mcSocket
        datagramSocket = dgSocket
        multicastGroup = InetSocketAddress(MULTICAST_ADDRESS, PORT)
        networkInterface = NetworkInterface.getByInetAddress(localInetAddress)
        multicastSocket.joinGroup(multicastGroup,networkInterface)
        datagramSocket.reuseAddress = true
        datagramSocket.bind(InetSocketAddress(localInetAddress, 0))
    }

    /**
     * used to send datagram packet.
     */
    @Throws(IOException::class)
    fun send(data: String) {
        val dp = DatagramPacket(data.toByteArray(), data.length, multicastGroup)
        datagramSocket.send(dp)
    }

    /**
     * used to receive ssdp response packet.
     */
    @Throws(IOException::class)
    fun responseReceive(): DatagramPacket {
        val buf = ByteArray(1024)
        val dp = DatagramPacket(buf, buf.size)
        datagramSocket.receive(dp)
        return dp
    }

    /**
     * used to receive ssdp multicast packet
     */
    fun multicastReceive(): DatagramPacket {
        val buf = ByteArray(1024)
        val dp = DatagramPacket(buf, buf.size)
        multicastSocket.receive(dp)
        return dp
    }

    /**
     * used to close the socket.
     */
    fun close() {
        if (multicastSocket != null) {
            try {
                multicastSocket.leaveGroup(multicastGroup, networkInterface)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (null != datagramSocket) {
            datagramSocket.close()
        }
    }

    fun isConnected(): Boolean {
        return datagramSocket != null
                && multicastSocket != null
                && datagramSocket.isConnected
                && multicastSocket.isConnected
    }

    /**
     * used to set timeout.
     */
    @Throws(SocketException::class)
    fun setTimeout(timeout: Int) {
        this.timeout = timeout
        datagramSocket.soTimeout = this.timeout
    }
}