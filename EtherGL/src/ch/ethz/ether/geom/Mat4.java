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
package ch.ethz.ether.geom;

import java.util.Arrays;

/**
 * Basic matrix utilities for dealing with OpenGL 4x4 matrices.
 *
 * @author radar
 */
public final class Mat4 {
    public final float[] m;

    public Mat4() {
        m = new float[16];
    }

    public Mat4(float[] m) {
        this.m = m;
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
        m[0] = m[5] = m[10] = m[15] = 1;
    }

    /**
     * Assign other matrix to this matrix.
     *
     * @param mat matrix to be assigned
     */
    public void assign(Mat4 mat) {
        System.arraycopy(mat.m, 0, m, 0, 16);
    }


    /**
     * Copy this matrix into new one.
     *
     * @return the copy
     */
    public Mat4 copy() {
        return new Mat4(Arrays.copyOf(m, 16));
    }


    /**
     * Post-multiply this matrix this = this * mat;
     *
     * @param mat the post matrix
     */
    public void multiply(final Mat4 mat) {
        for (int i = 0; i < 4; i++) {
            float ai0 = m[i];
            float ai1 = m[i + 4];
            float ai2 = m[i + 8];
            float ai3 = m[i + 12];
            m[i] = ai0 * mat.m[0] + ai1 * mat.m[1] + ai2 * mat.m[2] + ai3 * mat.m[3];
            m[i + 4] = ai0 * mat.m[4] + ai1 * mat.m[5] + ai2 * mat.m[6] + ai3 * mat.m[7];
            m[i + 8] = ai0 * mat.m[8] + ai1 * mat.m[9] + ai2 * mat.m[10] + ai3 * mat.m[11];
            m[i + 12] = ai0 * mat.m[12] + ai1 * mat.m[13] + ai2 * mat.m[14] + ai3 * mat.m[15];
        }
    }


    /**
     * Multiplies matrix m with translation matrix t. m = m * t
     *
     * @param tx x translation
     * @param ty y translation
     * @param tz z translation
     */
    public void translate(float tx, float ty, float tz) {
        final Mat4 t = identityMatrix();
        t.m[12] = tx;
        t.m[13] = ty;
        t.m[14] = tz;
        multiply(t);
    }

    public void translate(Vec3 t) {
        translate(t.x, t.y, t.z);
    }


    /**
     * Multiplies matrix m with rotation matrix r. m = m * r
     *
     * @param angle rotation angle in degrees
     * @param x     rotation axis x
     * @param y     rotation axis y
     * @param z     rotation axis z
     */
    public void rotate(float angle, float x, float y, float z) {
        float l = (float) Math.sqrt(x * x + y * y + z * z);
        if (l != 0 && l != 1) {
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

        final Mat4 r = identityMatrix();
        r.m[0] = x * x * ic + c;
        r.m[1] = xy * ic + zs;
        r.m[2] = xz * ic - ys;
        r.m[4] = xy * ic - zs;
        r.m[5] = y * y * ic + c;
        r.m[6] = yz * ic + xs;
        r.m[8] = xz * ic + ys;
        r.m[9] = yz * ic - xs;
        r.m[10] = z * z * ic + c;

        multiply(r);
    }

    public void rotate(float angle, Vec3 axis) {
        rotate(angle, axis.x, axis.y, axis.z);
    }

    /**
     * Multiplies matrix m with scale matrix s. m = m * s
     *
     * @param sx scale x factor
     * @param sy scale y factor
     * @param sz scale z factor
     */
    public void scale(float sx, float sy, float sz) {
        m[0] *= sx;
        m[5] *= sy;
        m[10] *= sz;
    }

    public void scale(Vec3 s) {
        scale(s.x, s.y, s.z);
    }

    /**
     * Set perspective projection. Supports far plane at infinity.
     *
     * @param fovy   field of view (degrees)
     * @param aspect aspect ratio
     * @param near   near plane
     * @param far    far plane (set to Float.POSITIVE_INFINITY for far plane at
     *               infinity)
     */
    public void perspective(float fovy, float aspect, float near, float far) {
        double radians = fovy / 2 * Math.PI / 180;
        double sine = Math.sin(radians);
        float deltaZ = far - near;

        if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
            throw new IllegalArgumentException("illegal arguments (fovy=" + fovy + " aspect=" + aspect + " near=" + near + " far=" + far);
        }

        double cotangent = (float) (Math.cos(radians) / sine);

        zero();
        m[0] = (float) (cotangent / aspect);
        m[5] = (float) cotangent;
        m[10] = ((far >= Double.POSITIVE_INFINITY) ? -1 : (-(far + near) / deltaZ));
        m[11] = -1;
        m[14] = ((far >= Double.POSITIVE_INFINITY) ? (-2 * near) : (-2 * near * far / deltaZ));
    }


