/*
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.runtime.global;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.llvm.runtime.LLVMLanguage;
import com.oracle.truffle.llvm.runtime.NodeFactory;
import com.oracle.truffle.llvm.runtime.interop.LLVMInternalTruffleObject;
import com.oracle.truffle.llvm.runtime.memory.LLVMMemory;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMObjectAccess;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMObjectNativeLibrary;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMToNativeNode;
import com.oracle.truffle.llvm.runtime.pointer.LLVMNativePointer;

public final class LLVMGlobalContainer implements LLVMObjectAccess, LLVMInternalTruffleObject, LLVMObjectNativeLibrary.Provider {

    private long address;
    private Object contents;

    public LLVMGlobalContainer() {
        contents = 0L;
    }

    public Object get() {
        return contents;
    }

    public void set(Object value) {
        contents = value;
    }

    public boolean isInNative() {
        return address != 0;
    }

    public long getAddress() {
        return address;
    }

    @SuppressWarnings("static-method")
    public int getSize() {
        return 1;
    }

    @TruffleBoundary
    public void transformToNative(LLVMToNativeNode toNative) {
        if (address == 0) {
            LLVMMemory memory = LLVMLanguage.getLanguage().getCapability(LLVMMemory.class);
            LLVMNativePointer pointer = memory.allocateMemory(8);
            address = pointer.asNative();
            long value;
            if (contents instanceof Number) {
                value = ((Number) contents).longValue();
            } else {
                value = toNative.executeWithTarget(contents).asNative();
            }
            memory.putI64(pointer, value);
        }
    }

    @Override
    public ForeignAccess getForeignAccess() {
        return ContainerForeignAccessForeign.ACCESS;
    }

    @Override
    public LLVMObjectReadNode createReadNode() {
        return getNodeFactory().createGlobalContainerReadNode();
    }

    @Override
    public LLVMObjectWriteNode createWriteNode() {
        return getNodeFactory().createGlobalContainerWriteNode();
    }

    public void dispose() {
        if (address != 0) {
            LLVMMemory memory = LLVMLanguage.getLanguage().getCapability(LLVMMemory.class);
            memory.free(address);
            address = 0;
        }
    }

    @Override
    @TruffleBoundary
    public String toString() {
        return String.format("LLVMGlobalContainer (address = 0x%x, contents = %s)", address, contents);
    }

    private static NodeFactory getNodeFactory() {
        return LLVMLanguage.getLanguage().getContextReference().get().getNodeFactory();
    }

    @MessageResolution(receiverType = LLVMGlobalContainer.class)
    static class ContainerForeignAccess {
        @CanResolve
        public abstract static class Check extends Node {

            protected static boolean test(TruffleObject receiver) {
                return receiver instanceof LLVMGlobalContainer;
            }
        }

        @Resolve(message = "HAS_SIZE")
        public abstract static class ForeignHasSizeNode extends Node {

            protected Object access(@SuppressWarnings("unused") LLVMGlobalContainer receiver) {
                return true;
            }
        }

        @Resolve(message = "GET_SIZE")
        public abstract static class ForeignGetSizeNode extends Node {

            protected Object access(LLVMGlobalContainer receiver) {
                return receiver.getSize();
            }
        }

        @Resolve(message = "READ")
        public abstract static class ForeignReadNode extends Node {

            protected Object access(LLVMGlobalContainer receiver, int index) {
                assert index == 0;
                return receiver.get();
            }
        }

        @Resolve(message = "IS_POINTER")
        public abstract static class ForeignIsPointerNode extends Node {

            protected boolean access(LLVMGlobalContainer receiver) {
                return receiver.getAddress() != 0;
            }
        }

        @Resolve(message = "AS_POINTER")
        public abstract static class ForeignAsPointerNode extends Node {

            protected long access(LLVMGlobalContainer receiver) {
                return receiver.getAddress();
            }
        }

        @Resolve(message = "TO_NATIVE")
        public abstract static class ForeignToNativeNode extends Node {

            @Child private LLVMToNativeNode toNative;

            protected Object access(LLVMGlobalContainer receiver) {
                if (receiver.getAddress() == 0) {
                    if (toNative == null) {
                        CompilerDirectives.transferToInterpreterAndInvalidate();
                        toNative = insert(LLVMToNativeNode.createToNativeWithTarget());
                    }
                    receiver.transformToNative(toNative);
                }
                return receiver;
            }
        }

        @Resolve(message = "WRITE")
        public abstract static class ForeignWriteNode extends Node {

            protected Object access(LLVMGlobalContainer receiver, int index, Object value) {
                assert index == 0;
                receiver.set(value);
                return value;
            }
        }
    }

    private static final class LLVMGlobalContainerNativeLibrary extends LLVMObjectNativeLibrary {

        @Child private LLVMToNativeNode toNative;

        @Override
        public boolean guard(Object obj) {
            return obj instanceof LLVMGlobalContainer;
        }

        @Override
        public boolean isPointer(Object obj) {
            return ((LLVMGlobalContainer) obj).address != 0;
        }

        @Override
        public boolean isNull(Object obj) {
            return false;
        }

        @Override
        public long asPointer(Object obj) {
            return ((LLVMGlobalContainer) obj).address;
        }

        @Override
        public Object toNative(Object obj) {
            LLVMGlobalContainer receiver = (LLVMGlobalContainer) obj;
            if (receiver.address == 0) {
                if (toNative == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    toNative = insert(LLVMToNativeNode.createToNativeWithTarget());
                }
                receiver.transformToNative(toNative);
            }
            return receiver;
        }
    }

    @Override
    public LLVMObjectNativeLibrary createLLVMObjectNativeLibrary() {
        return new LLVMGlobalContainerNativeLibrary();
    }
}
