package ch.ethz.ether.geom;

/**
 * Created by radar on 05/12/13.
 */
public final class Vec4 {
    public static final Vec4 ZERO = new Vec4(0, 0, 0, 0);

    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public Vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(float x, float y, float z) {
        this(x, y, z, 1);
    }

    public Vec4(Vec3 v) {
        this(v.x, v.y, v.z, 1);
    }
}
