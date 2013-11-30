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

import jogamp.opengl.ProjectFloat;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ch.ethz.ether.view.IView;

public final class ProjectionUtilities {
	// XXX replace this if possible to get rid of jogl dependencies
	private static final ProjectFloat project = new ProjectFloat();

	public static boolean projectToDeviceCoordinates(IView view, float x, float y, float z, float[] v) {
		if (!project.gluProject(x, y, z, view.getCamera().getModelviewMatrix(), 0, view.getCamera().getProjectionMatrix(), 0, view.getViewport(), 0, v, 0))
			return false;
		v[0] = screenToDeviceX(view, (int) v[0]);
		v[1] = screenToDeviceY(view, (int) v[1]);
		v[2] = 0;
		return true;
	}

	public static boolean projectToScreenCoordinates(IView view, float x, float y, float z, float[] v) {
		return project.gluProject(x, y, z, view.getCamera().getModelviewMatrix(), 0, view.getCamera().getProjectionMatrix(), 0, view.getViewport(), 0, v, 0);
	}

	public static int deviceToScreenX(IView view, float x) {
		return (int) ((1.0 + x) / 2.0 * view.getWidth());
	}

	public static int deviceToScreenY(IView view, float y) {
		return (int) ((1.0 + y) / 2.0 * view.getHeight());
	}

	public static float screenToDeviceX(IView view, int x) {
		return 2f * x / view.getWidth() - 1f;
	}

	public static float screenToDeviceY(IView view, int y) {
		return 2f * y / view.getHeight() - 1f;
	}

	public static Line getRay(IView view, float winX, float winY) {
		float[] w = new float[8];
		project.gluUnProject(winX, winY, 0.1f, view.getCamera().getModelviewMatrix(), 0, view.getCamera().getProjectionMatrix(), 0, view.getViewport(), 0, w, 0);
		project.gluUnProject(winX, winY, 0.9f, view.getCamera().getModelviewMatrix(), 0, view.getCamera().getProjectionMatrix(), 0, view.getViewport(), 0, w, 4);
		return new Line(new Vector3D(w[0], w[1], w[2]), new Vector3D(w[4], w[5], w[6]));
	}
}
