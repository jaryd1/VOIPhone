/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_jaryd_opuscodec_OpusUtils */

#ifndef _Included_com_jaryd_opuscodec_OpusUtils
#define _Included_com_jaryd_opuscodec_OpusUtils
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_jaryd_opuscodec_OpusUtils
 * Method:    _init
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_com_jaryd_opuscodec_OpusUtils__1init
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     com_jaryd_opuscodec_OpusUtils
 * Method:    _encode
 * Signature: (J[SI[BI)I
 */
JNIEXPORT jint JNICALL Java_com_jaryd_opuscodec_OpusUtils__1encode
  (JNIEnv *, jobject, jlong, jshortArray, jint, jbyteArray, jint);

/*
 * Class:     com_jaryd_opuscodec_OpusUtils
 * Method:    _decode
 * Signature: (J[BI[SI)I
 */
JNIEXPORT jint JNICALL Java_com_jaryd_opuscodec_OpusUtils__1decode
  (JNIEnv *, jobject, jlong, jbyteArray, jint, jshortArray, jint);



#ifdef __cplusplus
}
#endif
#endif
