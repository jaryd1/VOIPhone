package com.jaryd.voiphone

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jaryd.voiphone.databinding.CallBinding
import com.jaryd.voiphone.viewmodel.CallingViewModel
import com.jaryd.voiphone.viewmodel.CallingViewModelFactory

class CallingActivity :AppCompatActivity(){


    private lateinit var iservice:NetService.NetBinder
    private lateinit var viewModel: CallingViewModel
    val connect = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iservice = service as NetService.NetBinder
            iservice.registerCallBack {
                    packet -> viewModel.handlePacket(packet)
            }
            iservice.runService()
            viewModel.requestCall()
        }

    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val becalled = intent.getBooleanExtra(BECALLED,false)
        val addr = intent.getStringExtra(ADDR)


        viewModel = ViewModelProviders.of(this,
            CallingViewModelFactory(addr, becalled)).get(CallingViewModel::class.java)
        val binding:CallBinding = DataBindingUtil.setContentView(this,R.layout.call)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        viewModel.finish.observe(this, Observer {
            if(it) {
                finish()
            }
        })



    }

    override fun onResume() {
        super.onResume()
        val intent = Intent()
        intent.setClass(this,NetService::class.java)
        intent.putExtra(NetService.NEED_UDP,true)
        intent.putExtra(NetService.CALLING_STATE,viewModel.beCalled.value)
        bindService(intent,connect, Context.BIND_AUTO_CREATE)

    }

    override fun onPause() {
        super.onPause()
        unbindService(connect)
    }

    companion object{
        val BECALLED = "becalled"
        val ADDR = "addr"
    }
}