package com.jaryd.voiphone.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.jaryd.opuscodec.AudioConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRecorder(private val config: AudioConfig){

    private val record by lazy{ AudioRecord(MediaRecorder.AudioSource.MIC,
                config.sample_rete,if(config.channels == 1) AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO,
        AudioFormat.ENCODING_PCM_16BIT,
        AudioRecord.getMinBufferSize(config.sample_rete,
            if(config.channels == 1) AudioFormat.CHANNEL_IN_MONO
            else AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT))}
    private val FRAME_DURATION = 0.02 //20ms

    internal val sample_size by lazy { config.sample_rete* config.channels* FRAME_DURATION }

    internal val samples by lazy{ShortArray(sample_size.toInt())}


    fun init(){
        if(record.state != AudioRecord.STATE_INITIALIZED){
            throw RuntimeException("faild init audio_recorder")
        }
    }



    fun start(){
        if(record.state != AudioRecord.STATE_INITIALIZED){
            throw RuntimeException("faild init audio_recorder")
        }
        record.startRecording()

    }



    fun stop(){
        record.stop()
    }

    internal suspend fun rectriveAudio():Int= withContext(Dispatchers.Default){
        record.read(samples,0,sample_size.toInt())
    }



}