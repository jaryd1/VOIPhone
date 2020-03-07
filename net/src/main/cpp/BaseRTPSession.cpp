//
// Created by Jarryd.
//

#include <jrtplib3/rtpudpv4transmitter.h>
#include <jrtplib3/rtpsessionparams.h>
#include <android/log.h>
#include "BaseRTPSession.h"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"rtp session",__VA_ARGS__)

BaseRTPSession::BaseRTPSession(RTPResponser *responser) {
    this->responser = responser;
    response_data = NULL;
    response_len = 0;
    hasAddDestination = false;
}

void BaseRTPSession::destroy() {
    if(response_data)
        free(response_data);
    response_len = 0;
    responser = NULL;
    BYEDestroy(jrtplib::RTPTime(0.5),NULL,0);
}

void BaseRTPSession::checkError(char *op, int status) {
    if(status <0){
       LOGE("op:%s,%s,%s",op,getTAG(),jrtplib::RTPGetErrorString(status).c_str());
    }
}

void BaseRTPSession::listen(int port) {
    jrtplib::RTPSessionParams* sessionParams = new jrtplib::RTPSessionParams();
    jrtplib::RTPUDPv4TransmissionParams* transmissionParams = new jrtplib::RTPUDPv4TransmissionParams();

    assert(sessionParams->SetUsePollThread(true) >=0 );
    sessionParams->SetAcceptOwnPackets(true);
    sessionParams->SetOwnTimestampUnit(getTimestampUnits());

    transmissionParams->SetPortbase(port);

    int status = Create(*sessionParams,transmissionParams);
    checkError("creat session",status);

    delete sessionParams;
    delete transmissionParams;
}

void BaseRTPSession::addAddr(const char *addr, int port) {
    uint32_t  ip_addr = inet_addr(addr);
    assert(ip_addr != INADDR_NONE);
    ip_addr = ntohl(ip_addr);

    jrtplib::RTPIPv4Address address(ip_addr,port);
    int status =AddDestination(address);
    checkError("add  addr",status);
}

void BaseRTPSession::send(byte *buffer, int size, const char *addr, int port) {
    if(!hasAddDestination)
        initPayLoadParameters(addr, port);
    sendImp(buffer,size);
}

void BaseRTPSession::OnPollThreadStep() {
    BeginDataAccess();
    if(GotoFirstSourceWithData()){
        do{
            jrtplib::RTPPacket* packet;
            while((packet=GetNextPacket()) !=NULL){
                handlerPacket(packet);
                DeletePacket(packet);
            }
        }while (GotoNextSourceWithData());
    }
    EndDataAccess();
}
