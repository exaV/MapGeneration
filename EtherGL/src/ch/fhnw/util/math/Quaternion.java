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

/* 
 * Largely taken from libgdx with some adaptations.
 * http://libgdx.badlogicgames.com
 */
package ch.fhnw.util.math;

import ch.fhnw.util.Pair;

public class Quaternion extends Vec4 {
	public static final Quaternion IDENTITY = new Quaternion(0, 0, 0, 1);

	public Quaternion(float x, float y, float z) {
		super(x, y, z);
	}

	public Quaternion(float x, float y, float z, float w) {
		super(x, y, z, w);
	}

	/**
	 * Computes the quaternion to the given euler angles in degrees.
	 * 
	 * @param yaw
	 *            the rotation around the y axis in degrees
	 * @param pitch
	 *            the rotation around the x axis in degrees
	 * @param roll
	 *            the rotation around the z axis degrees
	 * @return this quaternion
	 */
	public static Quaternion fromEulerAngles(float yaw, float pitch, float roll) {
		return fromEulerAnglesRad(yaw * MathUtil.DEGREES_TO_RADIANS, pitch * MathUtil.DEGREES_TO_RADIANS, roll * MathUtil.DEGREES_TO_RADIANS);
	}

	/**
	 * Computes the quaternion to the given euler angles in radians.
	 * 
	 * @param yaw
	 *            the rotation around the y axis in radians
	 * @param pitch
	 *            the rotation around the x axis in radians
	 * @param roll
	 *            the rotation around the z axis in radians
	 * @return this quaternion
	 */
	public static Quaternion fromEulerAnglesRad(float yaw, float pitch, float roll) {
		final float hr = roll * 0.5f;
		final float shr = (float) Math.sin(hr);
		final float chr = (float) Math.cos(hr);
		final float hp = pitch * 0.5f;
		final float shp = (float) Math.sin(hp);
		final float chp = (float) Math.cos(hp);
		final float hy = yaw * 0.5f;
		final float shy = (float) Math.sin(hy);
		final float chy = (float) Math.cos(hy);
		final float chy_shp = chy * shp;
		final float shy_chp = shy * chp;
		final float chy_chp = chy * chp;
		final float shy_shp = shy * shp;

		return new Quaternion((chy_shp * chr) + (shy_chp * shr), // cos(yaw/2) * sin(pitch/2) * cos(roll/2) + sin(yaw/2)
																	// * cos(pitch/2) * sin(roll/2)
				(shy_chp * chr) - (chy_shp * shr), // sin(yaw/2) * cos(pitch/2) * cos(roll/2) - cos(yaw/2) *
													// sin(pitch/2) * sin(roll/2)
				(chy_chp * shr) - (shy_shp * chr), // cos(yaw/2) * cos(pitch/2) * sin(roll/2) - sin(yaw/2) *
													// sin(pitch/2) * cos(roll/2)
				(chy_chp * chr) + (shy_shp * shr)); // cos(yaw/2) * cos(pitch/2) * cos(roll/2) + sin(yaw/2) *
													// sin(pitch/2) * sin(roll/2)
	}

	/**
	 * Get the pole of the gimbal lock, if any.
	 * 
	 * @return positive (+1) for north pole, negative (-1) for south pole, zero (0) when no gimbal lock
	 */
	public int getGimbalPole() {
		final float t = y * x + z * w;
		return t > 0.499f ? 1 : (t < -0.499f ? -1 : 0);
	}

	/**
	 * Get the roll euler angle in radians, which is the rotation around the z axis. Requires that this quaternion is
	 * normalized.
	 * 
	 * @return the rotation around the z axis in radians (between -PI and +PI)
	 */
	public float getRollRad() {
		final int pole = getGimbalPole();
		return (float) (pole == 0 ? Math.atan2(2f * (w * z + y * x), 1f - 2f * (x * x + z * z)) : pole * 2f * Math.atan2(y, w));
	}

	/**
	 * Get the roll euler angle in degrees, which is the rotation around the z axis. Requires that this quaternion is
	 * normalized.
	 * 
	 * @return the rotation around the z axis in degrees (between -180 and +180)
	 */
	public float getRoll() {
		return getRollRad() * MathUtil.RADIANS_TO_DEGREES;
	}

