/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.gl;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import ch.ethz.ether.view.IView;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * OpenGL frame class (i.e. an OpenGL window) that combines a GLCanvas and a
 * JFrame.
 *
 * @author radar
 */
public final class Frame extends GLCanvas {
    private static final long serialVersionUID = 3901950325854383346L;

    private static ArrayList<Frame> frames = new ArrayList<>();

    private final JFrame jframe;
    private FPSAnimator animator;

    private IView view;

    /**
     * Creates undecorated frame.
     *
     * @param width  the frame's width
     * @param height the frame's height
     */
    public Frame(int width, int height) {
        this(width, height, null);
    }

    /**
     * Creates a decorated or undecorated frame with given dimensions
     *
     * @param width  the frame's width
     * @param height the frame's height
     * @param title  the frame's title, nor null for an undecorated frame
     */
    public Frame(int width, int height, String title) {
        super(getCapabilities(), null, null);
        if (frames.size() > 0)
            setSharedContext(frames.get(0).getContext());
        frames.add(this);
        setPreferredSize(new Dimension(width, height));

        jframe = new JFrame();
        jframe.getContentPane().add(this);
        jframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (animator != null && animator.isStarted())
                    animator.stop();
                frames.remove(Frame.this);
                if (frames.isEmpty())
                    System.exit(0);
            }
        });
        if (title != null)
            jframe.setTitle(title);
        else
            jframe.setUndecorated(true);
        jframe.pack();
        jframe.setVisible(true);
    }

    /**
     * Sets/clears the view for this frame.
     *
     * @param view The view to be assigned, or null if view to be cleared.
     */
    public void setView(IView view) {
        if (this.view == view)
            return;
        if (this.view != null) {
            removeGLEventListener(this.view);
            removeMouseListener(this.view);
            removeMouseMotionListener(this.view);
            removeMouseWheelListener(this.view);
            removeKeyListener(this.view);
        }
        this.view = view;
        if (this.view != null) {
            addGLEventListener(this.view);
            addMouseListener(this.view);
            addMouseMotionListener(this.view);
            addMouseWheelListener(this.view);
            addKeyListener(this.view);
        }
    }

    public JFrame getJFrame() {
        return jframe;
    }

    private static GLCapabilities getCapabilities() {
        // TODO: switch to GL3/GL4 once we're getting there
        // TODO: make this configurable
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities caps = new GLCapabilities(profile);
        caps.setAlphaBits(8);
        caps.setStencilBits(16);
        // caps.setSampleBuffers(true);
        // caps.setNumSamples(4);
        return caps;
    }
}
