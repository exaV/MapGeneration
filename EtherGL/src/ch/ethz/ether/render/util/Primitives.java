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
