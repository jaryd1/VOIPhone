package com.jaryd.voiphone.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.AudioTrack.MODE_STREAM
import com.jaryd.opuscodec.AudioConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioPlayer (val config: AudioConfig){

    private val player by lazy {AudioTrack(AudioManager.STREAM_VOICE_CALL,config.sample_rete,
        if(config.channels ==1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT,
        AudioTrack.getMinBufferSize(config.sample_rete,
            if(config.channels ==1) AudioFormat.CHANNEL_OUT_MONO
            else AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT),
        MODE_STREAM)  }


    fun init(){
        if(player.state != AudioTrack.STATE_INITIALIZED){
            throw RuntimeException("audiotrack not init")
        }
    }

    fun start(){
        if(player.state != AudioTrack.STATE_INITIALIZED){
            throw RuntimeException("audiotrack not init");
        }
        player.play()
    }

    fun stop(){
        player.stop()
    }





    internal suspend fun play(sample: ShortArray,size: Int) = withContext(Dispatchers.Default){
        player.write(sample,0,size)
    }



}