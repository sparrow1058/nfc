/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_rfid_Jni */

#ifndef _Included_com_example_rfid_Jni
#define _Included_com_example_rfid_Jni
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_rfid_Jni
 * Method:    load
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_rfid_Jni_load
  (JNIEnv *, jobject);

/*
 * Class:     com_example_rfid_Jni
 * Method:    RFSetDevice
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_example_rfid_Jni_RFSetDevice
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_example_rfid_Jni
 * Method:    RFReadBlock
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_example_rfid_Jni_RFReadBlock
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_example_rfid_Jni
 * Method:    RFWriteBlock
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_com_example_rfid_Jni_RFWriteBlock
  (JNIEnv *, jobject, jint, jbyteArray);

/*
 * Class:     com_example_rfid_Jni
 * Method:    RFGetUid
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_example_rfid_Jni_RFGetUid
  (JNIEnv *, jobject);

/*
 * Class:     com_example_rfid_Jni
 * Method:    RFInputKey
 * Signature: ([B)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_rfid_Jni_RFInputKey
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     com_example_rfid_Jni
 * Method:    RFChangeKey
 * Signature: (I[B[B)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_rfid_Jni_RFChangeKey
  (JNIEnv *, jobject, jint, jbyteArray, jbyteArray);

/*
 * Class:     com_example_rfid_Jni
 * Method:    KillRFThread
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_rfid_Jni_KillRFThread
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
