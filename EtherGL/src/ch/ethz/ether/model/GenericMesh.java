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

package ch.ethz.ether.model;

import ch.ethz.ether.geom.BoundingBox;
import ch.ethz.ether.geom.PickUtil;
import ch.ethz.ether.geom.Vec3;
import ch.ethz.ether.view.IView;
import ch.ethz.util.IAddOnlyFloatList;

/**
 * Created by radar on 05/12/13.
 */
public class GenericMesh extends AbstractMesh {
    private class TransformCache {
        TransformCache() {
            triangleVertices = transform.transformVertices(GenericMesh.this.triangleVertices);
            triangleNormals = transform.transformNormals(GenericMesh.this.triangleNormals);
            edgeVertices = transform.transformVertices(GenericMesh.this.edgeVertices);
            pointVertices = transform.transformVertices(GenericMesh.this.pointVertices);
        }

        final float[] triangleVertices;
        final float[] triangleNormals;
        final float[] edgeVertices;
        final float[] pointVertices;
    }

    private BoundingBox bounds;
    private Transform transform = new Transform();
    private float[] triangleVertices;
    private float[] triangleNormals;
    private float[] triangleColors;
    private float[] edgeVertices;
    private float[] edgeColors;
    private float[] pointVertices;
    private float[] pointColors;

    private TransformCache cache;

    public GenericMesh() {
        this(Vec3.ZERO);
    }

    public GenericMesh(Vec3 origin) {
        transform.setOrigin(origin);
    }

    public void setTriangles(float[] vertices, float[] normals, float[] colors) {
        triangleVertices = vertices;
        triangleNormals = normals;
        triangleColors = colors;
        invalidateCache();
    }

    public void setEdges(float[] vertices, float[] colors) {
        edgeVertices = vertices;
        edgeColors = colors;
        invalidateCache();
    }

    public void setPoints(float[] vertices, float[] colors) {
        pointVertices = vertices;
        pointColors = colors;
        invalidateCache();
    }

    @Override
    public BoundingBox getBounds() {
        validateCache();
        return bounds;
    }

    @Override
    public Vec3 getOrigin() {
        return transform.getOrigin();
    }

    @Override
	public void setOrigin(Vec3 origin) {
        transform.setOrigin(origin);
        invalidateCache();
    }

    @Override
    public Vec3 getTranslation() {
        return transform.getTranslation();
    }

    @Override
    public void setTranslation(Vec3 translation) {
        transform.setTranslation(translation);
        invalidateCache();
    }

    @Override
    public Vec3 getRotation() {
        return transform.getRotation();
    }

    @Override
    public void setRotation(Vec3 rotation) {
        transform.setRotation(rotation);
        invalidateCache();
    }

    @Override
    public Vec3 getScale() {
        return transform.getScale();
    }

    @Override
    public void setScale(Vec3 scale) {
        transform.setScale(scale);
        invalidateCache();
    }

    @Override
    public boolean pick(PickMode mode, int x, int y, int w, int h, IView view, IPickState state) {
        validateCache();

        float zMin = Float.POSITIVE_INFINITY;
        float z = PickUtil.pickBoundingBox(mode, x, y, w, h, view, getBounds());
        if (Float.isInfinite(z))
            return false;

        if (cache.triangleVertices != null) {
            z = PickUtil.pickTriangles(mode, x, y, w, h, view, cache.triangleVertices);
            zMin = Math.min(zMin, z);
        }

        if (cache.edgeVertices != null) {
            z = PickUtil.pickEdges(mode, x, y, w, h, view, cache.edgeVertices);
            zMin = Math.min(zMin, z);
        }

        if (cache.pointVertices != null) {
            z = PickUtil.pickPoints(mode, x, y, w, h, view, cache.pointVertices);
            zMin = Math.min(zMin, z);
        }

        if (Float.isInfinite(zMin))
            return false;

        if (state != null) 
        	state.add(zMin, this);
        return true;
    }

    @Override
    public boolean getTriangleVertices(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.triangleVertices);
    }

    @Override
    public boolean getTriangleNormals(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.triangleNormals);
    }

    @Override
    public boolean getTriangleColors(IAddOnlyFloatList dst) {
        return dst.add(triangleColors);
    }

    @Override
    public boolean getEdgeVertices(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.edgeVertices);
    }

    @Override
    public boolean getEdgeColors(IAddOnlyFloatList dst) {
        return dst.add(edgeColors);
    }

    @Override
    public boolean getPointVertices(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.pointVertices);
    }

    @Override
    public boolean getPointColors(IAddOnlyFloatList dst) {
        return dst.add(pointColors);
    }

    private void invalidateCache() {
        cache = null;
        bounds = null;
    }

    private void validateCache() {
        if (cache == null) {
            cache = new TransformCache();
            bounds = new BoundingBox();
            bounds.add(cache.triangleVertices);
            bounds.add(cache.edgeVertices);
            bounds.add(cache.pointVertices);
        }
    }
}
