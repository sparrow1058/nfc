/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_hbp_HbpJni */

#ifndef _Included_com_example_hbp_HbpJni
#define _Included_com_example_hbp_HbpJni
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_hbp_HbpJni
 * Method:    HBPOpen
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_hbp_HbpJni_HBPOpen
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_hbp_HbpJni
 * Method:    SendCmd
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_example_hbp_HbpJni_SendCmd
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_hbp_HbpJni
 * Method:    GetData
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_example_hbp_HbpJni_GetData
  (JNIEnv *, jobject);

/*
 * Class:     com_example_hbp_HbpJni
 * Method:    KillRFThread
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_hbp_HbpJni_KillRFThread
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
