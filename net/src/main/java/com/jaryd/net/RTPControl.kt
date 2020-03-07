package com.jaryd.net

object RTPControl {
    private  var rtp:RTPManager? = null
    fun init(callback:(packet:NetPacket) ->Unit){
        rtp = RTPManager()
        rtp!!.init(callback)
    }

    fun listen(){
        rtp!!.listen(NetConfig.RTP_VIDEO_PORT,NetConfig.RTP_AUDIO_PORT)
    }

    fun send(packet:NetPacket){
        if(packet.isAudioPacket()){
            rtp!!.sendOgg(packet.buffer!!, packet.addr!!, NetConfig.RTP_AUDIO_PORT)
        }else{
            rtp!!.sendH264(packet.buffer!!, packet.addr!!, NetConfig.RTP_VIDEO_PORT)
        }

    }

    fun release(){
        rtp?.destroy()
        rtp = null
    }

}