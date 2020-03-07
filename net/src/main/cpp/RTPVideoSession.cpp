//
// Created by Jarryd.
//

#include <PlayLoads.h>
#include <H264FrameSlice.h>
#include <android/log.h>
#include "RTPVideoSession.h"
#include <jrtplib3/rtppacket.h>
#include <FUA_indicator.h>
#include <FUA_header.h>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"rtp client",__VA_ARGS__)
#define TYPE_FUA 28

RTPVideoSession::RTPVideoSession(RTPResponser *responser) : BaseRTPSession(responser) {

}

double RTPVideoSession::getTimestampUnits() {
    return 1.0/90000.0;
}

char *RTPVideoSession::getTAG() {
    return "Video";
}

void RTPVideoSession::initPayLoadParameters(const char *addr, int port) {
    SetDefaultMark(false);
    SetDefaultPayloadType(MAX_H264_PAYLOAD_LEN);
    SetDefaultTimestampIncrement(3000);
    SetMaximumPacketSize(MAX_H264_PAYLOAD_LEN+12);
    addAddr(addr,port);
    hasAddDestination = true;
}

void RTPVideoSession::sendImp(byte *buffer, int size) {
    PlayLoads* playLoads = H264FrameSlice::frameSlice(buffer,size,MAX_H264_PAYLOAD_LEN);
    for (int i = 0; i < playLoads->getSize(); ++i) {
        bool marked = (i == playLoads->getSize()-1) && (i !=0);
        uint32_t timestamp = i == 0? 3000:0;
        int status = SendPacket(playLoads->getPlayLoads()[i],
                                                playLoads->getPlayLoadSize()[i], H264_PAYLOAD_TYPE, marked, timestamp);
        checkError("send h264",status);
    }
}

void RTPVideoSession::handlerPacket(jrtplib::RTPPacket *packet) {
    if (packet->GetPayloadData()[0] & 0x1f != TYPE_FUA) {
        addStartCode(packet->GetPayloadData(), packet->GetPayloadLength());
        responser->responseH264(response_data, response_len);
    } else {
        handleFUASlice(packet);
    }
}

void RTPVideoSession::addStartCode(byte *rawData, int len) {
    if(response_data) {
        free(response_data);
        response_len = 0;
    }
    response_data = (byte*)malloc(len+4);
    response_data[0] = 0;
    response_data[1] = 0;
    response_data[2] = 0;
    response_data[3] = 1;
    memcpy(response_data+4,rawData,len* sizeof(byte));
    response_len = len+4;
}

void RTPVideoSession::handleFUASlice(jrtplib::RTPPacket *packet) {
    byte* rawdata = packet->GetPayloadData();
    int len = packet->GetPayloadLength();
    FUA_Indicator* indicator = FUA_Indicator::parse(rawdata[0]);
    FUA_header* header = FUA_header::parse(rawdata[1]);
    if(header->isStart()){
        if(response_data) {
            free(response_data);
            response_len = 0;
        }
        response_data = (byte*)malloc(len-2+5);
        response_data[0] = 0;
        response_data[1] = 0;
        response_data[2] = 0;
        response_data[3] = 1;
        response_data[4] = (byte)(indicator->getForbidAndRefer() | header->getType());
        memcpy(response_data+5,rawdata+2, sizeof(byte)* len-2);
        response_len += len+3;
    }else{
        int new_len = response_len+ len-2;
        byte* np = (byte*)realloc(response_data,new_len);
        if(!np){
            LOGE("realloc faild");
            np = (byte*)malloc(new_len);
            if(!np){
                LOGE("error ,no enough memory");
                //todo throw error;
            }
            memcpy(np,response_data, sizeof(byte)*response_len);
            free(response_data);
            response_data = np;
        }else{
            response_data = np;
        }

        memcpy(response_data+response_len,rawdata+2, sizeof(byte)*(len-2));
        response_len += len-2;

        if(header->isEnd()){
            responser->responseH264(response_data, response_len);
        }
    }
}
