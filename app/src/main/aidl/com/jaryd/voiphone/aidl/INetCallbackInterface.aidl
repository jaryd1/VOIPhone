// INetCallbackInterface.aidl
package com.jaryd.voiphone.aidl;

// Declare any non-default types here with import statements

import com.jaryd.voiphone.aidl.Netpacket;

interface INetCallbackInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void handlePacket(out NetPacket packet);
}
