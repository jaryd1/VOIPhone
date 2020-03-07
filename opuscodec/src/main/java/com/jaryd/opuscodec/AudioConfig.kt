package com.jaryd.opuscodec

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 *     sample_rate channels bitrate
* byte     4          1       4
 */
data class AudioConfig( val sample_rete:Int, val channels:Int,
                   val bitrate:Int) {
    fun toBytes():ByteArray{
        return ByteBuffer.allocateDirect(9)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(sample_rete)
            .put(channels.toByte())
            .putInt(bitrate)
            .array()
    }

    companion object{
        fun parseAudioConfig(input:ByteArray):AudioConfig{
            return AudioConfig(ByteBuffer.allocate(4)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .put(input,0,4)
                            .getInt(0),
                             input[4].toInt(),
                            ByteBuffer.allocate(4)
                                .order(ByteOrder.LITTLE_ENDIAN)
                                .put(input,5,9)
                                .getInt(0))
        }

        fun getDefaultConfig():AudioConfig{
            return AudioConfig(48000,1,64000)
        }
    }
}