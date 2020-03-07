//
// Created by Jarryd.
//

#include <cstring>
#include <android/log.h>
#include "H264FrameSlice.h"
#include "NALUHeader.h"
#include "FUA_indicator.h"
#include "FUA_header.h"

#define FUA_TYPE 28

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"rtp h264",__VA_ARGS__)

void H264FrameSlice::removeStartCode(byte **nals, int *len) {
    byte *nal = *nals;
    if(nal[0] ==0 && nal[1] == 0 && nal[2] == 1){
        (*nals) +=3;
        (*len) -= 3;
    }else if(nal[0] ==0 && nal[1] ==0 && nal[2] == 0 && nal[3] == 1){
        (*nals) +=4;
        (*len) -= 4;
    }
}
PlayLoads* H264FrameSlice::frameSlice(byte *nal, int len,
                               int max_size) {
    removeStartCode(&nal,&len);
    if(len <= max_size){
        return new PlayLoads(nal,len);
    }

    NALUHeader* header = NALUHeader::paserHeader(nal[0]);
    //remove nal header
    nal +=1;
    len -= 1;

    int packets = (len /(max_size+1))+1;
    //add fua_indicator ,fua_header 2 byte
    if(packets*2 + len> max_size*packets)
        packets +=1;
    //last packet size
    int last_packet_size = len - (max_size - 2) * (packets - 1);
    if(last_packet_size >= max_size){
        LOGE("last packet_size %d,len %d,packet_size %d",last_packet_size,len,packets);
    }


    byte ** playloads = new byte*[packets];
    int *playloads_size = new int[packets];
    for (int i = 0; i < packets; ++i) {
        FUA_Indicator* indicator = new  FUA_Indicator(header->getForbidden()
                ,header->getReference(),FUA_TYPE);
        FUA_header* fuaHeader = new FUA_header(0,0,header->getType());
        if(i == 0){
            fuaHeader->setStart(1);
            playloads[i] = new byte[max_size];
            playloads_size[i] = max_size;
            playloads[i][0] = indicator->toByte();
            playloads[i][1] = fuaHeader->toByte();
            memcpy(playloads[i]+2,nal, sizeof(byte)*(max_size-2));
        }else if (i == packets-1){
            playloads[i] = new byte[last_packet_size +2];
            playloads_size[i] = last_packet_size+2;
            fuaHeader->setEnd(1);
            playloads[i][0] = indicator->toByte();
            playloads[i][1] = fuaHeader->toByte();
            memcpy(playloads[i]+2,nal+i*(max_size-2),last_packet_size* sizeof(byte));
        }else{
            playloads[i] = new byte[max_size];
            playloads_size[i] = max_size;
            playloads[i][0] = indicator->toByte();
            playloads[i][1] = fuaHeader->toByte();
            memcpy(playloads[i]+2,nal+i*(max_size-2), sizeof(byte)*(max_size-2));
        }
    }
    return new PlayLoads(playloads,playloads_size,packets);
}

void H264FrameSlice::test(int *len) {
    *len = 12;
}

