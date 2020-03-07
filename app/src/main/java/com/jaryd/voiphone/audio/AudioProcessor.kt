package com.jaryd.voiphone.audio

import com.jaryd.opuscodec.AudioConfig

class AudioProcessor {
    private val TAG = "AudioProcessor"
    private var mContext = 0L

    private lateinit var audio_config:AudioConfig


    init {
        System.loadLibrary("voip")

    }

    fun init(config: AudioConfig,sample_size: Int){
        mContext = _init(config.sample_rete,config.channels,sample_size)
        audio_config = config
    }


    fun process(sample:ShortArray,size:Int):Int{
        val result =  _process(mContext,sample, size)
        return result
    }



    fun putPlayBack(echo: ShortArray){
        _putPlayback(mContext,echo)
    }

    fun cancle(input:ShortArray,size:Int,echo: ShortArray){
        _cancle(mContext,input, size, echo)
    }

    private external fun _init(sample_rate:Int,channels:Int,sample_size:Int):Long
    private external fun _process(context:Long,sample: ShortArray,size: Int):Int
    private external fun _putPlayback(context:Long,input:ShortArray)
    private external fun _cancle(context: Long,input: ShortArray,size: Int,echo: ShortArray);
}