package com.jaryd.gles

import android.opengl.GLES30
import android.text.TextUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.*

open class GLImageCanvas(
                         protected  var mVertexShader:String?=VERTEX_SHADE,
                         protected var mFragmentShader:String?=FRAGMENT_SHADE){
    companion object {

        internal val VERTEX_SHADE = "" +
                "attribute vec4 aPosition; \n" +
                "attribute vec4 aTextureCoord; \n" +
                "varying vec2 textureCoordinate;\n" +
                "void main(){ \n" +
                "   gl_Position = aPosition;\n" +
                "   textureCoordinate = aTextureCoord.xy;\n" +
                "}"
        internal val FRAGMENT_SHADE = "" +
                "precision mediump float;\n" +
                "varying vec2 textureCoordinate;\n" +
                "uniform sampler2D inputTexture;\n" +
                "void main(){\n" +
                "   gl_FragColor = texture2D(inputTexture,textureCoordinate);\n" +
                "}"
    }

    protected val mCoordPerVertex = 2
    protected val mVertexCount = 4

    protected var mProgramHandler = 0
    protected var mPositionHandler = 0
    protected var mTextureCoordinateHandler = 35
    protected var mInputTextureHandler = 0

    protected var mImageWidth = 0
    protected var mImageHeight = 0

    protected var mDisplayWidth = 0
    protected var mDisplayHeight = 0

    protected var mFrameWidth = -1
    protected var mFrameHeight = -1

    protected var mFrameBuffers: IntArray? = null
    protected var mFrameBufferTextures: IntArray? = null


    protected var mTextureType = GLES30.GL_TEXTURE_2D

    protected var isInitialized  = false

    protected var mTextureId = GLESHelper.GL_NOT_INIT

    protected val mParameterTasks: LinkedList<Runnable> by lazy { LinkedList<Runnable>() }

    init {
        initHanders()
    }

    open  fun initHanders() {
        if((!TextUtils.isEmpty(mVertexShader)) and !(TextUtils.isEmpty(mFragmentShader))) {
            mProgramHandler = GLESHelper.creatProgram(mVertexShader!!, mFragmentShader!!)

            mPositionHandler = GLES30.glGetAttribLocation(mProgramHandler, "aPosition")
            mTextureCoordinateHandler = GLES30.glGetAttribLocation(mProgramHandler, "aTextureCoord")
            mInputTextureHandler = GLES30.glGetUniformLocation(mProgramHandler, "inputTexture")
            isInitialized = true
            Log.e("tag","texture handler ${mTextureCoordinateHandler}")
            Log.e("tag","position handler ${mPositionHandler}")
            Log.e("tag","inputexture handler ${mInputTextureHandler}")
        }else {
            mProgramHandler = GLESHelper.GL_NOT_INIT //no init
            mTextureCoordinateHandler = GLESHelper.GL_NOT_INIT
            mInputTextureHandler = GLESHelper.GL_NOT_INIT
            isInitialized = false
        }
    }

    open fun onInputSizeChanged(width:Int,height:Int){
        mImageWidth = width
        mImageHeight = height
    }

    open fun onDisplaySizeChanged(width: Int,height: Int){
        mDisplayWidth = width
        mDisplayHeight = height
    }

    open fun initFrameBuffer(width: Int,height: Int){
        if(!isInitialized) return
        if(mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height)) {
            destroyFrameBuffer()
        }
        if(mFrameBuffers == null) {
            mFrameWidth = width
            mFrameHeight = height
            mFrameBuffers = IntArray(1)
            mFrameBufferTextures = IntArray(1)
            GLESHelper.createFrameBuffer(mFrameBuffers!!, mFrameBufferTextures!!, mFrameWidth, mFrameHeight)
        }
    }

    open fun destroyFrameBuffer(){
        mFrameBufferTextures?.let {
            GLES30.glDeleteFramebuffers(it.size,it,0)
            mFrameBufferTextures = null
        }
        mFrameBuffers?.let {
            GLES30.glDeleteFramebuffers(it.size,it,0)
            mFrameBuffers = null
        }
        mFrameHeight = -1
        mFrameWidth = -1
    }

    open fun release(){
        destroyFrameBuffer()
        if(isInitialized){
            GLES30.glDeleteProgram(mProgramHandler)
            mProgramHandler = GLESHelper.GL_NOT_INIT
        }
    }

    open fun drawFrame(textureId:Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer){
        if((textureId == GLESHelper.GL_NOT_INIT) || !isInitialized) return

        GLES30.glViewport(0,0,mDisplayWidth,mDisplayHeight)
        GLES30.glClearColor(0f,0f,1f,1f)
        GLES30.glClear((GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT))
        GLES30.glUseProgram(mProgramHandler)
        runTasks()
        onDrawTexture(textureId,vertexBuffer,textureBuffer)

    }

    open fun drawFrame(data:ByteBuffer,width: Int,height: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer){
        if( !isInitialized) return
        GLES30.glViewport(0,0,mDisplayWidth,mDisplayHeight)
        GLES30.glClearColor(0f,1f,0f,1f)
        GLES30.glClear((GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT))
        GLES30.glUseProgram(mProgramHandler)
        runTasks()
        onDrawTexture(data,width,height,vertexBuffer,textureBuffer)
    }

    open fun drawFrameBuffer(textureId: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer):Int{
        if((mFrameBufferTextures == null) or (mFrameBuffers == null) or (textureId ==GLESHelper.GL_NOT_INIT)
            or !isInitialized){
            return textureId
        }
        GLES30.glViewport(0,0,mFrameWidth,mFrameHeight)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mFrameBuffers!![0])
        GLES30.glUseProgram(mProgramHandler)
        runTasks()
        onDrawTexture(textureId,vertexBuffer,textureBuffer)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0)
        return mFrameBufferTextures!![0]
    }

    open fun drawFrameBuffer(data: ByteBuffer,width: Int,height: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer):Int{
        if((mFrameBufferTextures == null) or (mFrameBuffers == null)
            or !isInitialized){
            return GLESHelper.GL_NOT_INIT
        }
        GLES30.glViewport(0,0,mFrameWidth,mFrameHeight)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mFrameBuffers!![0])
        GLES30.glUseProgram(mProgramHandler)
        runTasks()
        onDrawTexture(data,width,height,vertexBuffer,textureBuffer)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0)
        return mFrameBufferTextures!![0]
    }

    private fun onDrawTexture(textureId: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLESHelper.checkGlError("activtive texture")
        GLES30.glBindTexture(mTextureType,textureId)
        GLESHelper.checkGlError("bind texture")
        GLES30.glUniform1i(mInputTextureHandler,0)
        GLESHelper.checkGlError("bind texture handle ${mInputTextureHandler}")


        vertexBuffer.position(0)
        GLES30.glEnableVertexAttribArray(mPositionHandler)
        GLES30.glVertexAttribPointer(mPositionHandler,mCoordPerVertex, GLES30.GL_FLOAT,
            false,0,vertexBuffer)
        GLESHelper.checkGlError("vertex attr")

        textureBuffer.position(0)
        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandler)
        GLESHelper.checkGlError("texture able ${mTextureCoordinateHandler}")
        GLES30.glVertexAttribPointer(mTextureCoordinateHandler,mCoordPerVertex, GLES30.GL_FLOAT,
            false,0,textureBuffer)
        GLESHelper.checkGlError("texture attr")


        Drawbefore()
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,mVertexCount)
        GLESHelper.checkGlError("draw")


        GLES30.glDisableVertexAttribArray(mPositionHandler)
