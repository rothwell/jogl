/*
 * Copyright (c) 2010, Sven Gothel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Sven Gothel nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Sven Gothel BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jogamp.opengl.impl.egl;

import com.jogamp.common.os.DynamicLookupHelper;
import com.jogamp.common.os.NativeLibrary;
import java.util.*;
import javax.media.nativewindow.*;
import javax.media.opengl.*;
import com.jogamp.opengl.impl.*;
import java.security.*;

/**
 * Abstract implementation of the DynamicLookupHelper for EGL,
 * which decouples it's dependencies to EGLDrawable.
 *
 * Currently two implementations exist, one for ES1 and one for ES2.
 */
public abstract class EGLDynamicLibraryBundleInfo extends GLDynamicLibraryBundleInfo {

    protected EGLDynamicLibraryBundleInfo() {
        super();
    }

    /** Might be a desktop GL library, and might need to allow symbol access to subsequent libs */
    public boolean shallLinkGlobal() { return true; }

    public final List getToolGetProcAddressFuncNameList() {
        List res = new ArrayList();
        res.add("eglGetProcAddress");
        return res;
    }

    public final long toolDynamicLookupFunction(long toolGetProcAddressHandle, String funcName) {
        return EGL.eglGetProcAddress(toolGetProcAddressHandle, funcName);
    }

    protected List/*<String>*/ getEGLLibNamesList() {
        List/*<String>*/ eglLibNames = new ArrayList();
        // EGL
        eglLibNames.add("EGL");
        // for windows distributions using the 'unlike' lib prefix, 
        // where our tool does not add it.
        eglLibNames.add("libEGL");
        return eglLibNames;
    }
}
