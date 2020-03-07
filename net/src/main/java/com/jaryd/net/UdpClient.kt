package com.jaryd.net

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpClient {
    private lateinit var packet:DatagramPacket
    private lateinit var client:DatagramSocket
    private val BUFFER_LENGTH =64*1024


    fun init(port:Int){
        client = DatagramSocket(port)
        packet = DatagramPacket(ByteArray(BUFFER_LENGTH),BUFFER_LENGTH)
    }

    fun listen():DatagramPacket{
        client.receive(packet)
        return packet
    }

    fun resetPacket(){
        packet.length = BUFFER_LENGTH
    }

    fun close(){
        client.close()
    }

    fun send(packet: NetPacket){
        val data = packet.toBytes()!!
        client.send(DatagramPacket(data,
            data.size, InetAddress.getByName(packet.addr),packet.port))
    }

}