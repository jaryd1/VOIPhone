//
// Created by Jarryd.
//

#ifndef VOIPHONE_RTPAUDIOSESSION_H
#define VOIPHONE_RTPAUDIOSESSION_H

#include "BaseRTPSession.h"
#include "include/jrtplib3/rtppacket.h"

class RTPAudioSession: public BaseRTPSession{
protected:
    virtual double getTimestampUnits();

    virtual char *getTAG();

    virtual void initPayLoadParameters(const char *addr, int port);

    virtual void sendImp(byte *buffer, int size);

    virtual void handlerPacket(jrtplib::RTPPacket *packet);

public:
    RTPAudioSession(RTPResponser *responser);

};

#endif //VOIPHONE_RTPAUDIOSESSION_H
