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

import java.util.Arrays;

/**
 * Basic matrix utilities for dealing with OpenGL 4x4 matrices. Note: Some of
 * this code is taken from JOGL. For more complex matrix operations, use the
 * included Apache commons library.
 * 
 * @author radar
 * 
 */
public final class Matrix4x4 {
	/**
	 * Returns new identity matrix.
	 * 
	 * @return column-major identity matrix
	 */
	public static float[] identity() {
		return identity(null);
	}

	/**
	 * Sets and returns identity matrix.
	 * 
	 * @param m
	 *            the matrix to be set to the identity matrix or null to return
	 *            new identity matrix
	 * @return column-major identity matrix
	 */
	public static float[] identity(float[] m) {
		if (m != null) {
			m[0] = m[5] = m[10] = m[15] = 1;
			m[1] = m[2] = m[3] = m[4] = m[6] = m[7] = m[8] = m[9] = m[11] = m[12] = m[13] = m[14] = 0;
		} else {
			m = new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
		}
		return m;
	}

	/**
	 * Multiplies two matrices. a = a * b
	 * 
	 * @param a
	 *            4x4 matrix in column-major order (also result)
	 * @param b
	 *            4x4 matrix in column-major order
	 * @return multiplied column-major matrix
	 */
	public static float[] multiply(float[] a, float[] b) {
		for (int i = 0; i < 4; i++) {
			float ai0 = a[i + 0 * 4];
			float ai1 = a[i + 1 * 4];
			float ai2 = a[i + 2 * 4];
			float ai3 = a[i + 3 * 4];
			a[i + 0 * 4] = ai0 * b[0 + 0 * 4] + ai1 * b[1 + 0 * 4] + ai2 * b[2 + 0 * 4] + ai3 * b[3 + 0 * 4];
			a[i + 1 * 4] = ai0 * b[0 + 1 * 4] + ai1 * b[1 + 1 * 4] + ai2 * b[2 + 1 * 4] + ai3 * b[3 + 1 * 4];
			a[i + 2 * 4] = ai0 * b[0 + 2 * 4] + ai1 * b[1 + 2 * 4] + ai2 * b[2 + 2 * 4] + ai3 * b[3 + 2 * 4];
			a[i + 3 * 4] = ai0 * b[0 + 3 * 4] + ai1 * b[1 + 3 * 4] + ai2 * b[2 + 3 * 4] + ai3 * b[3 + 3 * 4];
		}
		return a;
	}

	private static final float[] t = identity();

	/**
	 * Multiplies matrix m with translation matrix t. m = m * t
	 * 
	 * @param tx
	 *            x translation
	 * @param ty
	 *            y translation
	 * @param tz
	 *            z translation
	 * @param m
	 *            matrix to be multiplied or null to create new matrix
	 * @return multiplied matrix m
	 */
	public static float[] translate(float tx, float ty, float tz, float[] m) {
		if (m == null)
			m = identity();
		t[12] = tx;
		t[13] = ty;
		t[14] = tz;
		return multiply(m, t);
	}

	private static final float[] r = identity();

	/**
	 * Multiplies matrix m with rotation matrix r. m = m * r
	 * 
	 * @param angle
	 *            rotation angle in degrees
	 * @param x
	 *            rotation axis x
	 * @param y
	 *            rotation axis y
	 * @param z
	 *            rotation axis z
	 * @param m
	 *            matrix to be multiplied or null to create new matrix
	 * @return multiplied matrix m
	 */
	public static float[] rotate(float angle, float x, float y, float z, float[] m) {
		if (m == null)
			m = identity();

		float l = (float) Math.sqrt(x * x + y * y + z * z);
		if (l != 0 || l != 1) {
			l = 1.0f / l;
			x *= l;
			y *= l;
			z *= l;
		}

		float radians = angle * (float) Math.PI / 180.0f;
		float c = (float) Math.cos(radians);
		float ic = 1.0f - c;
		float s = (float) Math.sin(radians);

		float xy = x * y;
		float xz = x * z;
		float xs = x * s;
		float ys = y * s;
		float yz = y * z;
		float zs = z * s;

		r[0] = x * x * ic + c;
		r[1] = xy * ic + zs;
		r[2] = xz * ic - ys;
		r[4] = xy * ic - zs;
		r[5] = y * y * ic + c;
		r[6] = yz * ic + xs;
		r[8] = xz * ic + ys;
		r[9] = yz * ic - xs;
		r[10] = z * z * ic + c;

		return multiply(m, r);
	}

