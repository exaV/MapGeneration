/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
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

package ch.fhnw.util.math;

import java.util.Arrays;

/**
 * 3x3 matrix for dealing with OpenGL 3x3 matrices (column major). Mat3 is mutable.
 *
 * @author radar
 */
public final class Mat3 {
	public static final int M00 = 0;
	public static final int M10 = 1;
	public static final int M20 = 2;
	public static final int M01 = 3;
	public static final int M11 = 4;
	public static final int M21 = 5;
	public static final int M02 = 6;
	public static final int M12 = 7;
	public static final int M22 = 8;

	public final float[] m;

	public Mat3() {
		m = new float[9];
	}

	public Mat3(float[] m) {
		this.m = m;
	}

	public Mat3(Mat4 m) {
		this();
		this.m[M00] = m.m[Mat4.M00];
		this.m[M10] = m.m[Mat4.M10];
		this.m[M20] = m.m[Mat4.M20];
		this.m[M01] = m.m[Mat4.M01];
		this.m[M11] = m.m[Mat4.M11];
		this.m[M21] = m.m[Mat4.M21];
		this.m[M02] = m.m[Mat4.M02];
		this.m[M12] = m.m[Mat4.M12];
		this.m[M22] = m.m[Mat4.M22];
	}

	/**
	 * Set matrix to zero.
	 */
	public void zero() {
		Arrays.fill(m, 0);
	}

	/**
	 * Set matrix to identity matrix.
	 */
	public void identity() {
		zero();
		m[M00] = m[M11] = m[M22] = 1;
	}

	/**
	 * Assign other matrix to this matrix.
	 *
	 * @param mat
	 *            matrix to be assigned
	 */
	public void assign(Mat3 mat) {
		System.arraycopy(mat.m, 0, m, 0, 9);
	}

	/**
	 * Copy this matrix into new one.
	 *
	 * @return the copy
	 */
	public Mat3 copy() {
		return new Mat3(Arrays.copyOf(m, 9));
	}

	/**
	 * Post-multiply this matrix this = this * mat;
	 *
	 * @param mat
	 *            the second factor of the matrix product
	 */
	public Mat3 postMultiply(final Mat3 mat) {
		float v00 = m[M00] * mat.m[M00] + m[M01] * mat.m[M10] + m[M02] * mat.m[M20];
		float v01 = m[M00] * mat.m[M01] + m[M01] * mat.m[M11] + m[M02] * mat.m[M21];
		float v02 = m[M00] * mat.m[M02] + m[M01] * mat.m[M12] + m[M02] * mat.m[M22];

		float v10 = m[M10] * mat.m[M00] + m[M11] * mat.m[M10] + m[M12] * mat.m[M20];
		float v11 = m[M10] * mat.m[M01] + m[M11] * mat.m[M11] + m[M12] * mat.m[M21];
		float v12 = m[M10] * mat.m[M02] + m[M11] * mat.m[M12] + m[M12] * mat.m[M22];

		float v20 = m[M20] * mat.m[M00] + m[M21] * mat.m[M10] + m[M22] * mat.m[M20];
		float v21 = m[M20] * mat.m[M01] + m[M21] * mat.m[M11] + m[M22] * mat.m[M21];
		float v22 = m[M20] * mat.m[M02] + m[M21] * mat.m[M12] + m[M22] * mat.m[M22];

		m[M00] = v00;
		m[M10] = v10;
		m[M20] = v20;
		m[M01] = v01;
		m[M11] = v11;
		m[M21] = v21;
		m[M02] = v02;
		m[M12] = v12;
		m[M22] = v22;
		return this;
	}

