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

import java.util.Collection;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Axis aligned bounding volume.
 *
 * @author radar
 */
public final class BoundingVolume {
    boolean valid;
    float minX;
    float maxX;
    float minY;
    float maxY;
    float minZ;
    float maxZ;

    public BoundingVolume() {
        reset();
    }

    public void reset() {
        valid = false;
        minX = Float.POSITIVE_INFINITY;
        maxX = Float.NEGATIVE_INFINITY;
        minY = Float.POSITIVE_INFINITY;
        maxY = Float.NEGATIVE_INFINITY;
        minZ = Float.POSITIVE_INFINITY;
        maxZ = Float.NEGATIVE_INFINITY;
    }

    public boolean isValid() {
        return valid;
    }

    public Vector3D getMin() {
        return new Vector3D(minX, minY, minZ);
    }

    public Vector3D getMax() {
        return new Vector3D(maxX, maxY, maxZ);
    }

    public Vector3D getCenter() {
        return new Vector3D(getCenterX(), getCenterY(), getCenterZ());
    }

    public Vector3D getExtent() {
        return new Vector3D(getExtentX(), getExtentY(), getExtentZ());
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMinZ() {
        return minZ;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public float getCenterX() {
        return minX + getExtentX() / 2;
    }

    public float getCenterY() {
        return minY + getExtentY() / 2;
    }

    public float getCenterZ() {
        return minZ + getExtentZ() / 2;
    }

    public float getExtentX() {
        return maxX - minX;
    }

    public float getExtentY() {
        return maxY - minY;
    }

    public float getExtentZ() {
        return maxZ - minZ;
    }

    public void add(float x, float y, float z) {
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        minY = Math.min(minY, y);
        maxY = Math.max(maxY, y);
        minZ = Math.min(minZ, z);
        maxZ = Math.max(maxZ, z);
        valid = true;
    }

    public void add(double x, double y, double z) {
        add((float) x, (float) y, (float) z);
    }

    public void add(Vector3D vertex) {
        if (vertex != null) {
            add(vertex.getX(), vertex.getY(), vertex.getZ());
        }
    }

    public void add(Collection<Vector3D> vertices) {
        if (vertices != null) {
            for (Vector3D vertex : vertices) {
                add(vertex);
            }
        }
    }

    public void add(float[] vertices) {
        if (vertices != null) {
            for (int i = 0; i < vertices.length; i += 3) {
                add(vertices[i], vertices[i + 1], vertices[i + 2]);
            }
        }
    }

    public void add(double[] vertices) {
        if (vertices != null) {
            for (int i = 0; i < vertices.length; i += 3) {
                add(vertices[i], vertices[i + 1], vertices[i + 2]);
            }
        }
    }

    public void add(BoundingVolume b) {
        if (b != null) {
            add(b.minX, b.minY, b.minZ);
            add(b.maxX, b.maxY, b.maxZ);
        }
    }

    @Override
    public String toString() {
        return valid ? "[" + minX + "," + maxX + "][" + minY + "," + maxY + "][" + minZ + "," + maxZ + "]" : "invalid";
    }
}
