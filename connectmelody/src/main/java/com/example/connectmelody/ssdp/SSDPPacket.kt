package com.example.connectmelody.ssdp

import java.net.DatagramPacket
import java.nio.charset.Charset

class SSDPPacket(datagramPacket: DatagramPacket) {
    companion object {
        val ASCII_CHARSET: Charset = Charset.forName("US-ASCII")
        const val CRLF = "\r\n"
        const val LF = "\n"

        /**
         * fast uppercase for ASCII strings.
         */
        fun asciiUpper(text: String): String {
            val chars = text.toCharArray()
            for (i in chars.indices) {
                val c = chars[i]
                chars[i] = if (c.toInt() in 97..122) (c.toInt() - 32).toChar() else c
            }
            return String(chars)
        }
    }
    private var datagramPacket: DatagramPacket? = datagramPacket
    private var type: String? = null
    private var data: MutableMap<String, String> = HashMap()

    init {
        init()
    }

    /**
     * datagramPacket type to string.
     * set type.
     * set data.
     */
    private fun init() {
        if (datagramPacket == null) return
        val text = String(datagramPacket!!.data, ASCII_CHARSET)
        var pos = 0
        var eolPos: Int
        when {
            text.indexOf(CRLF).also { eolPos = it } != -1 -> pos = eolPos + CRLF.length
            text.indexOf(LF).also { eolPos = it } != -1 -> pos = eolPos + LF.length
            else -> return
        }
        type = text.substring(0, eolPos)
        while (pos < text.length) {
            var line: String
            if (text.indexOf(CRLF, pos).also { eolPos = it } != -1) {
                line = text.substring(pos, eolPos)
                pos = eolPos + CRLF.length
            } else if (text.indexOf(LF, pos).also { eolPos = it } != -1) {
                line = text.substring(pos, eolPos)
                pos = eolPos + LF.length
            } else break
            val index = line.indexOf(':')
            if (index == -1) {
                continue
            }
            val key: String = asciiUpper(line.substring(0, index))
            val value = line.substring(index + 1).trim { it <= ' ' }
            data[key] = value
        }
    }

    @JvmName("getDatagramPacket1")
    fun getDatagramPacket(): DatagramPacket? {
        return datagramPacket
    }

    fun getData(): Map<String, String> {
        return data
    }

    fun getType(): String? {
        return type
    }
}