    /**
     * Returns an orthographic projection matrix.
     *
     * @param left   coordinate for left vertical clipping plane
     * @param right  coordinate for right vertical clipping plane
     * @param top    coordinate for top horizontal clipping plane
     * @param bottom coordinate for bottom horizontal clipping plane
     * @param near   near plane
     * @param far    far plane
     */
    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        float dx = right - left;
        float dy = top - bottom;
        float dz = far - near;
        float tx = -1.0f * (right + left) / dx;
        float ty = -1.0f * (top + bottom) / dy;
        float tz = -1.0f * (far + near) / dz;

        zero();
        m[0] = 2.0f / dx;
        m[5] = 2.0f / dy;
        m[10] = -2.0f / dz;
        m[12] = tx;
        m[13] = ty;
        m[14] = tz;
        m[15] = 1;
    }


    /**
     * Transform vector result = m * vec.
     *
     * @param vec the vector to be transformed
     * @return the transformed vector
     */
    public Vec4 transform(Vec4 vec) {
        float x = vec.x * m[0] + vec.y * m[4 + 0] + vec.z * m[8 + 0] + vec.w * m[12 + 0];
        float y = vec.x * m[1] + vec.y * m[4 + 1] + vec.z * m[8 + 1] + vec.w * m[12 + 1];
        float z = vec.x * m[2] + vec.y * m[4 + 2] + vec.z * m[8 + 2] + vec.w * m[12 + 2];
        float w = vec.x * m[3] + vec.y * m[4 + 3] + vec.z * m[8 + 3] + vec.w * m[12 + 3];
        return new Vec4(x, y, z, w);
    }


    /**
     * Transform vector result = m * vec (divided by w).
     *
     * @param vec the vector to be transformed
     * @return the transformed vector
     */
    public Vec3 transform(Vec3 vec) {
        float x = vec.x * m[0] + vec.y * m[4 + 0] + vec.z * m[8 + 0] + m[12 + 0];
        float y = vec.x * m[1] + vec.y * m[4 + 1] + vec.z * m[8 + 1] + m[12 + 1];
        float z = vec.x * m[2] + vec.y * m[4 + 2] + vec.z * m[8 + 2] + m[12 + 2];
        float w = vec.x * m[3] + vec.y * m[4 + 3] + vec.z * m[8 + 3] + m[12 + 3];
        return new Vec3(x / w, y / w, z / w);
    }


    /**
     * Transform a float array of xyz vectors.
     *
     * @param xyz    the input array of vectors to be transformed
     * @param result the array where to store the transformed vectors or NULL to create a new array
     * @return the transformed result
     */
    public float[] transform(float[] xyz, float[] result) {
        if (xyz == null)
            return null;
        if (result == null)
            result = new float[xyz.length];
        for (int i = 0; i < xyz.length; i += 3) {
            float x = xyz[i] * m[0] + xyz[i + 1] * m[4 + 0] + xyz[i + 2] * m[8 + 0] + m[12 + 0];
            float y = xyz[i] * m[1] + xyz[i + 1] * m[4 + 1] + xyz[i + 2] * m[8 + 1] + m[12 + 1];
            float z = xyz[i] * m[2] + xyz[i + 1] * m[4 + 2] + xyz[i + 2] * m[8 + 2] + m[12 + 2];
            float w = xyz[i] * m[3] + xyz[i + 1] * m[4 + 3] + xyz[i + 2] * m[8 + 3] + m[12 + 3];
            result[i] = x / w;
            result[i + 1] = y / w;
            result[i + 2] = z / w;
        }
        return result;
    }

    /**
     * Transform a float array of xyz vectors.
     *
     * @param xyz the input array of vectors to be transformed
     * @return new array containing the transformed result
     */
    public float[] transform(float[] xyz) {
        return transform(xyz, null);
    }

    /**
     * Get transposed matrix.
     * @return the transposed matrix
     */
    // FIXME: verify
    public Mat4 transposed() {
        Mat4 result = new Mat4();
        result.m[0] = m[0];
        result.m[1] = m[4];
        result.m[2] = m[8];
        result.m[3] = m[12];
        result.m[4] = m[1];
        result.m[5] = m[5];
        result.m[6] = m[9];
        result.m[7] = m[13];
        result.m[8] = m[2];
        result.m[9] = m[6];
        result.m[10] = m[10];
        result.m[11] = m[15];
        result.m[12] = m[3];
        result.m[13] = m[7];
        result.m[14] = m[11];
        result.m[15] = m[15];
        return result;
    }

    /**
     * Get inverse matrix.
     *
     * @return the inverse or null if a is singular
     */
    public Mat4 inverse() {
        final float[][] temp = new float[4][4];

        for (int i = 0; i < 4; i++) {
            System.arraycopy(m, i * 4, temp[i], 0, 4);
        }

        Mat4 inv = identityMatrix();

        for (int i = 0; i < 4; i++) {
            // look for largest element in column
            int swap = i;
            for (int j = i + 1; j < 4; j++) {
                if (Math.abs(temp[j][i]) > Math.abs(temp[i][i])) {
                    swap = j;
                }
            }

            if (swap != i) {
                // swap rows
                for (int k = 0; k < 4; k++) {
                    float t = temp[i][k];
                    temp[i][k] = temp[swap][k];
                    temp[swap][k] = t;

                    t = inv.m[i * 4 + k];
                    inv.m[i * 4 + k] = inv.m[swap * 4 + k];
                    inv.m[swap * 4 + k] = t;
                }
            }

            if (temp[i][i] == 0) {
                // singular input
                return null;
            }

            float t = temp[i][i];
            for (int k = 0; k < 4; k++) {
                temp[i][k] /= t;
                inv.m[i * 4 + k] /= t;
            }
            for (int j = 0; j < 4; j++) {
                if (j != i) {
                    t = temp[j][i];
                    for (int k = 0; k < 4; k++) {
                        temp[j][k] -= temp[i][k] * t;
                        inv.m[j * 4 + k] -= inv.m[i * 4 + k] * t;
                    }
                }
            }
        }
        return inv;
    }


    /**
     * Create new identity matrix
     *
     * @return the new identity matrix
     */
    public static Mat4 identityMatrix() {
        Mat4 mat = new Mat4();
        mat.m[0] = mat.m[5] = mat.m[10] = mat.m[15] = 1;
        return mat;
    }


    /**
     * Multiplies two matrices result = a * b.
     *
     * @param a 4x4 matrix in column-major order
     * @param b 4x4 matrix in column-major order
     * @return multiplied column-major matrix
     */
    public static Mat4 product(Mat4 a, Mat4 b) {
        Mat4 result = new Mat4();
        for (int i = 0; i < 4; i++) {
            float ai0 = a.m[i];
            float ai1 = a.m[i + 4];
            float ai2 = a.m[i + 8];
            float ai3 = a.m[i + 12];
            result.m[i] = ai0 * b.m[0] + ai1 * b.m[1] + ai2 * b.m[2] + ai3 * b.m[3];
            result.m[i + 4] = ai0 * b.m[4] + ai1 * b.m[5] + ai2 * b.m[6] + ai3 * b.m[7];
            result.m[i + 8] = ai0 * b.m[8] + ai1 * b.m[9] + ai2 * b.m[10] + ai3 * b.m[11];
            result.m[i + 12] = ai0 * b.m[12] + ai1 * b.m[13] + ai2 * b.m[14] + ai3 * b.m[15];
        }
        return result;
    }
}