	/**
	 * Get the pitch euler angle in radians, which is the rotation around the x axis. Requires that this quaternion is
	 * normalized.
	 * 
	 * @return the rotation around the x axis in radians (between -(PI/2) and +(PI/2))
	 */
	public float getPitchRad() {
		final int pole = getGimbalPole();
		return pole == 0 ? (float) Math.asin(MathUtil.clamp(2f * (w * x - z * y), -1f, 1f)) : pole * MathUtil.PI * 0.5f;
	}

	/**
	 * Get the pitch euler angle in degrees, which is the rotation around the x axis. Requires that this quaternion is
	 * normalized.
	 * 
	 * @return the rotation around the x axis in degrees (between -90 and +90)
	 */
	public float getPitch() {
		return getPitchRad() * MathUtil.RADIANS_TO_DEGREES;
	}

	/**
	 * Get the yaw euler angle in radians, which is the rotation around the y axis. Requires that this quaternion is
	 * normalized.
	 * 
	 * @return the rotation around the y axis in radians (between -PI and +PI)
	 */
	public float getYawRad() {
		return (float) (getGimbalPole() == 0 ? Math.atan2(2f * (y * w + x * z), 1f - 2f * (y * y + x * x)) : 0f);
	}

	/**
	 * Get the yaw euler angle in degrees, which is the rotation around the y axis. Requires that this quaternion is
	 * normalized.
	 * 
	 * @return the rotation around the y axis in degrees (between -180 and +180)
	 */
	public float getYaw() {
		return getYawRad() * MathUtil.RADIANS_TO_DEGREES;
	}

	/**
	 * Normalizes this quaternion to unit length
	 * 
	 * @return the normalized quaternion.
	 */
	@Override
	public Quaternion normalize() {
		float l = length();
		if (MathUtil.isZero(l) || l == 1)
			return this;
		return new Quaternion(x / l, y / l, z / l, w / l);
	}

	/**
	 * Conjugate the quaternion.
	 * 
	 * @return This conjugate quaternion.
	 */
	public Quaternion conjugate() {
		return new Quaternion(-x, -y, -z);
	}

	// TODO : this would better fit into the Vec3 class
	/**
	 * Transforms the given vector using this quaternion
	 * 
	 * @param v
	 *            Vector to transform
	 */
	public Vec3 transform(Vec3 v) {
		Quaternion tmp2 = this;
		tmp2 = tmp2.conjugate();
		tmp2 = tmp2.mulLeft(new Quaternion(v.x, v.y, v.z, 0)).mulLeft(this);

		return new Vec3(tmp2);
	}

	/**
	 * Multiplies this quaternion with another one in the form of result = this * other
	 * 
	 * @param other
	 *            Quaternion to multiply with
	 * @return Result quaternion
	 */
	public Quaternion mul(final Quaternion other) {
		return new Quaternion(this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y, this.w * other.y + this.y * other.w + this.z * other.x
				- this.x * other.z, this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x, this.w * other.w - this.x * other.x - this.y
				* other.y - this.z * other.z);
	}

	/**
	 * Multiplies this quaternion with another one in the form of result = this * other
	 * 
	 * @param x
	 *            the x component of the other quaternion to multiply with
	 * @param y
	 *            the y component of the other quaternion to multiply with
	 * @param z
	 *            the z component of the other quaternion to multiply with
	 * @param w
	 *            the w component of the other quaternion to multiply with
	 * @return Result quaternion
	 */
	public Quaternion mul(final float x, final float y, final float z, final float w) {
		return new Quaternion(this.w * x + this.x * w + this.y * z - this.z * y, this.w * y + this.y * w + this.z * x - this.x * z, this.w * z + this.z * w
				+ this.x * y - this.y * x, this.w * w - this.x * x - this.y * y - this.z * z);
	}

