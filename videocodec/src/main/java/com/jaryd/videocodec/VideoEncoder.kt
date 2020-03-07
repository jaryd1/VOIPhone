package com.jaryd.videocodec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Environment
import android.util.Log
import android.view.Surface
import java.io.BufferedOutputStream
import java.io.File

class VideoEncoder {

    private val TAG = "VideoCodec"
    private var initialized = false
    private lateinit var encoder:MediaCodec

    private lateinit var encodeBufferInfo: MediaCodec.BufferInfo

    private val TIMEOUT = 10000L //10ms

     var inputSurface: Surface? = null

    var encodeCallBack:((frame:H264Frame) ->Unit)? = null

    private var count = 0

    private lateinit var file: File
    private lateinit var outputStream: BufferedOutputStream
    private val bufferCallBack = object :MediaCodec.Callback(){
        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
            val output = codec.getOutputBuffer(index)!!
            output.position(info.offset)
            output.limit(info.offset+info.size)
            encodeCallBack?.invoke(H264Frame(output,info.size,info.presentationTimeUs,info.flags))
            codec.releaseOutputBuffer(index,false)
            count++
        }

        override fun onInputBufferAvailable(codec: MediaCodec, index: Int)=Unit

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {

            Log.e(TAG,"output format "+format)
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            Log.e(TAG,e.toString())
        }

    }




     fun initEncoder(width:Int,height:Int,bitrate:Int){
        if(initialized) return

        encoder = MediaCodec.createEncoderByType(CodecConfig.MIME)
        val format = MediaFormat.createVideoFormat(CodecConfig.MIME,width,height)
        format.setInteger(MediaFormat.KEY_BIT_RATE,bitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, CodecConfig.FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, CodecConfig.I_FRAME)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        encoder.setCallback(bufferCallBack)
        encoder.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE)
        inputSurface = encoder.createInputSurface()
        encodeBufferInfo = MediaCodec.BufferInfo()
        initialized = true
         file = File(Environment.getExternalStorageDirectory(),"config.h264")
         outputStream = BufferedOutputStream(file.outputStream())

    }


    fun addCallBack(callBack:(frame:H264Frame) ->Unit){
        encodeCallBack = callBack
    }

    fun startEncode(){
        if(!initialized) return
        encoder.start()
    }

    fun stopEncode(){
        encoder.stop()
        inputSurface?.release()
        encoder.release()
    }


}