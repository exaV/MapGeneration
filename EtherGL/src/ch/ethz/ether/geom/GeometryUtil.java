package ch.ethz.ether.geom;

import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class GeometryUtil {
    public static float[] calculateNormals(float[] faces) {
        float[] normals = new float[faces.length];
        for (int i = 0; i < faces.length; i += 9) {
            Vector3D n;
            try {
                Vector3D a = new Vector3D(faces[i + 3] - faces[i], faces[i + 4] - faces[i + 1], faces[i + 5] - faces[i + 2]);
                Vector3D b = new Vector3D(faces[i + 6] - faces[i], faces[i + 7] - faces[i + 1], faces[i + 8] - faces[i + 2]);
                n = Vector3D.crossProduct(a, b).normalize();
            } catch (Exception e) {
                n = new Vector3D(0, 0, 1);
            }
            normals[i] = normals[i + 3] = normals[i + 6] = (float) n.getX();
            normals[i + 1] = normals[i + 4] = normals[i + 7] = (float) n.getY();
            normals[i + 2] = normals[i + 5] = normals[i + 8] = (float) n.getZ();
        }
        return normals;
    }

    public static boolean isPointInPolygon(double x, double y, List<Vector3D> polygon) {
        boolean oddNodes = false;
        int j = polygon.size() - 1;
        for (int i = 0; i < polygon.size(); i++) {
            Vector3D a = polygon.get(i);
            Vector3D b = polygon.get(j);
            if ((a.getY() < y && b.getY() >= y) || (b.getY() < y && a.getY() >= y)) {
                if (a.getX() + (y - a.getY()) / (b.getY() - a.getY()) * (b.getX() - a.getX()) < x) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes;
    }

}
