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

package ch.fhnw.ether.examples.metrobuzz.tool;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.geom.Line;
import ch.fhnw.ether.geom.Plane;
import ch.fhnw.ether.geom.ProjectionUtil;
import ch.fhnw.ether.geom.RGBA;
import ch.fhnw.ether.geom.Vec3;
import ch.fhnw.ether.model.CubeMesh;
import ch.fhnw.ether.model.CubeMesh.Origin;
import ch.fhnw.ether.model.IPickable;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.shader.Triangles;
import ch.fhnw.ether.tool.AbstractTool;
import ch.fhnw.ether.view.IView;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

public final class AreaTool extends AbstractTool {
	private static final RGBA TOOL_COLOR = RGBA.YELLOW;

	private static final float KEY_INCREMENT = 0.01f;

	private CubeMesh mesh = new CubeMesh(Origin.BOTTOM_CENTER);

	private boolean moving = false;

	private float xOffset = 0;
	private float yOffset = 0;

	private final IRenderable area;

	public AreaTool(IController controller) {
		super(controller);
		mesh.setScale(new Vec3(0.1, 0.1, 0.001));
		area = controller.getRenderer().createRenderable(Pass.DEPTH, new Triangles(TOOL_COLOR, false), mesh);
	}

	@Override
	public void activate() {
		getController().getRenderer().addRenderables(area);
	}

	@Override
	public void deactivate() {
		getController().getRenderer().removeRenderables(area);
	}

	@Override
	public void keyPressed(KeyEvent e, IView view) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			yOffset += KEY_INCREMENT;
			break;
		case KeyEvent.VK_DOWN:
			yOffset -= KEY_INCREMENT;
			break;
		case KeyEvent.VK_LEFT:
			xOffset -= KEY_INCREMENT;
			break;
		case KeyEvent.VK_RIGHT:
			xOffset += KEY_INCREMENT;
			break;
		}

		mesh.setTranslation(new Vec3(xOffset, yOffset, 0));
		area.requestUpdate();
		view.getController().repaintViews();
	}

	@Override
	public void mousePressed(MouseEvent e, IView view) {
		int x = e.getX();
		int y = view.getViewport().h - e.getY();
		if (mesh.pick(IPickable.PickMode.POINT, x, y, 0, 0, view, null))
			moving = true;
	}

	@Override
	public void mouseDragged(MouseEvent e, IView view) {
		if (moving) {
			Line line = ProjectionUtil.getRay(view, e.getX(), view.getViewport().h - e.getY());
			Plane plane = new Plane(new Vec3(0, 0, 1));
			Vec3 p = plane.intersection(line);
			if (p != null) {
				xOffset = p.x;
				yOffset = p.y;
				mesh.setTranslation(new Vec3(xOffset, yOffset, 0));
				area.requestUpdate();
				view.getController().repaintViews();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e, IView view) {
		moving = false;
	}
}
