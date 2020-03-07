package com.jaryd.videocodec

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class VideoDecoder {
    private val TAG ="VideoDecoder"
    private var initialized = false

    private lateinit var decoder: MediaCodec

    private lateinit var decodeBufferInfo: MediaCodec.BufferInfo

    private val TIMEOUT = 400000L //10ms

    private val frameQueues = LinkedList<H264Frame>()
    @Volatile
    private var mDecoding = false

    private val lock = ReentrantLock()
    private val condition by lazy { lock.newCondition() }
    private val execute by lazy { Executors.newSingleThreadExecutor() }
    private val bufferCallBack = object :MediaCodec.Callback(){
        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
            codec.releaseOutputBuffer(index,true)
        }

        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
            if(!mDecoding)
                return
            execute.execute {
                val frame = getFrame()
                if(mDecoding) {
                    val buffer = codec.getInputBuffer(index)!!
                    buffer.put(frame.buffer)
                    codec.queueInputBuffer(index, 0, frame.size, frame.pts, frame.flags)
                }
            }
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            Log.e(TAG,"format change "+format)
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            Log.e(TAG,e.toString())
        }

    }

    fun initDecoder(width:Int,height:Int,surface:Surface){
        if(initialized) return

        decoder = MediaCodec.createDecoderByType(CodecConfig.MIME)
        val format = MediaFormat.createVideoFormat(CodecConfig.MIME,width,height)
        decoder.setCallback(bufferCallBack)
        decoder.configure(format,surface,null,0)
        decodeBufferInfo = MediaCodec.BufferInfo()
        initialized = true
        mDecoding = true
        decoder.start()
    }

    fun stopDecode(){
        mDecoding = false
        decoder.stop()
        decoder.release()
    }

    private fun getFrame():H264Frame{
        lock.withLock {
            if(frameQueues.isEmpty()){
                condition.await()
            }
        }
        return frameQueues.pop()
    }
    
    fun putFrame(frame: H264Frame){
        frameQueues.addLast(frame)
        lock.withLock {
            condition.signal()
        }
    }

    fun parse(data:ByteArray):H264Frame{
        val dataBuffer = ByteBuffer.allocate(data.size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(data)
//        dataBuffer.position(0)
        dataBuffer.position(data.size-4)
        val size = dataBuffer.getInt()
        var bytes = ByteArray(size)
        dataBuffer.position(0)
//        dataBuffer.position(4)
        dataBuffer.get(bytes,0,size)
        val buffer = ByteBuffer.wrap(bytes)
        dataBuffer.position(size)
//        dataBuffer.position(4+size)
        val pts = dataBuffer.getLong()
        dataBuffer.position(8+size)
//        dataBuffer.position(12+size)
        val flags = dataBuffer.getInt()

        return H264Frame(buffer, size, pts, flags)
    }
}