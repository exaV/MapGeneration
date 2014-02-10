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
package ch.ethz.ether.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import ch.ethz.ether.view.IView;

public abstract class AbstractTool implements ITool {
    public static final int SNAP_SIZE = 4;

    private IScene scene;

    protected AbstractTool(IScene scene) {
        this.scene = scene;
    }

    protected final IScene getScene() {
        return scene;
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

    @Override
    public void viewChanged(IView view) {
    }

    // key listener

    @Override
    public void keyPressed(KeyEvent e, IView view) {
    }

    // mouse listener

    @Override
    public void mousePressed(MouseEvent e, IView view) {
    }

    @Override
    public void mouseReleased(MouseEvent e, IView view) {
    }

    // mouse motion listener

    @Override
    public void mouseMoved(MouseEvent e, IView view) {
    }

    @Override
    public void mouseDragged(MouseEvent e, IView view) {
    }

    // mouse wheel listener

    @Override
    public void mouseWheelMoved(MouseWheelEvent e, IView view) {
    }

    // TODO: get rid of this or move to PickUtil
    public static boolean snap2D(int mx, int my, int x, int y) {
        return (mx >= x - SNAP_SIZE) && (mx <= x + SNAP_SIZE) && (my >= y - SNAP_SIZE) && (my < y + SNAP_SIZE);
    }
}
