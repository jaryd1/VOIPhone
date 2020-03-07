package com.jaryd.net

import android.util.Log

class RTPManager {

    init {
        System.loadLibrary("rtp")
    }


    private  var mContext = 0L
    private lateinit var callBack:(packet:NetPacket)->Unit

    fun init(callback:(packet:NetPacket) -> Unit){
        mContext = _init()
        this.callBack = callback
    }


    fun listen(video_port: Int,audio_port:Int){
        _listen(mContext,video_port,audio_port)
    }

    fun sendH264(buffer: ByteArray,addr: String,port: Int){
        _sendH264(mContext,buffer,buffer.size,addr, port)
    }

    fun sendOgg(buffer: ByteArray,addr: String,port: Int){
        _sendOgg(mContext,buffer,buffer.size,addr, port)
    }

    private fun getResponseH264(buffer:ByteArray){
          callBack.invoke(NetPacketUtils.getVideoCallingPacket(buffer,buffer.size,"",true))
    }

    private fun getResponseOgg(buffer:ByteArray){
        callBack.invoke(NetPacketUtils.getAudioCallingPacket(buffer,buffer.size,"",true))
    }

    fun destroy(){
        _destroy(mContext)
        mContext = -1
    }

    private external fun _init():Long
    private external fun _sendH264(context:Long,buffer:ByteArray,size: Int,addr:String,port:Int)
    private external fun _sendOgg(context:Long,buffer:ByteArray,size: Int,addr:String,port:Int)
    private external fun _listen(context: Long,port:Int,audioPort:Int)
    private external fun _destroy(context: Long)
}