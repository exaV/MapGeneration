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

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.NormalArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.math.Transform;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

// FIXME: !!!
// - changed doesn't really work (doesn't get back to false etc)
// - get back to vertex cache
public class VertexGeometry implements IGeometry {
	private PrimitiveType type;
	private IArrayAttribute[] attributes;
	private float[][] data; // first dimension is attribute, second data

	private BoundingBox bounds;

	private final UpdateRequest updater = new UpdateRequest(true);

	private Transform transform = new Transform();

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
	public VertexGeometry(PrimitiveType type, IArrayAttribute[] attributes, float[][] data) {
		this.type = type;
		this.attributes = Arrays.copyOf(attributes, attributes.length);
		this.data = new float[data.length][];
		for (int i = 0; i < data.length; ++i) {
			this.data[i] = Arrays.copyOf(data[i], data[i].length);
		}

		bounds = new BoundingBox();
		int positionArray = -1;
		for (int i = 0; i < attributes.length; ++i) {
			if (attributes[i].id() == PositionArray.ID) {
				bounds.add(this.data[i]);
				positionArray = i;
			}
		}

		if (positionArray == -1) {
			throw new IllegalArgumentException("Attributes must contain position");
		}

		requestUpdate();
	}

	/**
	 * Create copy of this geometry.
	 * 
	 * @return the copy
	 */
	public VertexGeometry copy() {
		VertexGeometry geometry = new VertexGeometry(type, attributes, data);
		geometry.transform.setOrigin(getOrigin());
		geometry.transform.setTranslation(getTranslation());
		geometry.transform.setRotation(getRotation());
		geometry.transform.setScale(getScale());
		return geometry;
	}

	public PrimitiveType getPrimitiveType() {
		return type;
	}

	public float[] getVertexData(int i) {
		return data[i];
	}

	// ---- IArrayAttributeProvider implementation

	@Override
	public void getAttributeSuppliers(PrimitiveType type, ISuppliers dst) {
		if (this.type != type) {
			throw new RuntimeException("Primitive type mismatch " + type.name() + " vs " + this.type.name());
		}

		for (int i = 0; i < attributes.length; ++i) {
			final int n = i;
			dst.add(attributes[i].id(), () -> {
				if (attributes[n].id() == PositionArray.ID) {
					return transform.transformVertices(data[n]);
				} else if (attributes[n].id() == NormalArray.ID) {
					return transform.transformNormals(data[n]);
				} else {
					return data[n];
				}
			});
		}
	}

	// ---- IGeometry implementation

	@Override
	public BoundingBox getBounds() {
		// FIXME: the boundingbox needs to respect the transformation...
		return bounds;
	}

	@Override
	public boolean needsUpdate() {
		return updater.needsUpdate();
	}

	// ---- ITransformable implementation

	@Override
	public Vec3 getOrigin() {
		return transform.getOrigin();
	}

	@Override
	public void setOrigin(Vec3 origin) {
		transform.setOrigin(origin);
		requestUpdate();
	}

	@Override
	public Vec3 getTranslation() {
		return transform.getTranslation();
	}

	@Override
	public void setTranslation(Vec3 translation) {
		transform.setTranslation(translation);
		requestUpdate();
	}

	@Override
	public Vec3 getRotation() {
		return transform.getRotation();
	}

	@Override
	public void setRotation(Vec3 rotation) {
		transform.setRotation(rotation);
		requestUpdate();
	}

	@Override
	public Vec3 getScale() {
		return transform.getScale();
	}

	@Override
	public void setScale(Vec3 scale) {
		transform.setScale(scale);
		requestUpdate();
	}
	
	
	private void requestUpdate() {
		updater.requestUpdate();
	}
}
