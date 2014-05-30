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

package ch.ethz.ether.render.util;

import ch.ethz.util.IAddOnlyFloatList;

public final class Primitives {
    public static final float[] UNIT_CUBE_TRIANGLES = {
            // bottom
            -0.5f, -0.5f, -0.5f, -0.5f, +0.5f, -0.5f, +0.5f, +0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f, +0.5f, +0.5f, -0.5f, +0.5f, -0.5f, -0.5f,

            // top
            +0.5f, -0.5f, +0.5f, +0.5f, +0.5f, +0.5f, -0.5f, +0.5f, +0.5f,
            +0.5f, -0.5f, +0.5f, -0.5f, +0.5f, +0.5f, -0.5f, -0.5f, +0.5f,

            // front
            -0.5f, -0.5f, -0.5f, +0.5f, -0.5f, -0.5f, +0.5f, -0.5f, +0.5f,
            -0.5f, -0.5f, -0.5f, +0.5f, -0.5f, +0.5f, -0.5f, -0.5f, +0.5f,

            // back
            +0.5f, +0.5f, -0.5f, -0.5f, +0.5f, -0.5f, -0.5f, +0.5f, +0.5f,
            +0.5f, +0.5f, -0.5f, -0.5f, +0.5f, +0.5f, +0.5f, +0.5f, +0.5f,

            // left
            -0.5f, +0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, +0.5f,
            -0.5f, +0.5f, -0.5f, -0.5f, -0.5f, +0.5f, -0.5f, +0.5f, +0.5f,

            // right
            +0.5f, -0.5f, -0.5f, +0.5f, +0.5f, -0.5f, +0.5f, +0.5f, +0.5f,
            +0.5f, -0.5f, -0.5f, +0.5f, +0.5f, +0.5f, +0.5f, -0.5f, +0.5f
    };

    public static final float[] UNIT_CUBE_EDGES = {
            // bottom
            -0.5f, -0.5f, -0.5f, -0.5f, +0.5f, -0.5f,
            -0.5f, +0.5f, -0.5f, +0.5f, +0.5f, -0.5f,
            +0.5f, +0.5f, -0.5f, +0.5f, -0.5f, -0.5f,
            +0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,

            // top
            +0.5f, -0.5f, +0.5f, +0.5f, +0.5f, +0.5f,
            +0.5f, +0.5f, +0.5f, -0.5f, +0.5f, +0.5f,
            -0.5f, +0.5f, +0.5f, -0.5f, -0.5f, +0.5f,
            -0.5f, -0.5f, +0.5f, +0.5f, -0.5f, +0.5f,

            // vertical
            -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, +0.5f,
            +0.5f, -0.5f, -0.5f, +0.5f, -0.5f, +0.5f,
            +0.5f, +0.5f, -0.5f, +0.5f, +0.5f, +0.5f,
            -0.5f, +0.5f, -0.5f, -0.5f, +0.5f, +0.5f
    };

    public static final float[] UNIT_CUBE_POINTS = {
            -0.5f, -0.5f, -0.5f, -0.5f, +0.5f, -0.5f, +0.5f, +0.5f, -0.5f, +0.5f, -0.5f, -0.5f,
            +0.5f, -0.5f, +0.5f, +0.5f, +0.5f, +0.5f, -0.5f, +0.5f, +0.5f, -0.5f, -0.5f, +0.5f,
    };


    public static void addLine(IAddOnlyFloatList dst, float x0, float y0, float x1, float y1) {
        dst.add(x0, y0, 0);
        dst.add(x1, y1, 0);
    }

    public static void addLine(IAddOnlyFloatList dst, float x0, float y0, float z0, float x1, float y1, float z1) {
        dst.add(x0, y0, z0);
        dst.add(x1, y1, z1);
    }

    public static void addRectangle(IAddOnlyFloatList dst, float x0, float y0, float x1, float y1) {
        addRectangle(dst, x0, y0, x1, y1, 0);
    }

    public static void addRectangle(IAddOnlyFloatList dst, float x0, float y0, float x1, float y1, float z) {
        dst.add(x0, y0, z);
        dst.add(x1, y0, z);
        dst.add(x1, y1, z);

        dst.add(x0, y0, z);
        dst.add(x1, y1, z);
        dst.add(x0, y1, z);
    }

    public static void addCube(FloatList dst, float tx, float ty, float tz, float sx, float sy, float sz) {
        dst.ensureCapacity(dst.size() + UNIT_CUBE_TRIANGLES.length * 3);
        for (int i = 0; i < UNIT_CUBE_TRIANGLES.length; i += 3) {
            dst.add((UNIT_CUBE_TRIANGLES[i] * sx) + tx);
            dst.add((UNIT_CUBE_TRIANGLES[i + 1] * sy) + ty);
            dst.add((UNIT_CUBE_TRIANGLES[i + 2] * sz) + tz);
        }
    }

}