//        GLES30.glDisableVertexAttribArray(mTextureCoordinateHandler)
        GLES30.glBindTexture(mTextureType,0)
        GLES30.glUseProgram(0)
        GLESHelper.checkGlError("unbind")
    }

    private fun onDrawTexture(data: ByteBuffer, width: Int,height: Int,vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        vertexBuffer.position(0)
        GLES30.glEnableVertexAttribArray(mPositionHandler)
        GLES30.glVertexAttribPointer(mPositionHandler,mCoordPerVertex, GLES30.GL_FLOAT,
            false,0,vertexBuffer)

        textureBuffer.position(0)
        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandler)
        GLES30.glVertexAttribPointer(mTextureCoordinateHandler,mCoordPerVertex, GLES30.GL_FLOAT,
            false,0,textureBuffer)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)

        if(mTextureId == GLESHelper.GL_NOT_INIT){
            mTextureId = GLESHelper.creatTextureID(mTextureType)
            GLES30.glBindTexture(mTextureType,mTextureId)
            GLES30.glTexImage2D(mTextureType,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA,
                GLES30.GL_UNSIGNED_BYTE,data)
        }else{
            GLES30.glBindTexture(mTextureType,mTextureId)
            GLES30.glTexSubImage2D(mTextureType,0,0,0,width,height,GLES30.GL_RGBA,
                GLES30.GL_UNSIGNED_BYTE,data)
        }

        GLES30.glUniform1f(mInputTextureHandler,0f)

        Drawbefore()
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,mVertexCount)


        GLES30.glDisableVertexAttribArray(mPositionHandler)
        GLES30.glDisableVertexAttribArray(mTextureCoordinateHandler)
        GLES30.glBindTexture(mTextureType,0)
        GLES30.glUseProgram(0)
    }

    protected fun addTask(task:Runnable){
        synchronized(mParameterTasks){
            mParameterTasks.addLast(task)
        }
    }

    protected fun runTasks(){
        while(!mParameterTasks.isEmpty()){
            mParameterTasks.removeFirst().run()
        }
    }

    protected fun setFloatValue(location:Int,value:Float){
        addTask(Runnable {
            GLES30.glUniform1f(location,value)
        })
    }

    protected fun setFloatVec2(location: Int,vec2:FloatArray){
        addTask(Runnable {
            GLES30.glUniform2fv(location,1,vec2,0)
        })
    }

    protected fun setFloatVec3(location: Int,vec3:FloatArray){
        addTask(Runnable {
            GLES30.glUniform3fv(location,1,vec3,0)
        })
    }

    open protected fun Drawbefore() {

    }
}