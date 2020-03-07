//
// Created by Jarryd.
//

#ifndef VOIPHONE_RTPRESPONSER_H
#define VOIPHONE_RTPRESPONSER_H

typedef unsigned char byte;

class RTPResponser{
public:
    virtual void responseH264(byte* response, int len) = 0;
    virtual void responseOgg(byte* response, int len) = 0;
};
#endif //VOIPHONE_RTPRESPONSER_H
