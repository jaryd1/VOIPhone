//
// Created by Jarryd.
//

#ifndef VOIPHONE_BASERTPSESSION_H
#define VOIPHONE_BASERTPSESSION_H

#include <jrtplib3/rtpsession.h>
#include "RTPResponser.h"

typedef unsigned char byte;
#define MAX_H264_PAYLOAD_LEN 1400
#define MAX_OGG_PAYLOAD_LEN 200

#define H264_PAYLOAD_TYPE 96
#define OGG_PAYLOAD_TYPE 99

class BaseRTPSession: public jrtplib::RTPSession{

protected:
    RTPResponser* responser;
    byte* response_data;
    int response_len;
    bool hasAddDestination;

    virtual double getTimestampUnits()=0;
    virtual char* getTAG() =0;
    virtual void initPayLoadParameters(const char* addr, int port) =0;
    virtual void sendImp(byte* buffer, int size)=0;
    virtual void handlerPacket(jrtplib::RTPPacket* packet)=0;
    void checkError(char* op,int status);
    void addAddr(const char* addr, int port);
    void OnPollThreadStep() override;

public:
    BaseRTPSession(RTPResponser* responser);
    void listen(int port);
    void send(byte* buffer, int size,const char* addr, int port);
    void destroy();


};

#endif //VOIPHONE_BASERTPSESSION_H
