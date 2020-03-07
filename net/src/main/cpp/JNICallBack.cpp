//
// Created by Jarryd.
//

#include <assert.h>
#include "JNICallBack.h"

JNICallBack::JNICallBack() {
    jvm = NULL;

}

JNICallBack::~JNICallBack() {
}

void JNICallBack::release() {
    JNIEnv* env;
    //main thread don't detach
    assert(jvm->AttachCurrentThread(&env,NULL) == JNI_OK);
    assert(env);
    env->DeleteGlobalRef(obj);
//    jvm->DetachCurrentThread();
    env = NULL;
    jvm = NULL;

}

void JNICallBack::init(JNIEnv *env, jobject obj) {
    env->GetJavaVM(&jvm);
    this->obj = env->NewGlobalRef(obj);

}

void JNICallBack::callResponseH264(unsigned char *response, int len) {
    JNIEnv* env;
    assert(jvm->AttachCurrentThread(&env,NULL) == JNI_OK);
    assert(env);
    jclass  cla = env->GetObjectClass(obj);
    if(cla){
        jmethodID  resp = env->GetMethodID(cla,"getResponseH264","([B)V");
        if(resp){
            jbyteArray array = env->NewByteArray(len);
            env->SetByteArrayRegion(array,0,len,(jbyte*)response);
            env->CallVoidMethod(obj,resp,array);
            env->DeleteLocalRef(array);
        }
        env->DeleteLocalRef(cla);
    }
    jvm->DetachCurrentThread();
}

void JNICallBack::callResponseOgg(unsigned char *response, int len) {
    JNIEnv* env;
    assert(jvm->AttachCurrentThread(&env,NULL) == JNI_OK);
    assert(env);
    jclass  cla = env->GetObjectClass(obj);
    if(cla){
        jmethodID  resp = env->GetMethodID(cla,"getResponseOgg","([B)V");
        if(resp){
            jbyteArray array = env->NewByteArray(len);
            env->SetByteArrayRegion(array,0,len,(jbyte*)response);
            env->CallVoidMethod(obj,resp,array);
            env->DeleteLocalRef(array);
        }
        env->DeleteLocalRef(cla);
    }
    jvm->DetachCurrentThread();
}
