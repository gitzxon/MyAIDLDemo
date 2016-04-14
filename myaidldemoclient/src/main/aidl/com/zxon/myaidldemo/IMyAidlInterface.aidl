// IMyAidlInterface.aidl
package com.zxon.myaidldemo;

// Declare any non-default types here with import statements
import com.zxon.myaidldemo.CustomBean;
import com.zxon.myaidldemo.ICallback;

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    CustomBean getServerResult(in CustomBean info, ICallback callback);
}
