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

package ch.ethz.ether.geom;

/**
 * 3D vector for basic vector algebra.
 *
 * @author radar
 */
public class Vec3 {
    public static final Vec3 ZERO = new Vec3(0, 0, 0);
    public static final Vec3 ONE = new Vec3(1, 1, 1);
    public static final Vec3 X = new Vec3(1, 0, 0);
    public static final Vec3 Y = new Vec3(0, 1, 0);
    public static final Vec3 Z = new Vec3(0, 0, 1);
    public static final Vec3 X_NEG = new Vec3(-1, 0, 0);
    public static final Vec3 Y_NEG = new Vec3(0, -1, 0);
    public static final Vec3 Z_NEG = new Vec3(0, 0, -1);

    public final float x;
    public final float y;
    public final float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public Vec3(Vec4 v) {
    	this.x = v.x;
    	this.y = v.y;
    	this.z = v.z;
	}

	public float length() {
        return length(x, y, z);
    }

	public static float length(final float x, final float y, final float z) {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
    public float distance(Vec3 v) {
        return (float) Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y) + (v.z - z) * (v.z - z));
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 subtract(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 scale(float s) {
        return new Vec3(x * s, y * s, z * s);
    }

    public Vec3 negate() {
        return scale(-1);
    }

    public Vec3 normalize() {
        float l = length();
        if (l == 0)
            return null;
        if (l == 1)
            return this;
        return new Vec3(x / l, y / l, z / l);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Vec3) {
            final Vec3 v = (Vec3) other;
            return (x == v.x) && (y == v.y) && (z == v.z);
        }
        return false;
    }

	/** @return The dot product between this vecotr and a */
    public float dot(Vec3 a) {
        return dot(x, y, z, a.x, a.y, a.z);
    }

	/** @return The dot product between the two vectors */
    public static float dot(Vec3 a, Vec3 b) {
        return dot(a.x, a.y, a.z, b.x, b.y, b.z);
    }

	/** @return The dot product between the two vectors */
	public static float dot (float ax, float ay, float az, float bx, float by, float bz) {
		return ax * bx + ay * by + az * bz;
	}
	
    public static Vec3 cross(Vec3 a, Vec3 b) {
        float x = a.y * b.z - a.z * b.y;
        float y = a.z * b.x - a.x * b.z;
        float z = a.x * b.y - a.y * b.x;
        return new Vec3(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
