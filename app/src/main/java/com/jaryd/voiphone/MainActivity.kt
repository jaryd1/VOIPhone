package com.jaryd.voiphone

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jaryd.voiphone.databinding.ActivityMainBinding
import com.jaryd.voiphone.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    val viewmodel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }
    private lateinit var iservice:NetService.NetBinder
    val connection = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iservice = service as NetService.NetBinder
            iservice.registerCallBack {
                packet -> viewmodel.handlePacket(packet)
            }
            iservice.runService()
        }



    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val databind:ActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        databind.viewmodel = viewmodel
        viewmodel.toCallingActivity.observe(this, Observer {
            if(it){
                val intent = Intent()
                intent.setClass(this,CallingActivity::class.java)
                intent.putExtra(CallingActivity.BECALLED,viewmodel.beCalled)
                intent.putExtra(CallingActivity.ADDR,viewmodel.addr)
                startActivity(intent)
            }
        })



    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA),100)
            }
        }

        val intent = Intent()
        intent.setClass(this,NetService::class.java)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unbindService(connection)
    }
}