	/**
	 * Multiplies matrix m with scale matrix s. m = m * s
	 * 
	 * @param sx
	 *            scale x factor
	 * @param sy
	 *            scale y factor
	 * @param sz
	 *            scale z factor
	 * @param m
	 *            matrix to be multiplied or null to create new matrix
	 * @return multiplied matrix m
	 */
	public static float[] scale(float sx, float sy, float sz, float[] m) {
		if (m == null)
			m = identity(null);
		m[0] *= sx;
		m[5] *= sy;
		m[10] *= sz;
		return m;
	}

	/**
	 * Returns a perspective projection matrix. Supports far plane at infinity.
	 * 
	 * @param fovy
	 *            field of view (degrees)
	 * @param aspect
	 *            aspect ratio
	 * @param near
	 *            near plane
	 * @param far
	 *            far plane (set to Float.POSITIVE_INFINITY for far plane at
	 *            infinity)
	 * @param m
	 *            matrix to be set to perspective matrix or null to create new
	 *            matrix
	 * @return column-major perspective projection matrix
	 */
	public static float[] perspective(float fovy, float aspect, float near, float far, float[] m) {
		if (m != null) {
			Arrays.fill(m, 0);
		} else {
			m = new float[16];
		}

		double radians = fovy / 2 * Math.PI / 180;
		double sine = Math.sin(radians);
		double deltaZ = far - near;

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			throw new IllegalArgumentException("illegal arguments (fovy=" + fovy + " aspect=" + aspect + " near=" + near + " far=" + far);
		}

		double cotangent = (float) (Math.cos(radians) / sine);

		m[0] = (float) (cotangent / aspect);
		m[5] = (float) cotangent;
		m[10] = far >= Double.POSITIVE_INFINITY ? -1 : (float) (-(far + near) / deltaZ);
		m[11] = -1;
		m[14] = far >= Double.POSITIVE_INFINITY ? (float) (-2 * near) : (float) (-2 * near * far / deltaZ);
		return m;
	}

	/**
	 * Returns an orthographic projection matrix.
	 * 
	 * @param left
	 *            coordinate for left vertical clipping plane
	 * @param right
	 *            coordinate for right vertical clipping plane
	 * @param top
	 *            coordinate for top horizontal clipping plane
	 * @param bottom
	 *            coordinate for bottom horizontal clipping plane
	 * @param near
	 *            near plane
	 * @param far
	 *            far plane
	 * @param m
	 *            matrix to be set to orthographic matrix or null to create new
	 *            matrix
	 * @return column-major orthographic projection matrix
	 */
	public static float[] ortho(float left, float right, float bottom, float top, float near, float far, float[] m) {
		if (m != null) {
			Arrays.fill(m, 0);
		} else {
			m = new float[16];
		}

		float dx = right - left;
		float dy = top - bottom;
		float dz = far - near;
		float tx = -1.0f * (right + left) / dx;
		float ty = -1.0f * (top + bottom) / dy;
		float tz = -1.0f * (far + near) / dz;

		m[0] = 2.0f / dx;
		m[5] = 2.0f / dy;
		m[10] = -2.0f / dz;
		m[12] = tx;
		m[13] = ty;
		m[14] = tz;
		m[15] = 1;
		return m;
	}
}
