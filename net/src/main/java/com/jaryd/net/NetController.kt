package com.jaryd.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NetController {

    private var udpClient:UdpClient? = null
    private var tcpClient:TcpClient? = null

    fun initUdp(becall:Boolean){
        if( udpClient != null){
            throw RuntimeException("udp has init")
        }
        udpClient = UdpClient()
        udpClient!!.init(if (becall == false) NetConfig.UDP_BECALL_PORT else NetConfig.UDP_PORT)
    }

    fun initTcp(){
        tcpClient = TcpClient()
    }

    suspend fun listenTcp():NetPacket = withContext(Dispatchers.IO){
        tcpClient!!.listen()
    }


    suspend fun listen():NetPacket? = withContext(Dispatchers.IO){
        val receivePacket = udpClient!!.listen()
        val result = NetPacketUtils.parsePacket(receivePacket.data,receivePacket.offset,
            receivePacket.length,receivePacket.address.hostAddress)
        udpClient!!.resetPacket()
        result
    }

     fun close(){
        udpClient?.close()
        udpClient = null
        tcpClient?.close()
         tcpClient = null
    }

    suspend fun send(packet: NetPacket)= withContext(Dispatchers.IO){
            if (packet.signal == NetPacketUtils.CALLING)
                udpClient!!.send(packet)
            else
                tcpClient!!.send(packet)
    }


}