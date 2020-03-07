package com.jaryd.voiphone.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jaryd.net.NetController
import com.jaryd.net.NetPacket
import com.jaryd.net.NetPacketUtils
import com.jaryd.net.RTPControl
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainViewModel :ViewModel(){
    val toCallingActivity = MutableLiveData(false)
    var beCalled = false
    var addr = ""
    fun call(addr:String){
//        this.addr = addr
//        beCalled = false
//        toCallingActivity.value = true
        testcall()


    }





    private fun testcall(){
        runBlocking {
            NetController.send(NetPacketUtils.getCallRequestPacket("127.0.0.1"))
        }
//        RTPControl.send(
//            NetPacketUtils.getAudioCallingPacket(
//                ByteArray(10),10,
//                "127.0.0.1",false
//            )
//        )
//        RTPControl.send(
//            NetPacketUtils.getVideoCallingPacket(
//                ByteArray(10),10,
//                "127.0.0.1",false
//            )
//        )
    }

    fun handlePacket(packet: NetPacket){
        if(packet.signal == NetPacketUtils.REQUEST){
            beCalled = true
            addr = packet.addr!!
            toCallingActivity.value = true
        }else{
            Log.e("rtp","handle packet")
        }
    }
}