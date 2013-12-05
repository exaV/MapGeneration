package ch.ethz.ether.geom;

/**
 * Created by radar on 05/12/13.
 */
public final class Vec3 {
    public static final Vec3 ZERO = new Vec3(0, 0, 0);
    public static final Vec3 ONE = new Vec3(1, 1, 1);

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

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
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

    public static float dot(Vec3 a, Vec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vec3 cross(Vec3 a, Vec3 b) {
        // FIXME: verify!!!
        float x = a.y * b.z - a.z * b.y;
        float y = a.x * b.z - a.z * b.x;
        float z = a.x * b.y - a.y * b.x;
        return new Vec3(x, y, z);
    }
}
