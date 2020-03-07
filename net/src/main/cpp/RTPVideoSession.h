//
// Created by Jarryd.
//

#ifndef VOIPHONE_RTPVIDEOSESSION_H
#define VOIPHONE_RTPVIDEOSESSION_H

#include "BaseRTPSession.h"

class RTPVideoSession: public BaseRTPSession{

private:
    void addStartCode(byte* rawData,int len);
    void handleFUASlice(jrtplib::RTPPacket* packet);
public:
    RTPVideoSession(RTPResponser *responser);

protected:
    double getTimestampUnits() override;

    char *getTAG() override;

    void initPayLoadParameters(const char *addr, int port) override;

    void sendImp(byte *buffer, int size) override;

    void handlerPacket(jrtplib::RTPPacket *packet) override;

};
#endif //VOIPHONE_RTPVIDEOSESSION_H
