package com.jaryd.videocodec

object CodecConfig {
    internal val WIDTH = 480
    internal val HEIGHT = 640
    internal val BITRATE = WIDTH* HEIGHT*5
    internal val FRAME_RATE = 30
    internal val I_FRAME = 1
    internal val MIME = "video/avc"

    fun getWidth():Int{
        return WIDTH
    }

    fun getHeight():Int{
        return HEIGHT
    }

    fun getBitRate():Int{
        return BITRATE
    }
}