	/**
	 * Multiplies this quaternion with another one in the form of result = other * this
	 * 
	 * @param other
	 *            Quaternion to multiply with
	 * @return Result quaternion
	 */
	public Quaternion mulLeft(Quaternion other) {
		return new Quaternion(other.w * this.x + other.x * this.w + other.y * this.z - other.z * y, other.w * this.y + other.y * this.w + other.z * this.x
				- other.x * z, other.w * this.z + other.z * this.w + other.x * this.y - other.y * x, other.w * this.w - other.x * this.x - other.y * this.y
				- other.z * z);
	}

	/**
	 * Multiplies this quaternion with another one in the form of result = other * this
	 * 
	 * @param x
	 *            the x component of the other quaternion to multiply with
	 * @param y
	 *            the y component of the other quaternion to multiply with
	 * @param z
	 *            the z component of the other quaternion to multiply with
	 * @param w
	 *            the w component of the other quaternion to multiply with
	 * @return Result quaternion
	 */
	public Quaternion mulLeft(final float x, final float y, final float z, final float w) {
		return new Quaternion(w * this.x + x * this.w + y * this.z - z * y, w * this.y + y * this.w + z * this.x - x * z, w * this.z + z * this.w + x * this.y
				- y * x, w * this.w - x * this.x - y * this.y - z * z);
	}

	/** Add the x,y,z,w components of the passed in quaternion to the ones of this quaternion */
	public Quaternion add(Quaternion quaternion) {
		return new Quaternion(this.x + quaternion.x, this.y + quaternion.y, this.z + quaternion.z, this.w + quaternion.w);
	}

	/** Add the x,y,z,w components of the passed in quaternion to the ones of this quaternion */
	public Quaternion add(float qx, float qy, float qz, float qw) {
		return new Quaternion(this.x + qx, this.y + qy, this.z + qz, this.w + qw);
	}

	// TODO : the matrix4 set(quaternion) does not set the last row+col of the matrix to 0,0,0,1 so... that's why there
	// is this
	// method
	/**
	 * Fills a 4x4 matrix with the rotation matrix represented by this quaternion.
	 * 
	 * @param matrix
	 *            Matrix to fill
	 */
	public void toMatrix(final float[] matrix) {
		final float xx = x * x;
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yy = y * y;
		final float yz = y * z;
		final float yw = y * w;
		final float zz = z * z;
		final float zw = z * w;
		// Set matrix from quaternion
		matrix[Mat4.M00] = 1 - 2 * (yy + zz);
		matrix[Mat4.M01] = 2 * (xy - zw);
		matrix[Mat4.M02] = 2 * (xz + yw);
		matrix[Mat4.M03] = 0;
		matrix[Mat4.M10] = 2 * (xy + zw);
		matrix[Mat4.M11] = 1 - 2 * (xx + zz);
		matrix[Mat4.M12] = 2 * (yz - xw);
		matrix[Mat4.M13] = 0;
		matrix[Mat4.M20] = 2 * (xz - yw);
		matrix[Mat4.M21] = 2 * (yz + xw);
		matrix[Mat4.M22] = 1 - 2 * (xx + yy);
		matrix[Mat4.M23] = 0;
		matrix[Mat4.M30] = 0;
		matrix[Mat4.M31] = 0;
		matrix[Mat4.M32] = 0;
		matrix[Mat4.M33] = 1;
	}

	/** @return If this quaternion is an identity Quaternion */
	public boolean isIdentity() {
		return MathUtil.isZero(x) && MathUtil.isZero(y) && MathUtil.isZero(z) && MathUtil.isEqual(w, 1f);
	}

	/** @return If this quaternion is an identity Quaternion */
	public boolean isIdentity(final float tolerance) {
		return MathUtil.isZero(x, tolerance) && MathUtil.isZero(y, tolerance) && MathUtil.isZero(z, tolerance) && MathUtil.isEqual(w, 1f, tolerance);
	}

	/**
	 * Computes the quaternion components from the given axis and angle around that axis.
	 * 
	 * @param axis
	 *            The axis
	 * @param degrees
	 *            The angle in degrees
	 * @return This quaternion for chaining.
	 */
	public static Quaternion fromAxis(Vec3 axis, float degrees) {
		return fromAxisRad(axis, degrees * MathUtil.DEGREES_TO_RADIANS);
	}

