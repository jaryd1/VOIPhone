//
// Created by Jarryd.
//
#include "com_jaryd_voiphone_audio_AudioProcessor.h"
#include "AudioProcessor.h"

JNIEXPORT jlong JNICALL Java_com_jaryd_voiphone_audio_AudioProcessor__1init
        (JNIEnv *env, jobject obj, jint sample_rate, jint channels, jint sample_size){
    AudioProcessor* processor = new AudioProcessor();
    processor->init(sample_rate,sample_size);
    return (jlong)processor;
}



JNIEXPORT jint JNICALL Java_com_jaryd_voiphone_audio_AudioProcessor__1process
        (JNIEnv *env, jobject obj, jlong context, jshortArray input, jint sample_size){
    AudioProcessor* processor = (AudioProcessor*)context;
    jshort * samples = env->GetShortArrayElements(input,JNI_FALSE);
    int result = processor->process(samples,sample_size);
    env->ReleaseShortArrayElements(input,samples,0);
    return result;
}

JNIEXPORT void JNICALL Java_com_jaryd_voiphone_audio_AudioProcessor__1putPlayback
        (JNIEnv *env, jobject obj, jlong context, jshortArray input){
    AudioProcessor* processor = (AudioProcessor*)context;
    jshort * sample= env->GetShortArrayElements(input,JNI_FALSE);
    processor->putPlayBack(sample);
}
JNIEXPORT void JNICALL Java_com_jaryd_voiphone_audio_AudioProcessor__1cancle
        (JNIEnv *env, jobject, jlong context, jshortArray input, jint size, jshortArray echo){
    AudioProcessor* processor = (AudioProcessor*)context;
    jshort * sample = env->GetShortArrayElements(input,JNI_FALSE);
    jshort * Echo = env->GetShortArrayElements(echo,JNI_FALSE);
    processor->cancle(sample,size,Echo);
}
