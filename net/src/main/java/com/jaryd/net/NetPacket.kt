package com.jaryd.net

import android.os.Parcel
import android.os.Parcelable
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 *     signal codec_info data_len data
 * len  1                  2           /byte
 */
class NetPacket internal constructor(var signal:Int = 0,var buffer:ByteArray?=null, var len:Int =0,
                                    var addr:String? = null, var port: Int = NetConfig.TCP_PORT,var type:Int = NetPacketUtils.AUDIO):Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createByteArray(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {}


    override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.also {
                it.writeInt(signal)
                it.writeByteArray(buffer)
                it.writeInt(len)
                it.writeString(addr)
                it.writeInt(port)
                it.writeInt(type)
            }
    }

    fun readFromParcel(source:Parcel){

        signal = source.readInt()
        buffer = source.createByteArray()
        len = source.readInt()
        addr = source.readString()
        port = source.readInt()
        type = source.readInt()
    }



    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NetPacket> {
        override fun createFromParcel(parcel: Parcel): NetPacket {
            return NetPacket(parcel)
        }

        override fun newArray(size: Int): Array<NetPacket?> {
            return arrayOfNulls(size)
        }
    }

    fun isAudioPacket():Boolean{
        return type == NetPacketUtils.AUDIO
    }

}



fun NetPacket.toBytes():ByteArray?{
    return when(this.signal){

        NetPacketUtils.CALLING -> {
            this.buffer?.let {
                ByteBuffer.allocate(NetPacketUtils.SIGNAL_SIZE + NetPacketUtils.DATA_LEN_SIZE +it.size+NetPacketUtils.TYPE)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(this.type!!)
                    .put(this.signal.toByte())
                    .putShort(this.len.toShort())
                    .put(it)
                    .array()
            }
        }

        else    -> {
                ByteBuffer.allocate(NetPacketUtils.TYPE+ NetPacketUtils.SIGNAL_SIZE)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(this.type)
                    .put(this.signal.toByte())
                    .array()
        }
    }
}