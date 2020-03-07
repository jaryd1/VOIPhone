//
// Created by Jarryd.
//

#include <android/log.h>
#include "RTPManager.h"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"rtp manager",__VA_ARGS__)

RTPManager::RTPManager() {
    videoSession = new RTPVideoSession(this);
    audioSession = new RTPAudioSession(this);
    callBack = new JNICallBack();
}

RTPManager::~RTPManager() {
    delete videoSession;
    delete audioSession;
    delete  callBack;
}

void RTPManager::listen(int video_port,int audio_port) {
    videoSession->listen(video_port);
    audioSession->listen(audio_port);
}

void RTPManager::initJniCallBack(JNIEnv *env, jobject obj) {
    callBack->init(env,obj);
}

void RTPManager::sendH264(byte *buffer, int size, const char* addr, int port) {
    videoSession->send(buffer,size,addr,port);
}

void RTPManager::sendOGG(byte *buffer, int size, const char *addr, int port) {
    audioSession->send(buffer,size,addr,port);
}

void RTPManager::responseH264(byte *response, int len) {
    callBack->callResponseH264(response, len);
}

void RTPManager::responseOgg(byte *response, int len) {
    callBack->callResponseOgg(response,len);
}

void RTPManager::destroy() {
    videoSession->destroy();
    audioSession->destroy();
    callBack->release();
}
