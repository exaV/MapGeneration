package ch.ethz.ether.geom;

import java.util.List;

public class GeometryUtil {
    public static float[] calculateNormals(float[] faces) {
        float[] normals = new float[faces.length];
        for (int i = 0; i < faces.length; i += 9) {
            Vec3 n;
            try {
                Vec3 a = new Vec3(faces[i + 3] - faces[i], faces[i + 4] - faces[i + 1], faces[i + 5] - faces[i + 2]);
                Vec3 b = new Vec3(faces[i + 6] - faces[i], faces[i + 7] - faces[i + 1], faces[i + 8] - faces[i + 2]);
                n = Vec3.cross(a, b).normalize();
            } catch (Exception e) {
                n = new Vec3(0, 0, 1);
            }
            normals[i] = normals[i + 3] = normals[i + 6] = n.x;
            normals[i + 1] = normals[i + 4] = normals[i + 7] = n.y;
            normals[i + 2] = normals[i + 5] = normals[i + 8] = n.z;
        }
        return normals;
    }



    public static boolean isPointInTriangle(float x, float y, float[] triangle) {
        boolean b1 = sign(x, y, triangle[0], triangle[1], triangle[3], triangle[4]) < 0.0f;
        boolean b2 = sign(x, y, triangle[3], triangle[4], triangle[6], triangle[7]) < 0.0f;
        boolean b3 = sign(x, y, triangle[6], triangle[7], triangle[0], triangle[1]) < 0.0f;
        return ((b1 == b2) && (b2 == b3));
    }

    private static float sign(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y);
    }



    public static boolean isPointInPolygon(float x, float y, List<Vec3> polygon) {
        boolean oddNodes = false;
        int j = polygon.size() - 1;
        for (int i = 0; i < polygon.size(); i++) {
            Vec3 a = polygon.get(i);
            Vec3 b = polygon.get(j);
            if ((a.y < y && b.y >= y) || (b.y < y && a.y >= y)) {
                if (a.x + (y - a.y) / (b.y - a.y) * (b.x - a.x) < x) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes;
    }
}
