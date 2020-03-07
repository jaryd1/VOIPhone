//
// Created by Jarryd.
//

#include "RTPAudioSession.h"

RTPAudioSession::RTPAudioSession(RTPResponser *responser)
    : BaseRTPSession(responser) {
}

double RTPAudioSession::getTimestampUnits() {
    return 1.0/48000.0;
}

char *RTPAudioSession::getTAG() {
    return "Audio";
}

void RTPAudioSession::initPayLoadParameters(const char *addr, int port) {
    SetDefaultMark(true);
    SetDefaultPayloadType(OGG_PAYLOAD_TYPE);
    SetDefaultTimestampIncrement(960);
    SetMaximumPacketSize(MAX_OGG_PAYLOAD_LEN+12);
    addAddr(addr,port);
    hasAddDestination = true;
}

void RTPAudioSession::sendImp(byte *buffer, int size) {
    SendPacket(buffer,size);
}

void RTPAudioSession::handlerPacket(jrtplib::RTPPacket *packet) {
    responser->responseOgg(packet->GetPayloadData(),packet->GetPayloadLength());
}
