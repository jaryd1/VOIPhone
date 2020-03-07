package com.jaryd.voiphone.viewmodel

import android.graphics.SurfaceTexture
import android.media.MediaCodec
import android.os.CountDownTimer
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.lifecycle.*
import com.jaryd.net.NetController
import com.jaryd.net.NetPacket
import com.jaryd.net.NetPacketUtils
import com.jaryd.net.RTPControl
import com.jaryd.opuscodec.AudioConfig
import com.jaryd.opuscodec.OpusUtils
import com.jaryd.videocodec.CodecConfig
import com.jaryd.videocodec.VideoDecoder
import com.jaryd.videocodec.toBytes
import com.jaryd.voiphone.audio.AudioPlayer
import com.jaryd.voiphone.audio.AudioProcessor
import com.jaryd.voiphone.audio.AudioRecorder
import com.jaryd.voiphone.video.CameraImp
import com.jaryd.voiphone.video.GLRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CallingViewModel(private val addr: String,private val _becalled:Boolean) :ViewModel(){
    private val TAG ="CallingViewModel"
    val Calling = MutableLiveData(false)
    val beCalled = MutableLiveData(false)
    val finish = MutableLiveData(false)
    val delayTime = MutableLiveData("")
    private val _callingTime =MutableLiveData(-1L)


    private val recorder by lazy {
        AudioRecorder(
            AudioConfig.getDefaultConfig()
        )
    }
    private val player   by lazy {
        AudioPlayer(
            AudioConfig.getDefaultConfig()
        )
    }

    private val audioCodec by lazy { OpusUtils() }

    private val audioProcessor by lazy { AudioProcessor() }

    private lateinit var camera: CameraImp
    private var recoder:GLRecorder? = null
    private var videoDecoder:VideoDecoder? = null

    @Volatile
    private var recording = false
    @Volatile
    var canRecord = true


    private lateinit var encodeBuffer:ByteArray
    private lateinit var decodeBuffer:ShortArray

    val CallingTime: LiveData<String> = Transformations.map(_callingTime){
        val hour = (it / 3600).toInt()
        val minu = (it % 3600 /60 ).toInt()
        val sec = (it % 60).toInt()
        val lamda = { value:Int ->
            if (value <=0) "00"
                else{
                if(value < 10) "0${value}"
                    else "${value}"
            }
        }
        if(hour == 0)
            lamda.invoke(minu)+":"+lamda.invoke(sec)
        else
            lamda.invoke(hour)+":"+lamda.invoke(minu)+":"+lamda.invoke(sec)
    }


    private val countDownTimer = object :CountDownTimer(3600*60*1000,1000){
        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            _callingTime.value = _callingTime.value?.plus(1)
        }

    }


    val SurfaceTextureSizeChanged:(surface: SurfaceTexture?, width:Int, height:Int) -> Unit = {
        surface, width, height -> Unit
    }
    val SurfaceTextureUpdated:(surface:SurfaceTexture?) -> Unit={
        surface -> Unit
    }
    val SurfaceTextureDestroyed:(surface:SurfaceTexture?) -> Boolean ={
        surface -> true
    }
    val SurfaceTextureAvailable:(textureView:TextureView,surface: SurfaceTexture?, width:Int, height:Int) -> Unit = {
            textureView,surface, width, height -> kotlin.run {
        recoder = GLRecorder()
            camera = CameraImp(textureView.context)
        recoder!!.initDisplaySurface(Surface(surface),width,height){
                frame -> kotlin.run {
                    runBlocking {
                        if(Calling.value == true && finish.value == false) {
                            val data = frame.toBytes()
                                RTPControl.send(
                                    NetPacketUtils.getVideoCallingPacket(
                                        data,
                                        data.size,
                                        addr,
                                        _becalled
                                    )
                                )
                        }
                    }
                }
            }
            camera.addSurfaceTexture(recoder!!.mCameraSurfaceTexture)
            camera.mExpectSize = Size(CodecConfig.getWidth(),CodecConfig.getHeight())
            camera.StartPreview()
        }
    }

    val DisplayTextureAvailable:(textureView:TextureView,surface: SurfaceTexture?, width:Int, height:Int) -> Unit = {
        textureView, surface, width, height -> kotlin.run {
            videoDecoder = VideoDecoder()
            videoDecoder!!.initDecoder(CodecConfig.getWidth(),CodecConfig.getHeight(), Surface(surface))
        }
    }



    init {
        beCalled.value = _becalled
    }

    private fun init(){
        recorder.init()
        player.init()
        audioCodec.init(AudioConfig.getDefaultConfig())
        audioProcessor.init(AudioConfig.getDefaultConfig(),recorder.sample_size.toInt())
        encodeBuffer = ByteArray(recorder.sample_size.toInt()*2)
        decodeBuffer = ShortArray(recorder.sample_size.toInt())
        Log.e("init","initinng")

    }


    private fun start(){
        recording = true
        recorder.start()
        player.start()
        recoder?.startRecord()
        viewModelScope.launch {
            while(recording) {
                var len = recorder.rectriveAudio()
                if (len == recorder.sample_size.toInt()) {
                    val hasVoice = audioProcessor.process(recorder.samples,len)
                    if(hasVoice ==0){
                        continue
                    }
                    len = encode(recorder.samples, len, encodeBuffer, encodeBuffer.size)
                    if(canRecord && finish.value == false) {
                        RTPControl.send(
                            NetPacketUtils.getAudioCallingPacket(
                                encodeBuffer.copyOfRange(0,len),
                                len,
                                addr,
                                _becalled
                            )
                        )
                    }
                }
            }
        }

        Log.e("tag","startiiing")
    }



    private fun stop(){
        if(recording){
            recording = false
            recorder.stop()
            player.stop()
        }
        camera.StopPreview()
        recoder!!.destroy()
        recoder = null
        videoDecoder?.stopDecode()

    }


    fun requestCall(){
        if(beCalled.value!! == false){
            runBlocking { NetController.send(NetPacketUtils
                .getCallRequestPacket(addr)) }
        }
    }

    fun refuseCall(){
        runBlocking {
            NetController.send(NetPacketUtils.getCallEndPacket(addr))
        }
//        stopCalling()
    }

    fun endCall(){
        runBlocking {
            NetController.send(NetPacketUtils.getCallEndPacket(addr))
        }
//        stopCalling()
    }

    fun acceptCall(){
        runBlocking {
            NetController.send(NetPacketUtils.getCallAcceptPacket(addr))
        }
//        init()
//        startCalling()
    }


    private fun startCalling(){
        start()
        Calling.value = true
        countDownTimer.start()
    }

    private fun stopCalling(){
        stop()
        finish.value = true
        countDownTimer.cancel()
    }

    private suspend fun encode(input:ShortArray,sample_size:Int,output:ByteArray,byte_size:Int):Int
            = withContext(Dispatchers.Default){
        audioCodec.encode(input,sample_size,output,byte_size)

    }

    private suspend fun decode(input: ByteArray,byte_size:Int,output: ShortArray,sample_size: Int):Int
            = withContext(Dispatchers.Default){
        audioCodec.decode(input,byte_size,output,sample_size)
    }


    fun handlePacket(packet: NetPacket){
        when(packet.signal){
            NetPacketUtils.REFUSE,NetPacketUtils.END -> {
                stopCalling()
            }
            NetPacketUtils.ACCEPT -> {
                init()
                startCalling()
            }
            NetPacketUtils.CALLING -> {
                runBlocking {
                    if(packet.isAudioPacket()) {
                        val sample_size = decode(
                            packet.buffer!!,
                            packet.len,
                            decodeBuffer,
                            recorder.sample_size.toInt()
                        )
                        player.play(decodeBuffer, sample_size)
                    }else{
                        val frame = videoDecoder!!.parse(packet.buffer!!)
                        videoDecoder!!.putFrame(frame)
                    }

                }
            }
        }
    }
}

class CallingViewModelFactory(private val addr:String,private val becalled: Boolean) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(ViewModel::class.java.isAssignableFrom(modelClass)){
            return CallingViewModel(addr,becalled) as T
        }
        return super.create(modelClass)
    }
}