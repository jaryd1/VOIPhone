// INetBinderInterface.aidl
package com.jaryd.voiphone.aidl;

// Declare any non-default types here with import statements
import com.jaryd.voiphone.aidl.INetCallbackInterface;
import com.jaryd.voiphone.aidl.Netpacket;

interface INetBinderInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerCallBack(INetCallbackInterface callback);
    void removeCallBack();
    void runService();
    void send(in NetPacket packet);
}