	/**
	 * Computes the quaternion components from the given axis and angle around that axis.
	 * 
	 * @param axis
	 *            The axis
	 * @param radians
	 *            The angle in radians
	 * @return This quaternion for chaining.
	 */
	public static Quaternion fromAxisRad(Vec3 axis, float radians) {
		float d = axis.length();
		if (d == 0f)
			return IDENTITY;
		d = 1f / d;
		float l_ang = radians;
		float l_sin = (float) Math.sin(l_ang / 2);
		float l_cos = (float) Math.cos(l_ang / 2);
		return new Quaternion(d * axis.x * l_sin, d * axis.y * l_sin, d * axis.z * l_sin, l_cos).normalize();
	}

	/** Computes the Quaternion from the given matrix, optionally removing any scaling. */
	public static Quaternion fromMatrix(Mat4 matrix, boolean normalize) {
		return fromAxes(new Vec3(matrix.m[Mat4.M00], matrix.m[Mat4.M01], matrix.m[Mat4.M02]), new Vec3(matrix.m[Mat4.M10], matrix.m[Mat4.M11],
				matrix.m[Mat4.M12]), new Vec3(matrix.m[Mat4.M20], matrix.m[Mat4.M21], matrix.m[Mat4.M22]), normalize);
	}

	/** Computes the Quaternion from the given rotation matrix, which must not contain scaling. */
	public static Quaternion fromMatrix(Mat4 matrix) {
		return fromMatrix(matrix, false);
	}

	/**
	 * Computes the Quaternion from the given x-, y- and z-axis which have to be orthonormal.
	 */
	public static Quaternion fromAxes(Vec3 vx, Vec3 vy, Vec3 vz) {
		return fromAxes(vx, vy, vz, false);
	}

	/**
	 * Computes the Quaternion from the given x-, y- and z-axis.
	 */
	public static Quaternion fromAxes(Vec3 vx, Vec3 vy, Vec3 vz, boolean normalize) {
		if (normalize) {
			vx = vx.normalize();
			vy = vy.normalize();
			vz = vz.normalize();
		}
		// the trace is the sum of the diagonal elements; see
		// http://mathworld.wolfram.com/MatrixTrace.html
		final float t = vx.x + vy.y + vz.z;

		float x;
		float y;
		float z;
		float w;

		// we protect the division by s by ensuring that s>=1
		if (t >= 0) { // |w| >= .5
			float s = (float) Math.sqrt(t + 1); // |s|>=1 ...
			w = 0.5f * s;
			s = 0.5f / s; // so this division isn't bad
			x = (vz.y - vy.z) * s;
			y = (vx.z - vz.x) * s;
			z = (vy.x - vx.y) * s;
		} else if ((vx.x > vy.y) && (vx.x > vz.z)) {
			float s = (float) Math.sqrt(1.0 + vx.x - vy.y - vz.z); // |s|>=1
			x = s * 0.5f; // |x| >= .5
			s = 0.5f / s;
			y = (vy.x + vx.y) * s;
			z = (vx.z + vz.x) * s;
			w = (vz.y - vy.z) * s;
		} else if (vy.y > vz.z) {
			float s = (float) Math.sqrt(1.0 + vy.y - vx.x - vz.z); // |s|>=1
			y = s * 0.5f; // |y| >= .5
			s = 0.5f / s;
			x = (vy.x + vx.y) * s;
			z = (vz.y + vy.z) * s;
			w = (vx.z - vz.x) * s;
		} else {
			float s = (float) Math.sqrt(1.0 + vz.z - vx.x - vy.y); // |s|>=1
			z = s * 0.5f; // |z| >= .5
			s = 0.5f / s;
			x = (vx.z + vz.x) * s;
			y = (vz.y + vy.z) * s;
			w = (vy.x - vx.y) * s;
		}

		return new Quaternion(x, y, z, w);
	}

