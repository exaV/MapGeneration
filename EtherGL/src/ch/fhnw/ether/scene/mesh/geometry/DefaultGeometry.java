/*
\ * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
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

package ch.fhnw.ether.scene.mesh.geometry;

import java.util.Arrays;

import ch.fhnw.util.math.Transform;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

// note: position is always expected as first attribute
public class DefaultGeometry extends AbstractGeometry {
	private class TransformCache {
		TransformCache() {
			data = new float[attributeTypes.length][];
			for (int i = 0; i < attributeTypes.length; ++i) {
				if (attributeTypes[i] == POSITION_ARRAY) {
					data[i] = transform.transformVertices(DefaultGeometry.this.attributeData[i]);
				} else if (attributeTypes[i] == NORMAL_ARRAY) {
					data[i] = transform.transformNormals(DefaultGeometry.this.attributeData[i]);
				} else {
					data[i] = DefaultGeometry.this.attributeData[i];
				}
			}
			bounds = new BoundingBox();
			bounds.add(data[0]);
		}

		final float[][] data;
		final BoundingBox bounds;
	}

	private final IGeometryAttribute[] attributeTypes;
	private final float[][] attributeData; // first dimension is attribute, second data
	private final Transform transform = new Transform();

	private TransformCache cache;

	/**
	 * Generates geometry from the given data with the given attribute-layout. All data is copied. Changes on the passed
	 * arrays will not affect this geometry.
	 * 
	 * @param type
	 *            Primitive type of this geometry (points, lines, triangles)
	 * 
	 * @param attributes
	 *            Kind of attributes, must be same order as attribData
	 * 
	 * @param data
	 *            Vertex data, may contain positions, colors, normals, etc.
	 */
	public DefaultGeometry(Primitive type, IGeometryAttribute[] attributes, float[][] data) {
		super(type);

		if (attributes[0] != POSITION_ARRAY)
			throw new IllegalArgumentException("First attribute must be position");
		if (attributes.length != data.length)
			throw new IllegalArgumentException("# attribute type != # attribute data");

		this.attributeTypes = Arrays.copyOf(attributes, attributes.length);

		this.attributeData = new float[data.length][];
		for (int i = 0; i < data.length; ++i) {
			this.attributeData[i] = Arrays.copyOf(data[i], data[i].length);
		}
	}

	/**
	 * Create copy of this geometry.
	 * 
	 * @return the copy
	 */
	public DefaultGeometry copy() {
		DefaultGeometry geometry = new DefaultGeometry(getType(), attributeTypes, attributeData);
		geometry.transform.setOrigin(getOrigin());
		geometry.transform.setTranslation(getTranslation());
		geometry.transform.setRotation(getRotation());
		geometry.transform.setScale(getScale());
		return geometry;
	}

	@Override
	public void inspect(int index, IAttributeVisitor visitor) {
		validateCache();
		visitor.visit(attributeTypes[index], cache.data[index]);
	}

	@Override
	public void inspect(IAttributesVisitor visitor) {
		validateCache();
		visitor.visit(attributeTypes, cache.data);
	}

	@Override
	public void modify(int index, IAttributeVisitor visitor) {
		visitor.visit(attributeTypes[index], attributeData[index]);
		if (attributeTypes[index].equals(POSITION_ARRAY) || attributeTypes[index].equals(NORMAL_ARRAY))
			invalidateCache();
		else
			requestUpdate();
	}

	@Override
	public void modify(IAttributesVisitor visitor) {
		visitor.visit(attributeTypes, attributeData);
		invalidateCache();
	}

	// ---- IArrayAttributeProvider implementation

	@Override
	public void getAttributes(IAttributes attributes) {
		for (int i = 0; i < attributeTypes.length; ++i) {
			final int a = i;
			attributes.provide(attributeTypes[a], () -> {
				validateCache();
				return cache.data[a];
			});
		}
	}

	// ---- IGeometry implementation

	@Override
	public BoundingBox getBounds() {
		validateCache();
		return cache.bounds;
	}

	// ---- ITransformable implementation

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

	private void invalidateCache() {
		cache = null;
		requestUpdate();
	}

	private void validateCache() {
		if (cache == null) {
			cache = new TransformCache();
		}
	}

	// ---- static helpers for simple geometry creation from arrays

	public static DefaultGeometry createV(Primitive type, float[] vertices) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY };
		float[][] data = { vertices };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVN(Primitive type, float[] vertices, float[] normals) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY, NORMAL_ARRAY };
		float[][] data = { vertices, normals };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVC(Primitive type, float[] vertices, float[] colors) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY, COLOR_ARRAY };
		float[][] data = { vertices, colors };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVM(Primitive type, float[] vertices, float[] texCoords) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY, COLOR_MAP_ARRAY };
		float[][] data = { vertices, texCoords };
		return new DefaultGeometry(type, attributes, data);
	}
	
	public static DefaultGeometry createVNC(Primitive type, float[] vertices, float[] normals, float[] colors) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY, NORMAL_ARRAY, COLOR_ARRAY };
		float[][] data = { vertices, normals, colors };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVNM(Primitive type, float[] vertices, float[] normals, float[] texCoords) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY, NORMAL_ARRAY, COLOR_MAP_ARRAY };
		float[][] data = { vertices, normals, texCoords };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVCM(Primitive type, float[] vertices, float[] colors, float[] texCoords) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY, COLOR_ARRAY, COLOR_MAP_ARRAY };
		float[][] data = { vertices, colors, texCoords };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVNCM(Primitive type, float[] vertices, float[] normals, float[] colors, float[] texCoords) {
		IGeometryAttribute[] attributes = { POSITION_ARRAY, NORMAL_ARRAY, COLOR_ARRAY, COLOR_MAP_ARRAY };
		float[][] data = { vertices, normals, colors, texCoords };
		return new DefaultGeometry(type, attributes, data);
	}	
}
