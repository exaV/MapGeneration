/*
 * Copyright (c) 2013 - 2014, ETH Zurich & FHNW (Stefan Muller Arisona)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of ETH Zurich nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.ethz.ether.tools;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import ch.ethz.ether.render.AbstractRenderGroup;
import ch.ethz.ether.render.IRenderGroup;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderGroup.Type;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.render.util.Primitives;
import ch.ethz.ether.scene.IScene;
import ch.ethz.ether.view.IView;
import ch.ethz.util.IAddOnlyFloatList;

public class NavigationTool extends AbstractTool {
    public static final float[] GRID_COLOR = {0.5f, 0.5f, 0.5f, 1.0f};

    private int button;
    private int mouseX;
    private int mouseY;

    // TODO: make grid dynamic/configurable
    private IRenderGroup grid = new AbstractRenderGroup(Source.TOOL, Type.LINES) {
        @Override
        public void getVertices(IAddOnlyFloatList dst) {
            int gridNumLines = 12;
            float gridSpacing = 0.1f;

            // add axis lines
            float e = 0.5f * gridSpacing * (gridNumLines + 1);
            Primitives.addLine(dst, -e, 0, e, 0);
            Primitives.addLine(dst, 0, -e, 0, e);

            // add grid lines
            int n = gridNumLines / 2;
            for (int i = 1; i <= n; ++i) {
                Primitives.addLine(dst, i * gridSpacing, -e, i * gridSpacing, e);
                Primitives.addLine(dst, -i * gridSpacing, -e, -i * gridSpacing, e);
                Primitives.addLine(dst, -e, i * gridSpacing, e, i * gridSpacing);
                Primitives.addLine(dst, -e, -i * gridSpacing, e, -i * gridSpacing);
            }
        }

        @Override
        public float[] getColor() {
            return GRID_COLOR;
        }
    };

    public NavigationTool(IScene scene) {
        super(scene);
        // XXX hack: currently grid is always enabled
        activate();
    }

    @Override
    public void activate() {
        IRenderer.GROUPS.add(grid);
    }

    @Override
    public void deactivate() {
        IRenderer.GROUPS.remove(grid);
    }

    @Override
    public void mousePressed(MouseEvent e, IView view) {
        button = e.getButton();
    }

    @Override
    public void mouseMoved(MouseEvent e, IView view) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e, IView view) {
        if (button == MouseEvent.BUTTON1) {
            view.getCamera().addToRotateZ(e.getX() - mouseX);
            view.getCamera().addToRotateX(e.getY() - mouseY);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            view.getCamera().addToTranslateX(e.getX() - mouseX);
            view.getCamera().addToTranslateY(mouseY - e.getY());
        }
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e, IView view) {
        view.getCamera().addToDistance(0.25f * e.getWheelRotation());
    }
}