	/**
	 * Set this quaternion to the rotation between two vectors.
	 * 
	 * @param v1
	 *            The base vector, which should be normalized.
	 * @param v2
	 *            The target vector, which should be normalized.
	 * @return This quaternion for chaining
	 */
	public static Quaternion fromCross(Vec3 v1, Vec3 v2) {
		final float dot = MathUtil.clamp(v1.dot(v2), -1f, 1f);
		final float angle = (float) Math.acos(dot);
		return fromAxisRad(Vec3.cross(v1, v2), angle);
	}

	/**
	 * Spherical linear interpolation between this quaternion and the other quaternion, based on the alpha value in the
	 * range [0,1]. Taken from. Taken from Bones framework for JPCT, see http://www.aptalkarga.com/bones/
	 * 
	 * @param end
	 *            the end quaternion
	 * @param alpha
	 *            alpha in the range [0,1]
	 * @return result
	 */
	public Quaternion slerp(Quaternion end, float alpha) {
		final float dot = dot(end);
		float absDot = dot < 0.f ? -dot : dot;

		float scale0 = 1 - alpha;
		float scale1 = alpha;

		if ((1 - absDot) > 0.1) {
			final double angle = Math.acos(absDot);
			final double invSinTheta = 1f / Math.sin(angle);

			scale0 = (float) (Math.sin((1 - alpha) * angle) * invSinTheta);
			scale1 = (float) (Math.sin((alpha * angle)) * invSinTheta);
		}

		if (dot < 0.f)
			scale1 = -scale1;

		return new Quaternion((scale0 * x) + (scale1 * end.x), (scale0 * y) + (scale1 * end.y), (scale0 * z) + (scale1 * end.z), (scale0 * w)
				+ (scale1 * end.w));
	}

	/**
	 * Spherical linearly interpolates multiple quaternions and will return the result. Will not destroy the data
	 * previously inside the elements of q. result = (q_1^w_1)*(q_2^w_2)* ... *(q_n^w_n) where w_i=1/n.
	 * 
	 * @param q
	 *            List of quaternions
	 * @return Result quaternion for.
	 */
	public Quaternion slerp(Quaternion[] q) {
		final float w = 1.0f / q.length;
		Quaternion result = q[0].exp(w);
		for (int i = 1; i < q.length; i++)
			result = result.mul(q[i]).exp(w);
		return result.normalize();
	}

	/**
	 * Spherical linearly interpolates multiple quaternions by the given weights and will return the result. Will not
	 * destroy the data previously inside the elements of q or w. result = (q_1^w_1)*(q_2^w_2)* ... *(q_n^w_n) where the
	 * sum of w_i is 1. Lists must be equal in length.
	 * 
	 * @param q
	 *            List of quaternions
	 * @param w
	 *            List of weights
	 * @return Result quaternion.
	 */
	public Quaternion slerp(Quaternion[] q, float[] w) {
		Quaternion result = q[0].exp(w[0]);
		for (int i = 1; i < q.length; i++)
			result = result.mul(q[i]).exp(w[i]);
		return result.normalize();
	}

	/**
	 * Calculates this^alpha where alpha is a real number and stores the result in this quaternion. See
	 * http://en.wikipedia.org/wiki/Quaternion#Exponential.2C_logarithm.2C_and_power
	 * 
	 * @param alpha
	 *            Exponent
	 * @return Result quaternion.
	 */
	public Quaternion exp(float alpha) {
		float norm = length();
		float normExp = (float) Math.pow(norm, alpha);

		float theta = (float) Math.acos(w / norm);

		float coeff = 0;
		if (Math.abs(theta) < 0.001)
			coeff = normExp * alpha / norm;
		else
			coeff = (float) (normExp * Math.sin(alpha * theta) / (norm * Math.sin(theta)));

		return new Quaternion(x * coeff, y * coeff, z * coeff, (float) (normExp * Math.cos(alpha * theta))).normalize();
	}

	/**
	 * Get the angle in radians of the rotation this quaternion represents. Does not normalize the quaternion.
	 * 
	 * @return the angle in radians of the rotation
	 */
	public float getAngleRad() {
		return (float) (2.0 * Math.acos((this.w > 1) ? (this.w / length()) : this.w));
	}