	/**
	 * Pre-multiply this matrix this = mat * this;
	 *
	 * @param mat
	 *            the first factor of the matrix product
	 */
	public Mat3 preMultiply(final Mat3 mat) {
		float v00 = mat.m[M00] * m[M00] + mat.m[M01] * m[M10] + mat.m[M02] * m[M20];
		float v01 = mat.m[M00] * m[M01] + mat.m[M01] * m[M11] + mat.m[M02] * m[M21];
		float v02 = mat.m[M00] * m[M02] + mat.m[M01] * m[M12] + mat.m[M02] * m[M22];

		float v10 = mat.m[M10] * m[M00] + mat.m[M11] * m[M10] + mat.m[M12] * m[M20];
		float v11 = mat.m[M10] * m[M01] + mat.m[M11] * m[M11] + mat.m[M12] * m[M21];
		float v12 = mat.m[M10] * m[M02] + mat.m[M11] * m[M12] + mat.m[M12] * m[M22];

		float v20 = mat.m[M20] * m[M00] + mat.m[M21] * m[M10] + mat.m[M22] * m[M20];
		float v21 = mat.m[M20] * m[M01] + mat.m[M21] * m[M11] + mat.m[M22] * m[M21];
		float v22 = mat.m[M20] * m[M02] + mat.m[M21] * m[M12] + mat.m[M22] * m[M22];

		m[M00] = v00;
		m[M10] = v10;
		m[M20] = v20;
		m[M01] = v01;
		m[M11] = v11;
		m[M21] = v21;
		m[M02] = v02;
		m[M12] = v12;
		m[M22] = v22;		
		return this;
	}

	/**
	 * Pre-multiplies matrix m with translation matrix t (m = t * m)
	 *
	 * @param tx
	 *            x translation
	 * @param ty
	 *            y translation
	 */
	public Mat3 translate(float tx, float ty) {
		final Mat3 t = identityMatrix();
		t.m[M02] = tx;
		t.m[M12] = ty;
		return preMultiply(t);
	}

	public Mat3 translate(Vec3 t) {
		return translate(t.x, t.y);
	}

	/**
	 * Pre-multiplies matrix m with rotation matrix r (m = r * m).
	 *
	 * @param angle
	 *            rotation angle in degrees
	 */
	public Mat3 rotate(float angle) {
		float radians = angle * MathUtil.DEGREES_TO_RADIANS;
		float c = (float) Math.cos(radians);
		float s = (float) Math.sin(radians);

		final Mat3 r = new Mat3(new float[] { c, -s, 0, s, c, 0, 0, 0, 1 });

		return preMultiply(r);
	}

	/**
	 * Multiplies matrix m with scale matrix s (m = s * m = m * s).
	 *
	 * @param sx
	 *            scale x factor
	 * @param sy
	 *            scale y factor
	 */
	public Mat3 scale(float sx, float sy) {
		m[M00] *= sx;
		m[M11] *= sy;
		m[M02] *= sx;
		m[M12] *= sy;
		return this;
	}

	public Mat3 scale(Vec3 s) {
		return scale(s.x, s.y);
	}

	/**
	 * Transform vector result = m * vec.
	 *
	 * @param vec
	 *            the vector to be transformed
	 * @return the transformed vector
	 */
	public Vec3 transform(Vec3 vec) {
		float x = vec.x * m[M00] + vec.y * m[M01] + vec.z * m[M02];
		float y = vec.x * m[M10] + vec.y * m[M11] + vec.z * m[M12];
		float z = vec.x * m[M20] + vec.y * m[M21] + vec.z * m[M22];
		return new Vec3(x, y, z);
	}

	/**
	 * Transform a float array of xyz vectors.
	 *
	 * @param xyz
	 *            the input array of vectors to be transformed
	 * @param result
	 *            the array where to store the transformed vectors or NULL to create a new array
	 * @return the transformed result
	 */
	public float[] transform(float[] xyz, float[] result) {
		if (xyz == null)
			return null;
		if (result == null)
			result = new float[xyz.length];
		for (int i = 0; i < xyz.length; i += 3) {
			float x = xyz[i] * m[M00] + xyz[i + 1] * m[M01] + xyz[i + 2] * m[M02];
			float y = xyz[i] * m[M10] + xyz[i + 1] * m[M11] + xyz[i + 2] * m[M12];
			float z = xyz[i] * m[M20] + xyz[i + 1] * m[M21] + xyz[i + 2] * m[M22];
			result[i] = x;
			result[i + 1] = y;
			result[i + 2] = z;
		}
		return result;
	}

	/**
	 * Transform a float array of xyz vectors.
	 *
	 * @param xyz
	 *            the input array of vectors to be transformed
	 * @return new array containing the transformed result
	 */
	public float[] transform(float[] xyz) {
		return transform(xyz, null);
	}

