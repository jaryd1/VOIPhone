//
// Created by Jarryd.
//

#ifndef VOIPHONE_JNICALLBACK_H
#define VOIPHONE_JNICALLBACK_H

#include <jni.h>

class JNICallBack{
private:
    JavaVM* jvm;
    jobject obj;
public:
    JNICallBack();
    ~JNICallBack();
    void init(JNIEnv* env,jobject obj);
    void callResponseH264(unsigned char* reponse, int len);
    void callResponseOgg(unsigned char* reponse, int len);
    void release();
};

#endif //VOIPHONE_JNICALLBACK_H
