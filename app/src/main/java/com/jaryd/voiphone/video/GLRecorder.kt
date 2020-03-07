package com.jaryd.voiphone.video

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.util.Log
import android.view.Surface
import com.jaryd.gles.EglCore
import com.jaryd.gles.GLESHelper
import com.jaryd.gles.GLESRender
import com.jaryd.gles.WindowSurface
import com.jaryd.videocodec.CodecConfig
import com.jaryd.videocodec.H264Frame
import com.jaryd.videocodec.VideoEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class GLRecorder {
    private val TAG ="GLRecorder"
    private val egl by lazy { EglCore() }
    private lateinit var mDisplaySurface:WindowSurface
    private lateinit var mEncoderSurface:WindowSurface
    lateinit var mCameraSurfaceTexture:SurfaceTexture
    private var mCameraTexture = 0
    private var mWidth = 0
    private var mHeight = 0
    @Volatile
    private var mDestroyed = false
    @Volatile
    private var mRecording = false

    private val render by lazy { GLESRender() }
    private val videoEncoder by lazy { VideoEncoder() }

    fun initDisplaySurface(surface:Surface,width:Int,heigt:Int,encodeCallBack:(frame:H264Frame) ->Unit){
        Log.e(TAG,surface.isValid.toString())
        mDisplaySurface = WindowSurface(egl,surface,true)
        mDisplaySurface.makeCurrent()
        mCameraTexture = GLESHelper.creatTextureID(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
        mCameraSurfaceTexture = SurfaceTexture(mCameraTexture)
        videoEncoder.initEncoder(CodecConfig.getWidth(),CodecConfig.getHeight(),CodecConfig.getBitRate())
        videoEncoder.addCallBack(encodeCallBack)
        mEncoderSurface = WindowSurface(egl, videoEncoder.inputSurface!!,true)
        render.setDisplaySize(width,heigt)
        mWidth = width
        mHeight = heigt
        egl.makeNothingCurrent()
        mCameraSurfaceTexture.setOnFrameAvailableListener {

              runBlocking {
                  if(!mDestroyed) {
                      rendertoScreen()
                  }
              }
        }
    }

    fun startRecord(){
        mRecording = true
        videoEncoder.startEncode()
    }

    fun destroy(){
        mDestroyed = true
        mDisplaySurface?.releaseEglSurface()
        mEncoderSurface?.releaseEglSurface()
        egl?.release()
        mCameraSurfaceTexture?.release()
        videoEncoder.stopEncode()
    }

    private suspend fun rendertoScreen() = withContext(Dispatchers.Default){
        mDisplaySurface.makeCurrent()
        mCameraSurfaceTexture.updateTexImage()
        val matrix = FloatArray(16)
        mCameraSurfaceTexture.getTransformMatrix(matrix)

        render.setTramsMatrix(matrix)
        render.setDisplaySize(mWidth, mHeight)
        render.drawFrame(mCameraTexture)
        mDisplaySurface.swapBuffers()

        if(mRecording) {
            mEncoderSurface.makeCurrent()
            render.setDisplaySize(CodecConfig.getWidth(), CodecConfig.getHeight())
            render.drawFrame(mCameraTexture)
            mEncoderSurface.setPresentationTime(mCameraSurfaceTexture.timestamp)
            mEncoderSurface.swapBuffers()
        }
    }
}