package com.jaryd.voiphone

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import androidx.databinding.BindingAdapter

object BindingAdapter {

    @BindingAdapter(value = ["android:onSurfaceTextureSizeChanged",
        "android:onSurfaceTextureUpdated","android:onSurfaceTextureDestroyed",
        "android:onSurfaceTextureAvailable"],requireAll = false)
    @JvmStatic
    fun SetSurfaceTextureListen(textureView: TextureView,
                SizeChanged:((surface:SurfaceTexture?,width:Int,height:Int) -> Unit)? = null,
                Updated:((surface:SurfaceTexture?) -> Unit)? = null,
                Destroyed:((surface:SurfaceTexture?) -> Boolean)? = null,
                Available:(texture:TextureView,surface:SurfaceTexture?,width:Int,height:Int) -> Unit){

        textureView.surfaceTextureListener = object :TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                SizeChanged?.invoke(surface, width, height)
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                Updated?.invoke(surface)
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return if(Destroyed == null) true else Destroyed!!.invoke(surface)
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                Available.invoke(textureView,surface, width, height)
            }

        }

    }
}