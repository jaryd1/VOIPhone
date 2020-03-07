package com.jaryd.net

import android.util.Log
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

class TcpClient {
    var client:Socket? = null
    lateinit var server:ServerSocket
    private val BUFFER_LENGTH =1024
    lateinit var buffer:ByteArray
    init {
        server = ServerSocket(NetConfig.TCP_PORT)
        buffer = ByteArray(BUFFER_LENGTH)
    }

    fun listen():NetPacket{
        val socket = server.accept()
        val len = socket.getInputStream().read(buffer)
        return NetPacketUtils.parsePacket(buffer,0,len,socket.inetAddress.hostAddress)
    }

    fun close(){
        server.close()
    }

    fun send(packet: NetPacket){
        try {
            client = Socket(InetAddress.getByName(packet.addr), packet.port)
        }catch (e:Exception){
            Log.e("connect",e.toString())
            return
        }
        if(client?.isConnected == true) {
            client?.getOutputStream()?.write(packet.toBytes()!!)
            client?.getOutputStream()?.flush()
            client?.close()
        }

    }
}