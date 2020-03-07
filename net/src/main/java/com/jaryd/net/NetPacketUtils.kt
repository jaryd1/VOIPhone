package com.jaryd.net

import java.nio.ByteBuffer
import java.nio.ByteOrder

class NetPacketUtils {

    companion object{
         val REQUEST = 4
         val ACCEPT = 5
         val REFUSE = 6
         val END = 7
         val CALLING = 8
         internal val SIGNAL_SIZE = 1
         internal val DATA_LEN_SIZE =2
         internal var TYPE = 4

        internal val AUDIO = 0
        internal val VIDEO = 1

        fun getCallRequestPacket(IPaddr: String):NetPacket{
            return NetPacket(REQUEST,addr = IPaddr)
        }

        fun getCallAcceptPacket(IPaddr: String):NetPacket{
            return NetPacket(ACCEPT,addr = IPaddr)
        }

        fun getCallRefusePacket(IPaddr: String):NetPacket{
            return NetPacket(REFUSE,addr = IPaddr)
        }

        fun getCallEndPacket(IPaddr: String):NetPacket{
            return NetPacket(END,addr = IPaddr)
        }

        fun getAudioCallingPacket(buffer: ByteArray, len: Int, IPaddr: String, becall:Boolean):NetPacket{
            return NetPacket(CALLING,buffer = buffer, len = len, addr = IPaddr,type = NetPacketUtils.AUDIO,
                    port = if(becall == false) NetConfig.UDP_PORT else NetConfig.UDP_BECALL_PORT)
        }

        fun getVideoCallingPacket(buffer: ByteArray, len: Int, IPaddr: String, becall:Boolean):NetPacket{
            return NetPacket(CALLING,buffer = buffer, len = len, addr = IPaddr,type = NetPacketUtils.VIDEO,
                port = if(becall == false) NetConfig.UDP_PORT else NetConfig.UDP_BECALL_PORT)
        }



        fun parsePacket(input:ByteArray,offset:Int,len: Int,addr:String?):NetPacket{
            val type = ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(input,offset,offset+4)
                .getInt(0)
                val newoffset = offset +4
            return  when(input[newoffset]){
                CALLING.toByte() -> {
                    NetPacket(input[newoffset].toInt(),
                        buffer = input.copyOfRange(newoffset+3,len),
                        len = ByteBuffer.allocate(2)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .put(input.copyOfRange(newoffset+1,newoffset+3))
                            .getShort(0)
                            .toInt(),addr = addr,type = type)
                }
                else             -> {
                    NetPacket(input[newoffset].toInt(),addr = addr,type = type)
                }
            }
        }
    }
}