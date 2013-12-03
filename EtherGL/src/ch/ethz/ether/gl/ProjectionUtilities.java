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

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ch.ethz.ether.view.IView;

import com.jogamp.opengl.math.FloatUtil;

public final class ProjectionUtilities {
	public static boolean projectToDevice(IView view, float x, float y, float z, float[] v) {
		if (!projectToScreen(view, x, y, z, v))
			return false;
		v[0] = screenToDeviceX(view, (int) v[0]);
		v[1] = screenToDeviceY(view, (int) v[1]);
		v[2] = 0;
		return true;
	}

	public static boolean projectToScreen(IView view, float x, float y, float z, float[] v) {
		return projectToScreen(view.getCamera().getViewMatrix(), view.getCamera().getProjectionMatrix(), view.getViewport(), x, y, z, v);
	}

	private static boolean projectToScreen(float[] viewMatrix, float[] projMatrix, int[] viewport, float x, float y, float z, float[] v) {
		final float[] in = new float[4];
		final float[] out = new float[4];

		in[0] = x;
		in[1] = y;
		in[2] = z;
		in[3] = 1.0f;

		Matrix4x4.multiplyVector(viewMatrix, in, out);
		Matrix4x4.multiplyVector(projMatrix, out, in);

		if (in[3] == 0.0f)
			return false;

		in[3] = (1.0f / in[3]) * 0.5f;

		// map x, y and z to range [0, 1]
		in[0] = in[0] * in[3] + 0.5f;
		in[1] = in[1] * in[3] + 0.5f;
		in[2] = in[2] * in[3] + 0.5f;

		// map x,y to viewport
		v[0] = in[0] * viewport[2] + viewport[0];
		v[1] = in[1] * viewport[3] + viewport[1];
		v[2] = in[2];
		return true;
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

	public static Line getRay(IView view, float x, float y) {
		float[] p0 = new float[4];
		float[] p1 = new float[4];
		float[] vm = view.getCamera().getViewMatrix();
		float[] pm = view.getCamera().getProjectionMatrix();
		int[] vp = view.getViewport();
		unprojectFromScreen(vm, pm, vp, x, y, 0.1f, p0);
		unprojectFromScreen(vm, pm, vp, x, y, 0.9f, p1);
		return new Line(new Vector3D(p0[0], p0[1], p0[2]), new Vector3D(p1[0], p1[1], p1[2]));
	}

	public static boolean unprojectFromScreen(IView view, float x, float y, float z, float[] v) {
		return unprojectFromScreen(view.getCamera().getViewMatrix(), view.getCamera().getProjectionMatrix(), view.getViewport(), x, y, z, v);
	}

	public static boolean unprojectFromScreen(float[] viewMatrix, float[] projectionMatrix, int[] viewport, float x, float y, float z, float[] v) {
		final float[] in = new float[4];
		final float[] out = new float[4];
		final float[] matrix = new float[16];

		Matrix4x4.multiply(projectionMatrix, viewMatrix, matrix);
		FloatUtil.multMatrixf(projectionMatrix, 0, viewMatrix, 0, matrix, 0);

		if (Matrix4x4.invert(matrix, matrix) == null) {
			return false;
		}

		in[0] = x;
		in[1] = y;
		in[2] = z;
		in[3] = 1.0f;

		// Map x and y from window coordinates
		in[0] = (in[0] - viewport[0]) / viewport[2];
		in[1] = (in[1] - viewport[1]) / viewport[3];

		// Map to range -1 to 1
		in[0] = in[0] * 2 - 1;
		in[1] = in[1] * 2 - 1;
		in[2] = in[2] * 2 - 1;

		FloatUtil.multMatrixVecf(matrix, in, out);

		if (out[3] == 0.0) {
			return false;
		}

		out[3] = 1.0f / out[3];

		v[0] = out[0] * out[3];
		v[1] = out[1] * out[3];
		v[2] = out[2] * out[3];

		return true;
	}
}
