/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.core.windows;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.log.Log;
import com.oracle.svm.core.util.VMError;
import com.oracle.svm.hosted.jni.JNIRuntimeAccess;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.c.function.CLibrary;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.windows.headers.WinSock;

import java.net.InetAddress;

@Platforms(Platform.WINDOWS.class)
@AutomaticFeature
@CLibrary("net")
class WindowsJavaNetSubstitutionsFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        try {
            /* Common Networking Classes */
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface").getDeclaredField("name"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface").getDeclaredField("displayName"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface").getDeclaredField("index"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface").getDeclaredField("addrs"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface").getDeclaredField("bindings"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface").getDeclaredField("childs"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.NetworkInterface").getDeclaredConstructor());

            JNIRuntimeAccess.register(access.findClassByName("java.net.InterfaceAddress"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InterfaceAddress").getDeclaredConstructor());
            JNIRuntimeAccess.register(access.findClassByName("java.net.InterfaceAddress").getDeclaredField("address"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InterfaceAddress").getDeclaredField("broadcast"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InterfaceAddress").getDeclaredField("maskLength"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress").getDeclaredField("holder"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress").getDeclaredField("preferIPv6Address"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress").getDeclaredMethod("anyLocalAddress"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddressContainer"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddressContainer").getDeclaredField("addr"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress$InetAddressHolder"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress$InetAddressHolder").getDeclaredField("address"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress$InetAddressHolder").getDeclaredField("family"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress$InetAddressHolder").getDeclaredField("hostName"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.InetAddress$InetAddressHolder").getDeclaredField("originalHostName"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet4Address"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet4Address").getDeclaredConstructor());

            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address").getDeclaredField("holder6"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address").getDeclaredField("cached_scope_id"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address").getDeclaredConstructor());
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address$Inet6AddressHolder"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address$Inet6AddressHolder").getDeclaredField("ipaddress"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address$Inet6AddressHolder").getDeclaredField("scope_id"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address$Inet6AddressHolder").getDeclaredField("scope_id_set"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.Inet6Address$Inet6AddressHolder").getDeclaredField("scope_ifname"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramPacket"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramPacket").getDeclaredField("address"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramPacket").getDeclaredField("port"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramPacket").getDeclaredField("buf"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramPacket").getDeclaredField("offset"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramPacket").getDeclaredField("length"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramPacket").getDeclaredField("bufLength"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.InetSocketAddress").getDeclaredConstructor(InetAddress.class, int.class));

            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketException"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketException").getDeclaredConstructor(String.class));
            JNIRuntimeAccess.register(access.findClassByName("java.net.ConnectException"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.ConnectException").getDeclaredConstructor(String.class));

            /* Windows specific classes */
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketInputStream"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketOutputStream"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.DualStackPlainDatagramSocketImpl"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramSocketImpl"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramSocketImpl").getDeclaredField("fd"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.DatagramSocketImpl").getDeclaredField("localPort"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.AbstractPlainDatagramSocketImpl"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.AbstractPlainDatagramSocketImpl").getDeclaredField("timeout"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.AbstractPlainDatagramSocketImpl").getDeclaredField("trafficClass"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.AbstractPlainDatagramSocketImpl").getDeclaredField("connected"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.TwoStacksPlainDatagramSocketImpl"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.TwoStacksPlainDatagramSocketImpl").getDeclaredField("fd1"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.TwoStacksPlainDatagramSocketImpl").getDeclaredField("fduse"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.TwoStacksPlainDatagramSocketImpl").getDeclaredField("lastfd"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketImpl"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketImpl").getDeclaredField("fd"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketImpl").getDeclaredField("localport"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketImpl").getDeclaredField("serverSocket"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketImpl").getDeclaredField("address"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.SocketImpl").getDeclaredField("port"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.AbstractPlainSocketImpl"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.AbstractPlainSocketImpl").getDeclaredField("timeout"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.AbstractPlainSocketImpl").getDeclaredField("trafficClass"));

            JNIRuntimeAccess.register(access.findClassByName("java.net.TwoStacksPlainSocketImpl"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.TwoStacksPlainSocketImpl").getDeclaredField("fd1"));
            JNIRuntimeAccess.register(access.findClassByName("java.net.TwoStacksPlainSocketImpl").getDeclaredField("lastfd"));

            JNIRuntimeAccess.register(access.findClassByName("java.lang.Integer").getDeclaredConstructor(int.class));
            JNIRuntimeAccess.register(access.findClassByName("java.lang.Integer").getDeclaredField("value"));

            JNIRuntimeAccess.register(access.findClassByName("java.lang.Boolean"));
            JNIRuntimeAccess.register(access.findClassByName("java.lang.Boolean").getDeclaredConstructor(boolean.class));
            JNIRuntimeAccess.register(access.findClassByName("java.lang.Boolean").getDeclaredMethod("getBoolean", String.class));

        } catch (Exception e) {
            VMError.shouldNotReachHere("WindowsJavaNetSubstitutionsFeature: Error registering class or method: ", e);
        }
    }
}

@TargetClass(className = "java.net.NetworkInterface")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_NetworkInterface {

    @Alias
    static native void init();

}

@TargetClass(className = "java.net.DatagramPacket")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_DatagramPacket {

    @Alias
    static native void init();

}

@TargetClass(className = "java.net.DualStackPlainDatagramSocketImpl")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_DualStackPlainDatagramSocketImpl {

    @Alias
    static native void initIDs();

}

@TargetClass(className = "java.net.DualStackPlainSocketImpl")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_DualStackPlainSocketImpl {

    @Alias
    static native void initIDs();

}

@TargetClass(className = "java.net.TwoStacksPlainDatagramSocketImpl")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_TwoStacksPlainDatagramSocketImpl {

    @Alias
    static native void init();

}

@TargetClass(className = "java.net.TwoStacksPlainSocketImpl")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_TwoStacksPlainSocketImpl {

    @Alias
    static native void initProto();

}

@TargetClass(className = "java.net.SocketInputStream")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_SocketInputStream {

    @Alias
    static native void init();

}

@TargetClass(className = "java.net.SocketOutputStream")
@Platforms(Platform.WINDOWS.class)
final class Target_java_net_SocketOutputStream {

    @Alias
    static native void init();

}

@Platforms(Platform.WINDOWS.class)
public final class WindowsJavaNetSubstitutions {

    public static boolean initIDs() {
        try {
            WinSock.init();
            System.loadLibrary("net");
            Target_java_net_SocketInputStream.init();
            Target_java_net_SocketOutputStream.init();
            Target_java_net_NetworkInterface.init();
            Target_java_net_DatagramPacket.init();
            Target_java_net_DualStackPlainSocketImpl.initIDs();
            Target_java_net_TwoStacksPlainSocketImpl.initProto();
            Target_java_net_DualStackPlainDatagramSocketImpl.initIDs();
            Target_java_net_TwoStacksPlainDatagramSocketImpl.init();
            return true;
        } catch (UnsatisfiedLinkError e) {
            Log.log().string("System.loadLibrary of builtIn net library failed, " + e).newline();
            return false;
        }
    }
}
