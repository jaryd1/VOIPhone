//
// Created by Jarryd.
//
#include "com_jaryd_net_RTPManager.h"
#include "RTPManager.h"
#include <android/log.h>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"rtp manager",__VA_ARGS__)

JNIEXPORT jlong JNICALL Java_com_jaryd_net_RTPManager__1init
        (JNIEnv *env, jobject thiz){
    RTPManager* client = new RTPManager();
    return (long)client;
}

JNIEXPORT void JNICALL Java_com_jaryd_net_RTPManager__1sendH264
        (JNIEnv *env, jobject thiz, jlong context, jbyteArray buffer, jint size,
                jstring addr, jint port){
        RTPManager* client = (RTPManager*)context;
        jbyte * data = env->GetByteArrayElements(buffer,JNI_FALSE);
        const char *ip_addr = env->GetStringUTFChars(addr,JNI_FALSE);
    client->sendH264((byte *) data, size, ip_addr, port);
        env->ReleaseStringUTFChars(addr,ip_addr);
        env->ReleaseByteArrayElements(buffer,data,0);

}

JNIEXPORT void JNICALL Java_com_jaryd_net_RTPManager__1sendOgg
        (JNIEnv *env, jobject thiz, jlong context, jbyteArray buffer, jint size,
         jstring addr, jint port){
    RTPManager* client = (RTPManager*)context;
    jbyte * data = env->GetByteArrayElements(buffer,JNI_FALSE);
    const char *ip_addr = env->GetStringUTFChars(addr,JNI_FALSE);
    client->sendOGG((byte *) data, size, ip_addr, port);
    env->ReleaseStringUTFChars(addr,ip_addr);
    env->ReleaseByteArrayElements(buffer,data,0);

}

JNIEXPORT void JNICALL Java_com_jaryd_net_RTPManager__1listen
        (JNIEnv *env, jobject thiz, jlong context,jint port,jint audio_port){
    RTPManager* client = (RTPManager*)context;
    client->initJniCallBack(env,thiz);
    client->listen(port,audio_port);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_jaryd_net_RTPManager__1destroy(JNIEnv *env, jobject thiz, jlong context) {
    RTPManager* client = (RTPManager*)context;
    client->destroy();
    delete client;
}