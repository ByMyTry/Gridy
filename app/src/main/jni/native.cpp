#include <string.h>
#include <jni.h>

extern "C" {
JNIEXPORT jstring JNICALL Java_com_example_lenovo_testapplication_PlayActivity_getMessageFromNative(JNIEnv *env, jobject obj) {
    return env->NewStringUTF("Message From C++");
}
}