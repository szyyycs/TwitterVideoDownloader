// IBuyBlueBerry.aidl
package com.ycs.AIDLTest;
import org.qiyi.video.svg.IPCCallback;
// Declare any non-default types here with import statements

interface IBuyBlueBerry {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void buyNum(int num,IPCCallback callback);
    int getPrice();
}