	/**
	 * Get transpose matrix.
	 *
	 * @return the transpose matrix
	 */
	public Mat3 transpose() {
		Mat3 result = new Mat3();
		result.m[M00] = m[M00];
		result.m[M10] = m[M01];
		result.m[M20] = m[M02];

		result.m[M01] = m[M10];
		result.m[M11] = m[M11];
		result.m[M21] = m[M12];

		result.m[M02] = m[M20];
		result.m[M12] = m[M21];
		result.m[M22] = m[M22];
		return result;
	}

	/**
	 * Get the determinant.
	 * 
	 * @return the determinant
	 */
	public float determinant() {
		return m[M00] * m[M11] * m[M22] + m[M01] * m[M12] * m[M20] + m[M02] * m[M10] * m[M21] - m[M00] * m[M12] * m[M21] - m[M01] * m[M10] * m[M22] - m[M02]
				* m[M11] * m[M20];
	}

	/**
	 * Get inverse matrix.
	 *
	 * @return the inverse or null if a is singular
	 */
	public Mat3 inverse() {
		float d = determinant();
		if (d == 0)
			return null;

		float t00 = (m[M11] * m[M22] - m[M21] * m[M12]) / d;
		float t10 = (m[M20] * m[M12] - m[M10] * m[M22]) / d;
		float t20 = (m[M10] * m[M21] - m[M20] * m[M11]) / d;
		float t01 = (m[M21] * m[M02] - m[M01] * m[M22]) / d;
		float t11 = (m[M00] * m[M22] - m[M20] * m[M02]) / d;
		float t21 = (m[M20] * m[M01] - m[M00] * m[M21]) / d;
		float t02 = (m[M01] * m[M12] - m[M11] * m[M02]) / d;
		float t12 = (m[M10] * m[M02] - m[M00] * m[M12]) / d;
		float t22 = (m[M00] * m[M11] - m[M10] * m[M01]) / d;

		return new Mat3(new float[] { t00, t10, t20, t01, t11, t21, t02, t12, t22 });
	}

	/**
	 * Create new identity matrix.
	 *
	 * @return the new identity matrix
	 */
	public static Mat3 identityMatrix() {
		Mat3 result = new Mat3();
		result.m[M00] = result.m[M11] = result.m[M22] = 1;
		return result;
	}

	/**
	 * Multiplies two matrices result = a * b.
	 *
	 * @param a
	 *            3x3 matrix
	 * @param b
	 *            3x3 matrix
	 * @return multiplied column-major matrix
	 */
	public static Mat3 product(Mat3 a, Mat3 b) {
		Mat3 result = new Mat3();
		float v00 = a.m[M00] * b.m[M00] + a.m[M01] * b.m[M10] + a.m[M02] * b.m[M20];
		float v01 = a.m[M00] * b.m[M01] + a.m[M01] * b.m[M11] + a.m[M02] * b.m[M21];
		float v02 = a.m[M00] * b.m[M02] + a.m[M01] * b.m[M12] + a.m[M02] * b.m[M22];

		float v10 = a.m[M10] * b.m[M00] + a.m[M11] * b.m[M10] + a.m[M12] * b.m[M20];
		float v11 = a.m[M10] * b.m[M01] + a.m[M11] * b.m[M11] + a.m[M12] * b.m[M21];
		float v12 = a.m[M10] * b.m[M02] + a.m[M11] * b.m[M12] + a.m[M12] * b.m[M22];

		float v20 = a.m[M20] * b.m[M00] + a.m[M21] * b.m[M10] + a.m[M22] * b.m[M20];
		float v21 = a.m[M20] * b.m[M01] + a.m[M21] * b.m[M11] + a.m[M22] * b.m[M21];
		float v22 = a.m[M20] * b.m[M02] + a.m[M21] * b.m[M12] + a.m[M22] * b.m[M22];

		result.m[M00] = v00;
		result.m[M10] = v10;
		result.m[M20] = v20;
		result.m[M01] = v01;
		result.m[M11] = v11;
		result.m[M21] = v21;
		result.m[M02] = v02;
		result.m[M12] = v12;
		result.m[M22] = v22;

		return result;
	}

	@Override
	public String toString() {
		//@formatter:off
		return String.format("[% .2f,% .2f,% .2f\n % .2f,% .2f,% .2f\n % .2f,% .2f,% .2f\n\n",
							 m[M00], m[M01], m[M02],
							 m[M10], m[M11], m[M12],
							 m[M20], m[M21], m[M22]);
		//@formatter:on
	}
}
