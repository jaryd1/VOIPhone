//
// Created by Jarryd.
//

#ifndef VOIPHONE_RTPMANAGER_H
#define VOIPHONE_RTPMANAGER_H

#include "JNICallBack.h"
#include "RTPVideoSession.h"
#include "RTPAudioSession.h"


class RTPManager:public RTPResponser{
private:
    RTPVideoSession* videoSession;
    RTPAudioSession* audioSession;
    JNICallBack* callBack;
public:
    RTPManager();
    ~RTPManager();
    void listen(int video_port,int audio_port);
    void initJniCallBack(JNIEnv* env,jobject obj);
    void sendH264(byte* buffer, int size, const char* addr, int port);
    void sendOGG(byte* buffer, int size, const char* addr, int port);
    void responseH264(byte *response, int len) override;
    void destroy();

    void responseOgg(byte *response, int len) override;
};
#endif //VOIPHONE_RTPMANAGER_H
