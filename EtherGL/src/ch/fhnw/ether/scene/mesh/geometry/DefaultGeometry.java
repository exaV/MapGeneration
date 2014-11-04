/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
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

import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.math.Transform;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

// note: position is always expected as first attribute
public class DefaultGeometry extends AbstractGeometry {
	private class TransformCache {
		TransformCache() {
			data = new float[attributeTypes.length][];
			for (int i = 0; i < attributeTypes.length; ++i) {
				if (attributeTypes[i].equals(IMaterial.POSITION_ARRAY.id())) {
					data[i] = transform.transformVertices(DefaultGeometry.this.attributeData[i]);
				} else if (attributeTypes[i].equals(IMaterial.NORMAL_ARRAY.id())) {
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

	private final String[] attributeTypes;
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
	public DefaultGeometry(Primitive type, String[] attributes, float[][] data) {
		super(type);

		if (!attributes[0].equals(IMaterial.POSITION_ARRAY.id()))
			throw new IllegalArgumentException("First attribute must be position");
		if (attributes.length != data.length)
			throw new IllegalArgumentException("# attribute type != # attribute data");

		this.attributeTypes = Arrays.copyOf(attributes, attributes.length);

		this.attributeData = new float[data.length][];
		for (int i = 0; i < data.length; ++i) {
			this.attributeData[i] = Arrays.copyOf(data[i], data[i].length);
		}
	}

	public DefaultGeometry(Primitive type, IAttribute[] attributes, float[][] data) {
		this(type, getAttributeIds(attributes), data);
	}

	private static String[] getAttributeIds(IAttribute[] attributes) {
		String[] result = new String[attributes.length];
		for (int i = 0; i < attributes.length; ++i)
			result[i] = attributes[i].id();
		return result;
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
		visitor.visit(attributeTypes[index], attributeData[index]);
	}

	@Override
	public void inspect(IAttributesVisitor visitor) {
		visitor.visit(attributeTypes, attributeData);
	}

	@Override
	public void modify(int index, IAttributeVisitor visitor) {
		visitor.visit(attributeTypes[index], attributeData[index]);
		if (attributeTypes[index].equals(IMaterial.POSITION_ARRAY.id()) || attributeTypes[index].equals(IMaterial.NORMAL_ARRAY.id()))
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
	public void getAttributeSuppliers(ISuppliers dst) {
		for (int i = 0; i < attributeTypes.length; ++i) {
			final int a = i;
			dst.provide(attributeTypes[a], () -> {
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
		IAttribute[] attributes = { IMaterial.POSITION_ARRAY };
		float[][] data = { vertices };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVN(Primitive type, float[] vertices, float[] normals) {
		IAttribute[] attributes = { IMaterial.POSITION_ARRAY, IMaterial.NORMAL_ARRAY };
		float[][] data = { vertices, normals };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVC(Primitive type, float[] vertices, float[] colors) {
		IAttribute[] attributes = { IMaterial.POSITION_ARRAY, IMaterial.COLOR_ARRAY };
		float[][] data = { vertices, colors };
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVNC(Primitive type, float[] vertices, float[] normals, float[] colors) {
		IAttribute[] attributes = { IMaterial.POSITION_ARRAY, IMaterial.NORMAL_ARRAY, IMaterial.COLOR_ARRAY };
		float[][] data = { vertices, normals, colors };
		System.out.println(vertices.length + " " + normals.length + " " + colors.length);
		return new DefaultGeometry(type, attributes, data);
	}

	public static DefaultGeometry createVNCM(Primitive type, float[] vertices, float[] normals, float[] colors, float[] texCoords) {
		IAttribute[] attributes = { IMaterial.POSITION_ARRAY, IMaterial.NORMAL_ARRAY, IMaterial.COLOR_ARRAY, IMaterial.COLOR_MAP_ARRAY };
		float[][] data = { vertices, normals, colors, texCoords };
		return new DefaultGeometry(type, attributes, data);
	}
}
