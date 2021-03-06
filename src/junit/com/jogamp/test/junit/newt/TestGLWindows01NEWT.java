/*
 * Copyright (c) 2010 Sven Gothel. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name Sven Gothel or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SVEN GOTHEL HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.jogamp.test.junit.newt;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import javax.media.nativewindow.*;
import javax.media.opengl.*;

import com.jogamp.opengl.util.Animator;
import com.jogamp.newt.*;
import com.jogamp.newt.opengl.*;
import java.io.IOException;

import com.jogamp.test.junit.util.MiscUtils;
import com.jogamp.test.junit.jogl.demos.gl2.gears.Gears;

public class TestGLWindows01NEWT {
    static GLProfile glp;
    static int width, height;
    static long duration = 100; // ms

    @BeforeClass
    public static void initClass() {
        width  = 640;
        height = 480;
        glp = GLProfile.getDefault();
    }

    static GLWindow createWindow(Screen screen, GLCapabilities caps, int width, int height, boolean onscreen, boolean undecorated) {
        Assert.assertNotNull(caps);
        caps.setOnscreen(onscreen);
        // System.out.println("Requested: "+caps);

        //
        // Create native windowing resources .. X11/Win/OSX
        // 
        GLWindow glWindow;
        if(null!=screen) {
            Window window = NewtFactory.createWindow(screen, caps, onscreen && undecorated);
            Assert.assertNotNull(window);
            glWindow = GLWindow.create(window);
        } else {
            glWindow = GLWindow.create(caps, onscreen && undecorated);
        }
        Assert.assertNotNull(glWindow);
        Assert.assertEquals(false,glWindow.isNativeWindowValid());
        glWindow.setSize(width, height);
        Assert.assertEquals(false,glWindow.isVisible());
        glWindow.setVisible(true);
        Assert.assertEquals(true,glWindow.isVisible());
        Assert.assertEquals(true,glWindow.isNativeWindowValid());
        // Assert.assertEquals(width,glWindow.getWidth());
        // Assert.assertEquals(height,glWindow.getHeight());
        // System.out.println("Created: "+glWindow);

        //
        // Create native OpenGL resources .. XGL/WGL/CGL .. 
        // equivalent to GLAutoDrawable methods: setVisible(true)
        // 
        caps = (GLCapabilities) glWindow.getGraphicsConfiguration().getNativeGraphicsConfiguration().getChosenCapabilities();
        Assert.assertNotNull(caps);
        Assert.assertTrue(caps.getGreenBits()>5);
        Assert.assertTrue(caps.getBlueBits()>5);
        Assert.assertTrue(caps.getRedBits()>5);
        Assert.assertEquals(caps.isOnscreen(),onscreen);

        GLEventListener demo = new Gears();
        setDemoFields(demo, glWindow);
        glWindow.addGLEventListener(demo);

        return glWindow;
    }

    static void destroyWindow(GLWindow glWindow, boolean deep) {
        if(null!=glWindow) {
            glWindow.destroy(deep);
        }
    }

    @Test
    public void testWindowNativeRecreate01Simple() throws InterruptedException {
        GLCapabilities caps = new GLCapabilities(glp);
        Assert.assertNotNull(caps);
        GLWindow window = createWindow(null, caps, width, height, true /* onscreen */, false /* undecorated */);

        window.display();
        window.destroy();
        Assert.assertEquals(false,window.isNativeWindowValid());
        Assert.assertEquals(false,window.isVisible());

        window.display();
        Assert.assertEquals(false,window.isNativeWindowValid());
        Assert.assertEquals(false,window.isVisible());

        window.setVisible(true);
        Assert.assertEquals(true,window.isNativeWindowValid());
        Assert.assertEquals(true,window.isVisible());
        window.display();

        Animator animator = new Animator(window);
        animator.start();
        while(animator.isAnimating() && animator.getDuration()<duration) {
            Thread.sleep(100);
        }
        animator.stop();
        destroyWindow(window, true);
    }

    @Test
    public void testWindowDecor01Simple() throws InterruptedException {
        GLCapabilities caps = new GLCapabilities(glp);
        Assert.assertNotNull(caps);
        GLWindow window = createWindow(null, caps, width, height, true /* onscreen */, false /* undecorated */);
        Animator animator = new Animator(window);
        animator.start();
        while(animator.isAnimating() && animator.getDuration()<duration) {
            Thread.sleep(100);
        }
        animator.stop();
        destroyWindow(window, true);
    }

    @Test
    public void testWindowDecor02DestroyWinTwiceA() throws InterruptedException {
        GLCapabilities caps = new GLCapabilities(glp);
        Assert.assertNotNull(caps);
        GLWindow window = createWindow(null, caps, width, height, true /* onscreen */, false /* undecorated */);
        Animator animator = new Animator(window);
        animator.start();
        while(animator.isAnimating() && animator.getDuration()<duration) {
            Thread.sleep(100);
        }
        animator.stop();
        destroyWindow(window, false);
        destroyWindow(window, true);
    }

    @Test
    public void testWindowDecor03TwoWin() throws InterruptedException {
        GLCapabilities caps = new GLCapabilities(glp);
        Assert.assertNotNull(caps);

        Display display1 = NewtFactory.createDisplay(null); // local display
        Assert.assertNotNull(display1);
        Display display2 = NewtFactory.createDisplay(null); // local display
        Assert.assertNotNull(display2);
        Assert.assertEquals(display1, display2); // must be equal: same thread - same display

        Screen screen1  = NewtFactory.createScreen(display1, 0); // screen 0
        Assert.assertNotNull(screen1);
        GLWindow window1 = createWindow(screen1, caps, width, height, true /* onscreen */, false /* undecorated */);
        Assert.assertNotNull(window1);

        Screen screen2  = NewtFactory.createScreen(display2, 0); // screen 0
        Assert.assertNotNull(screen2);
        GLWindow window2 = createWindow(screen2, caps, width, height, true /* onscreen */, false /* undecorated */);
        Assert.assertNotNull(window2);

        Animator animator1 = new Animator(window1);
        animator1.start();
        Animator animator2 = new Animator(window2);
        animator2.start();
        while(animator1.isAnimating() && animator1.getDuration()<duration) {
            Thread.sleep(100);
        }

        animator2.stop();
        Assert.assertEquals(false, animator2.isAnimating());
        destroyWindow(window2, true);

        animator1.stop();
        Assert.assertEquals(false, animator1.isAnimating());
        destroyWindow(window1, true);
    }

    public static void setDemoFields(GLEventListener demo, GLWindow glWindow) {
        Assert.assertNotNull(demo);
        Assert.assertNotNull(glWindow);
        if(!MiscUtils.setFieldIfExists(demo, "window", glWindow.getInnerWindow())) {
            MiscUtils.setFieldIfExists(demo, "glWindow", glWindow);
        }
    }

    public static void main(String args[]) throws IOException {
        String tstname = TestGLWindows01NEWT.class.getName();
        org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner.main(new String[] {
            tstname,
            "filtertrace=true",
            "haltOnError=false",
            "haltOnFailure=false",
            "showoutput=true",
            "outputtoformatters=true",
            "logfailedtests=true",
            "logtestlistenerevents=true",
            "formatter=org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter",
            "formatter=org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter,TEST-"+tstname+".xml" } );
    }

}
