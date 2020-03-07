package com.jaryd.opuscodec

class OpusUtils {
    private var mContext = 0L
    private var mConfig:AudioConfig?= null

    init {
        System.loadLibrary("opusutils")
    }


    fun init(config: AudioConfig){

        mConfig = config
        mContext = _init(config.sample_rete,config.channels,config.bitrate)
    }

    fun encode(sample:ShortArray, sample_size:Int, output:ByteArray,output_max_len:Int):Int{
        return _encode(mContext,sample, sample_size,output,output_max_len)
    }

    fun decode(input:ByteArray, len:Int, output:ShortArray,output_max_sample:Int):Int{
        return _decode(mContext,input, len, output,output_max_sample)
    }





    private external fun _init(sample_rate:Int,channels:Int,bitrate:Int):Long
    private external fun _encode(context:Long,input:ShortArray,sample_size:Int,output: ByteArray,output_max_len: Int):Int
    private external fun _decode(context: Long,input: ByteArray,len: Int,output: ShortArray,output_max_sample: Int):Int
}