	/**
	 * Get the angle in degrees of the rotation this quaternion represents. Does not normalize the quaternion.
	 * 
	 * @return the angle in degrees of the rotation
	 */
	public float getAngle() {
		return getAngleRad() * MathUtil.RADIANS_TO_DEGREES;
	}

	/**
	 * Get the axis-angle representation of the rotation in radians. The x, y and z values will be the axis of the
	 * rotation and the w component returned is the angle in radians around that axis. The result axis is a unit vector.
	 * However, if this is an identity quaternion (no rotation), then the length of the axis may be zero.
	 * 
	 * @return the axis vector (xyz) and the angle in radians (w).
	 */
	public Vec4 getAxisAngleRad() {
		Quaternion q = this.w > 1 ? this : this.normalize();
		float angle = (float) (2.0 * Math.acos(this.w));
		double s = Math.sqrt(1 - q.w * q.w);
		if (s < MathUtil.FLOAT_ROUNDING_ERROR) {
			return new Vec4(q.x, q.y, q.z, angle);
		} else {
			return new Vec4((float) (q.x / s), (float) (q.y / s), (float) (q.z / s), angle);
		}
	}

	/**
	 * Get the axis-angle representation of the rotation in degrees. The x, y and z values will be the axis of the
	 * rotation and the w component returned is the angle in degrees around that axis. The result axis is a unit vector.
	 * However, if this is an identity quaternion (no rotation), then the length of the axis may be zero.
	 * 
	 * @return the axis vector (xyz) and the angle in radians (w).
	 */
	public Vec4 getAxisAngle() {
		return getAxisAngleRad().scale(MathUtil.RADIANS_TO_DEGREES);
	}

	/**
	 * Get the angle in radians of the rotation around the specified axis. The axis must be normalized.
	 * 
	 * @param axis
	 *            the normalized axis for which to get the angle
	 * @return the angle in radians of the rotation around the specified axis
	 */
	public float getAngleAroundRad(final Vec3 axis) {
		final float d = Vec3.dot(this.x, this.y, this.z, axis.x, axis.y, axis.z);
		final float l = Quaternion.length(axis.x * d, axis.y * d, axis.z * d, this.w);
		return MathUtil.isZero(l) ? 0f : (float) (2.0 * Math.acos(this.w / l));
	}

	/**
	 * Get the angle in degrees of the rotation around the specified axis. The axis must be normalized.
	 * 
	 * @param axis
	 *            the normalized axis for which to get the angle
	 * @return the angle in degrees of the rotation around the specified axis
	 */
	public float getAngleAround(final Vec3 axis) {
		return getAngleAround(axis) * MathUtil.RADIANS_TO_DEGREES;
	}

	/**
	 * Get the swing rotation and twist rotation for the specified axis. The twist rotation represents the rotation
	 * around the specified axis. The swing rotation represents the rotation of the specified axis itself, which is the
	 * rotation around an axis perpendicular to the specified axis. </p> The swing and twist rotation can be used to
	 * reconstruct the original quaternion: this = swing * twist
	 * 
	 * @param axis
	 *            the normalized axis for which to get the swing and twist rotation
	 * @param swing
	 *            will receive the swing rotation: the rotation around an axis perpendicular to the specified axis
	 * @param twist
	 *            will receive the twist rotation: the rotation around the specified axis
	 */
	public Pair<Quaternion, Quaternion> getSwingTwist(final Vec3 axis) {
		float d = new Vec3(x, y, z).dot(axis);

		Quaternion twist = new Quaternion(axis.x * d, axis.y * d, axis.z * d, this.w).normalize();
		Quaternion swing = twist.conjugate().mulLeft(this);
		return new Pair<>(swing, twist);
	}

	@Override
	public String toString() {
		return "Q" + super.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Quaternion)) {
			return false;
		}
		Quaternion other = (Quaternion) obj;
		return (Float.floatToRawIntBits(w) == Float.floatToRawIntBits(other.w)) && (Float.floatToRawIntBits(x) == Float.floatToRawIntBits(other.x))
				&& (Float.floatToRawIntBits(y) == Float.floatToRawIntBits(other.y)) && (Float.floatToRawIntBits(z) == Float.floatToRawIntBits(other.z));
	}

}
