/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
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

package ch.fhnw.ether.tool;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.geom.RGBA;
import ch.fhnw.ether.geom.Vec3;
import ch.fhnw.ether.model.GenericMesh;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.shader.Lines;
import ch.fhnw.ether.render.util.Primitives;
import ch.fhnw.ether.view.IView;

import com.jogamp.newt.event.MouseEvent;

public class NavigationTool extends AbstractTool {
	public static final RGBA GRID_COLOR = RGBA.GRAY;

	private int button;
	private int mouseX;
	private int mouseY;

	private IRenderable renderable;

	// TODO: make grid dynamic/configurable

	public NavigationTool(IController controller) {
		super(controller);
		renderable = controller.getRenderer().createRenderable(IRenderer.Pass.DEPTH, new Lines(GRID_COLOR), makeGrid());
		// XXX hack: currently grid is always enabled
		activate();
	}

	@Override
	public void activate() {
		getController().getRenderer().addRenderables(renderable);
	}

	@Override
	public void deactivate() {
		getController().getRenderer().removeRenderables(renderable);
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
		} else if (button == MouseEvent.BUTTON2 || button == MouseEvent.BUTTON3) {
			view.getCamera().addToTranslateX(e.getX() - mouseX);
			view.getCamera().addToTranslateY(mouseY - e.getY());
		}
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseEvent e, IView view) {
		view.getCamera().addToTranslateX(e.getRotation()[0]);
		view.getCamera().addToDistance(-0.125f * e.getRotation()[1]);
	}

	private static GenericMesh makeGrid() {
		GenericMesh mesh = new GenericMesh();
		List<Vec3> lines = new ArrayList<>();

		int gridNumLines = 12;
		float gridSpacing = 0.1f;

		// add axis lines
		float e = 0.5f * gridSpacing * (gridNumLines + 1);
		Primitives.addLine(lines, -e, 0, e, 0);
		Primitives.addLine(lines, 0, -e, 0, e);

		// add grid lines
		int n = gridNumLines / 2;
		for (int i = 1; i <= n; ++i) {
			Primitives.addLine(lines, i * gridSpacing, -e, i * gridSpacing, e);
			Primitives.addLine(lines, -i * gridSpacing, -e, -i * gridSpacing, e);
			Primitives.addLine(lines, -e, i * gridSpacing, e, i * gridSpacing);
			Primitives.addLine(lines, -e, -i * gridSpacing, e, -i * gridSpacing);
		}
		
		mesh.setLines(Vec3.toArray(lines));
		return mesh;
	}
}
