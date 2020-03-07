package com.jaryd.voiphone

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.jaryd.net.NetController
import com.jaryd.net.NetPacket
import com.jaryd.net.RTPControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NetService :Service(){

    private val TAG = "NetService"
    private lateinit var binder: NetBinder
    private var scope :CoroutineScope? = null
    private var becalled = false
    private var needUdp = false

    companion object{
       val CALLING_STATE = "call_state"
        val NEED_UDP ="udp"
    }

    override fun onBind(intent: Intent?): IBinder? {
        scope = MainScope()
        Log.e("life","bind")
        binder = NetBinder()
        becalled = intent!!.getBooleanExtra(CALLING_STATE,false)
        needUdp = intent!!.getBooleanExtra(NEED_UDP,false)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        binder.removeCallBack()
        NetController.close()
        RTPControl.release()
        Log.e("tag","onunbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        scope!!.cancel()
        scope = null
        Log.e("life","ondestroy")
        super.onDestroy()
    }

    fun runListen(){
        NetController.initTcp()
        scope!!.launch {
            while(true){
                try {
                    val packet = NetController.listenTcp()
                    binder.handlePacket(packet)
                }catch (e: Exception){
                    Log.e("tcp",e.message)
                    break
                }
            }
        }
//        if(needUdp) {
//            NetController.initUdp(becalled)
//            scope!!.launch {
//                while (true) {
//                    try {
//                        val packet = NetController.listen()!!
//                        binder.handlePacket(packet!!)
//                    } catch (e: Exception) {
//                        Log.e("udp", e.message)
//                        break
//                    }
//                }
//            }
//        }
        RTPControl.init(){
            binder.handlePacket(it)
        }
        RTPControl.listen()
    }


    internal inner class NetBinder: Binder() {
        private  var callback: ((packet:NetPacket) -> Unit)? = null
        fun registerCallBack(callback: ((packet:NetPacket) -> Unit)) {
            this.callback = callback
        }

        fun runService() {
            this@NetService.runListen()
        }

        fun removeCallBack() {
            callback = null
        }

        fun handlePacket(packet: NetPacket){
            callback?.invoke(packet)
        }
    }
}


