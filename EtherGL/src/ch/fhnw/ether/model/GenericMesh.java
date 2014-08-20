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
 */

package ch.fhnw.ether.model;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.geom.IColor;
import ch.fhnw.ether.geom.PickUtil;
import ch.fhnw.ether.geom.Vec3;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.NormalArray;
import ch.fhnw.ether.render.attribute.builtin.PointSizeArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.TexCoordArray;
import ch.fhnw.ether.view.IView;

/**
 * Created by radar on 05/12/13.
 */
public class GenericMesh extends AbstractMesh {
	private String name = "GenericMesh";

	private class TransformCache {
		TransformCache() {
			triangleVertices = transform.transformVertices(GenericMesh.this.triangleVertices);
			triangleNormals = transform.transformNormals(GenericMesh.this.triangleNormals);
			lineVertices = transform.transformVertices(GenericMesh.this.lineVertices);
			pointVertices = transform.transformVertices(GenericMesh.this.pointVertices);
		}

		final float[] triangleVertices;
		final float[] triangleNormals;
		final float[] lineVertices;
		final float[] pointVertices;
	}

	private BoundingBox bounds;
	private Transform transform = new Transform();
	private float[] triangleVertices;
	private float[] triangleNormals;
	private float[] triangleColors;
	private float[] triangleTexCoords;
	private float[] lineVertices;
	private float[] lineColors;
	private float[] pointVertices;
	private float[] pointColors;
	private float[] pointSizes;

	private TransformCache cache;

	public GenericMesh() {
		this(Vec3.ZERO);
	}

	public GenericMesh(Vec3 origin) {
		transform.setOrigin(origin);
	}

	public void setTriangles(float[] vertices) {
		setTriangles(vertices, null, null, null);
	}

	public void setTriangles(float[] vertices, float[] normals, float[] colors, float[] texCoords) {
		triangleVertices = vertices;
		triangleNormals = normals;
		triangleColors = colors;
		triangleTexCoords = texCoords;
		invalidateCache();
	}
    
    public void setTriangles(float[] vertices, float[] normals, IColor rgba) {
        triangleVertices = vertices;
        triangleNormals  = normals;
        triangleColors   = rgba.toArray();
		triangleTexCoords = null;
        invalidateCache();
    }

	public void setLines(float[] vertices) {
		setLines(vertices, (float[])null);
	}

	public void setLines(float[] vertices, float[] colors) {
		lineVertices = vertices;
		lineColors = colors;
		invalidateCache();
	}


    public void setLines(float[] vertices, IColor rgba) {
        lineVertices = vertices;
        lineColors   = rgba.toArray();
        invalidateCache();
    }
	public void setPoints(float[] vertices) {
		setPoints(vertices, (float[])null, null);
	}

	public void setPoints(float[] vertices, IColor color, float[] sizes) {
		setPoints(vertices, color.toArray(), sizes);
	}
	
	public void setPoints(float[] vertices, float[] colors, float[] sizes) {
		pointVertices = vertices;
		pointColors = colors;
		pointSizes = sizes;
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

		if (cache.lineVertices != null) {
			z = PickUtil.pickEdges(mode, x, y, w, h, view, cache.lineVertices);
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
	public void getAttributeSuppliers(PrimitiveType primitiveType, ISuppliers dst) {
		switch (primitiveType) {
		case POINT:
			getPointSuppliers(dst);
			break;
		case LINE:
			getLineSuppliers(dst);
			break;
		case TRIANGLE:
			getTriangleSuppliers(dst);
			break;
		}
	}

	private void getPointSuppliers(ISuppliers dst) {
		dst.add(PositionArray.supply(() -> {
			validateCache();
			return cache.pointVertices;
		}));
		dst.add(ColorArray.supply(() -> pointColors));
		dst.add(PointSizeArray.supply(() -> pointSizes));
	}

	private void getLineSuppliers(ISuppliers dst) {
		dst.add(PositionArray.supply(() -> {
			validateCache();
			return cache.lineVertices;
		}));
		dst.add(ColorArray.supply(() -> lineColors));
	}

	private void getTriangleSuppliers(ISuppliers dst) {
		dst.add(PositionArray.supply(() -> {
			validateCache();
			return cache.triangleVertices;
		}));
		dst.add(NormalArray.supply(() -> {
			validateCache();
			return cache.triangleNormals;
		}));
		dst.add(ColorArray.supply(() -> triangleColors));
		dst.add(TexCoordArray.supply(() -> triangleTexCoords));
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
			bounds.add(cache.lineVertices);
			bounds.add(cache.pointVertices);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isEmpty() {
		return triangleVertices == null && lineVertices == null && pointVertices == null;
	}

	@Override
	public String toString() {
		return getName();
	}

}
