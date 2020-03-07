package com.jaryd.gles

import android.util.SparseArray
import androidx.core.util.forEach
import com.jaryd.gles.utils.CoordinationUtils
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class GLESRender {

    private lateinit var mVertexs:FloatBuffer
    private lateinit var mTextures:FloatBuffer
    private val canvas = SparseArray<GLImageCanvas>()
    private val CAMERA = 0
    private val IMAGE = 1
    init {
        mVertexs =GLESHelper.creatFloatBuffer(CoordinationUtils.mVertexsCoor)
        mTextures = GLESHelper.creatFloatBuffer(CoordinationUtils.mTexturesCoor)
        canvas.append(CAMERA,GLImageOESCanvas())
    }

    fun setDisplaySize(width:Int,height:Int){
        canvas.forEach { key, value ->
            value.onDisplaySizeChanged(width, height)
        }
    }

    fun setImageSize(width: Int,height: Int){
        canvas[CAMERA].onInputSizeChanged(width, height)
    }

    fun setTramsMatrix(matrix:FloatArray){
        (canvas[CAMERA] as GLImageOESCanvas).setTextureTransformMatrix(matrix)
    }

    fun drawFrame(textureId:Int){
            canvas[CAMERA].drawFrame(textureId, mVertexs, mTextures)
//        var id = canvas[CAMERA].drawFrameBuffer(textureId, mVertexs, mTextures)
//        canvas[IMAGE].drawFrame(id, mVertexs, mTextures)
    }

    fun drawFrame(data:ByteBuffer,width: Int,height: Int){
    }
}