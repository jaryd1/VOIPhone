package com.jaryd.videocodec

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class H264Frame(val buffer: ByteBuffer,val size:Int,
                        val pts:Long,val flags:Int)

fun H264Frame.toBytes():ByteArray{
    val data = ByteArray(size)
    buffer.get(data)
    return ByteBuffer.allocate(size+16)
        .order(ByteOrder.LITTLE_ENDIAN)
//        .putInt(size)
        .put(data)
        .putLong(pts)
        .putInt(flags)
        .putInt(size)
        .array()
}

