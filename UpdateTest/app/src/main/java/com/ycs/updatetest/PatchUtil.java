package com.ycs.updatetest;

/**
 * <pre>
 *     author : yangchaosheng
 *     e-mail : yangchaosheng@hisense.com
 *     time   : 2022/04/28
 *     desc   :
 * </pre>
 */
public class PatchUtil {
    static {
        System.loadLibrary("native-lib");
    }
    public native static void patchAPK(String oldApkFile,String newApkFile,String patchFile);
}
