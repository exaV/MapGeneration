/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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
 */package ch.fhnw.ether.geom;

import java.util.List;

public class GeometryUtil {
    public static float[] calculateNormals(float[] triangles) {
        float[] normals = new float[triangles.length];
        for (int i = 0; i < triangles.length; i += 9) {
            Vec3 n;
            try {
                Vec3 a = new Vec3(triangles[i + 3] - triangles[i], triangles[i + 4] - triangles[i + 1], triangles[i + 5] - triangles[i + 2]);
                Vec3 b = new Vec3(triangles[i + 6] - triangles[i], triangles[i + 7] - triangles[i + 1], triangles[i + 8] - triangles[i + 2]);
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

    public static float intersectRayWithTriangle(Vec3 rayOrigin, Vec3 rayDirection, float[] triangle, int index) {
        return intersectRayWithTriangleOrPlane(rayOrigin, rayDirection, triangle, index, true);
    }

    public static float intersectScreenRayWithTriangle(float x, float y, float[] triangle, int index) {
        return intersectRayWithTriangleOrPlane(new Vec3(x, y, 0), new Vec3(0, 0, -1), triangle, index, true);
    }

    public static float intersectRayWithPlane(Vec3 rayOrigin, Vec3 rayDirection, float[] triangle, int index) {
        return intersectRayWithTriangleOrPlane(rayOrigin, rayDirection, triangle, index, false);
    }

    public static float intersectScreenRayWithPlane(float x, float y, float[] triangle, int index) {
        return intersectRayWithTriangleOrPlane(new Vec3(x, y, 0), new Vec3(0, 0, -1), triangle, index, false);
    }


    // http://en.wikipedia.org/wiki/Möller–Trumbore_intersection_algorithm
    private static final float EPSILON = 0.000001f;

    private static float intersectRayWithTriangleOrPlane(Vec3 rayOrigin, Vec3 rayDirection, float[] triangle, int index, boolean testBounds) {
        Vec3 o = rayOrigin;
        Vec3 d = rayDirection;

        // edge e1 = p2 - p1
        float e1x = triangle[index + 3] - triangle[index];
        float e1y = triangle[index + 4] - triangle[index + 1];
        float e1z = triangle[index + 5] - triangle[index + 2];

        // edge e2 = p3 - p1
        float e2x = triangle[index + 6] - triangle[index];
        float e2y = triangle[index + 7] - triangle[index + 1];
        float e2z = triangle[index + 8] - triangle[index + 2];

        // Vec3 p = d x e2
        float px = d.y * e2z - d.z * e2y;
        float py = d.z * e2x - d.x * e2z;
        float pz = d.x * e2y - d.y * e2x;

        // float det = e1 * p
        float det = e1x * px + e1y * py + e1z * pz;


        if (det > -EPSILON && det < EPSILON)
            return Float.POSITIVE_INFINITY;
        float detInv = 1f / det;

        // Vec3 t = o - p1 (distance from p1 to ray origin)
        float tx = o.x - triangle[index];
        float ty = o.y - triangle[index + 1];
        float tz = o.z - triangle[index + 2];

        // float u = (t * p) * detInv
        float u = (tx * px + ty * py + tz * pz) * detInv;

        if (testBounds && (u < 0 || u > 1))
            return Float.POSITIVE_INFINITY;

        // Vec3 q = t x e1
        float qx = ty * e1z - tz * e1y;
        float qy = tz * e1x - tx * e1z;
        float qz = tx * e1y - ty * e1x;

        // float v = (d, q) * detInv
        float v = (d.x * qx + d.y * qy + d.z * qz) * detInv;

        if (testBounds && (v < 0 || u + v > 1))
            return Float.POSITIVE_INFINITY;

        // t = (e2 * q) * detInv;
        float t = (e2x * qx + e2y * qy + e2z * qz) * detInv;

        // done
        return (t > EPSILON) ? t : Float.POSITIVE_INFINITY;
    }

    public static boolean is2DPointInTriangle(float x, float y, float[] triangle) {
        return is2DPointInTriangle(x, y, triangle, 0);
    }

    public static boolean is2DPointInTriangle(float x, float y, float[] triangle, int index) {
        boolean b1 = sign(x, y, triangle[index], triangle[index + 1], triangle[index + 3], triangle[index + 4]) < 0.0f;
        boolean b2 = sign(x, y, triangle[index + 3], triangle[index + 4], triangle[index + 6], triangle[index + 7]) < 0.0f;
        boolean b3 = sign(x, y, triangle[index + 6], triangle[index + 7], triangle[index], triangle[index + 1]) < 0.0f;
        return ((b1 == b2) && (b2 == b3));
    }

    private static float sign(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y);
    }

    public static boolean is2DPointInPolygon(float x, float y, float[] polygon) {
        boolean oddNodes = false;
        int j = polygon.length - 3;
        for (int i = 0; i < polygon.length; i += 3) {
            float ax = polygon[i];
            float ay = polygon[i + 1];
            float bx = polygon[j];
            float by = polygon[j + 1];
            if ((ay < y && by >= y) || (by < y && ay >= y)) {
                if (ax + (y - ay) / (by - ay) * (bx - ax) < x) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes;
    }

    public static boolean is2DPointInPolygon(float x, float y, List<Vec3> polygon) {
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
