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

package ch.ethz.ether.examples.metrobuzz.scene;

import java.awt.event.KeyEvent;

import ch.ethz.ether.examples.metrobuzz.model.Model;
import ch.ethz.ether.examples.metrobuzz.tools.AreaTool;
import ch.ethz.ether.render.forward.ForwardRenderer;
import ch.ethz.ether.scene.AbstractScene;
import ch.ethz.ether.tools.ITool;
import ch.ethz.ether.ui.Button;
import ch.ethz.ether.ui.Button.IButtonAction;
import ch.ethz.ether.view.IView;

public class Scene extends AbstractScene {
	private final ITool areaTool = new AreaTool(this);
    
    public Scene() {
        super(new ForwardRenderer());
        addUI();
    }

    @Override
    public Model getModel() {
        return (Model) super.getModel();
    }
    
    private void addUI() {
        getUI().addButton(new Button(0, 0, "PICK", "Pick Tool (1)", KeyEvent.VK_1, new IButtonAction() {
            @Override
            public void execute(Button button, IView view) {
            	setCurrentTool(null);
            }
        }));

        getUI().addButton(new Button(1, 0, "AREA", "AREA Tool (2)", KeyEvent.VK_2, new IButtonAction() {
            @Override
            public void execute(Button button, IView view) {
            	setCurrentTool(areaTool);
            }
        }));

        getUI().addButton(new Button(0, 1, "F", "Frame Scene (F)", KeyEvent.VK_F, new IButtonAction() {
            @Override
            public void execute(Button button, IView view) {
                view.getCamera().frame(getModel().getBounds());
                repaintViews();
            }
        }));

        getUI().addButton(new Button(1, 1, "Quit", "Quit", KeyEvent.VK_ESCAPE, new IButtonAction() {
            @Override
            public void execute(Button button, IView view) {
                System.exit(0);
            }
        }));
    }
    
}
