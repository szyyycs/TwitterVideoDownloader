//
// Created by Hisense on 2022/4/28.
//

#include <jni.h>
#include <string>
#include "bspatch.h"
extern "C"
JNIEXPORT void JNICALL
Java_com_ycs_ctest_PatchUtil_patchAPK(JNIEnv *env, jclass clazz, jstring old_apk_file,
                                      jstring new_apk_file, jstring patch_file) {
    int argc = 4;
    char * argv[argc];
    argv[0] = "bspatch";
    argv[1] = (char*) (env->GetStringUTFChars(old_apk_file, 0));
    argv[2] = (char*) (env->GetStringUTFChars(new_apk_file, 0));
    argv[3] = (char*) (env->GetStringUTFChars(patch_file, 0));

    //调用合并的方法
    main(argc, argv);

    env->ReleaseStringUTFChars(old_apk_file, argv[1]);
    env->ReleaseStringUTFChars(new_apk_file, argv[2]);
    env->ReleaseStringUTFChars(patch_file, argv[3]);
}