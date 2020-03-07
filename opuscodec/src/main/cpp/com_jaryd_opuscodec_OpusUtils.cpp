//
// Created by Jarryd.
//
#include "com_jaryd_opuscodec_OpusUtils.h"
#include "OpusUtils.h"


JNIEXPORT jlong JNICALL Java_com_jaryd_opuscodec_OpusUtils__1init
        (JNIEnv *env, jobject obj, jint sample_rate, jint channels, jint bitrate){
    OpusUtils* opusUtils = new OpusUtils();
    opusUtils->init(sample_rate,channels,bitrate);
    return (jlong)opusUtils;
}


JNIEXPORT jint JNICALL Java_com_jaryd_opuscodec_OpusUtils__1encode
        (JNIEnv *env, jobject obj, jlong context, jshortArray input, jint sample_size, jbyteArray output, jint output_size){
    OpusUtils* opusUtils = (OpusUtils*)context;
    jshort * samples = env->GetShortArrayElements(input,JNI_FALSE);
    jbyte * buffer = env->GetByteArrayElements(output,JNI_FALSE);
    int len = opusUtils->encode(samples,sample_size,(byte*)buffer,output_size);
    env->ReleaseShortArrayElements(input,samples,0);
    env->ReleaseByteArrayElements(output,buffer,0);
    return len;
}

JNIEXPORT jint JNICALL Java_com_jaryd_opuscodec_OpusUtils__1decode
        (JNIEnv *env, jobject obj, jlong context, jbyteArray input, jint len, jshortArray output, jint output_sample){
    OpusUtils* opusUtils = (OpusUtils*)context;
    jbyte * data = env->GetByteArrayElements(input,JNI_FALSE);
    jshort *buffer = env->GetShortArrayElements(output,JNI_FALSE);
    int samples_size = opusUtils->decode((byte*)data,len,buffer,output_sample);
    env->ReleaseByteArrayElements(input,data,0);
    env->ReleaseShortArrayElements(output,buffer,0);
    return samples_size;
}

