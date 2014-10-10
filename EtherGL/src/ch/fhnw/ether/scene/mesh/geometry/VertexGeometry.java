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
import ch.fhnw.util.math.Transform;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class VertexGeometry implements IGeometry {

	private Transform transform = new Transform();
	
	private float[][] vertexData; //first dimension is attribute, second data
	private IArrayAttribute[] attributes;
	private BoundingBox boundings;
	private PrimitiveType type;
	private boolean changed = false;

	/**
	 * Generates geometry from the given data with the given attribute-layout.
	 * All data is copied. Changes on the passed arrays will not affect this geometry.
	 * 
	 * @param attribData Vertex Data, may contain positions, colors, normals, etc.
	 * @param attributes Kind of attributes, must be same order as attribData
	 * @param type
	 */
	public VertexGeometry(float[][] attribData, IArrayAttribute[] attributes, PrimitiveType type) {
		this.attributes = Arrays.copyOf(attributes, attributes.length);
		this.vertexData = new float[attribData.length][];
		for(int i=0; i<attribData.length; ++i) {
			vertexData[i] = Arrays.copyOf(attribData[i], attribData[i].length);
		}
		this.type = type;
		
		boundings = new BoundingBox();
		int positionArray = -1;
		for(int i=0; i<attributes.length; ++i) {
			if(attributes[i].id() == PositionArray.ID) {
				boundings.add(vertexData[i]);
				positionArray = i;
			}
		}
		
		if(positionArray == -1) {
			throw new IllegalArgumentException("Attributes must contain position");
		}
		
		changed = true;

	}
	
	/**
	 * Copy constructor. Transformation will also be copied.
	 * 
	 * @param geo
	 */
	public VertexGeometry(VertexGeometry geo) {
		this(geo.vertexData, geo.attributes, geo.type);
		this.transform.setOrigin(geo.getOrigin());
		this.transform.setTranslation(geo.getTranslation());
		this.transform.setRotation(geo.getRotation());
		this.transform.setScale(geo.getScale());
	}
	
	public PrimitiveType getPrimitiveType() {
		return type;
	}

	@Override
	public void getAttributeSuppliers(PrimitiveType primitiveType,
			ISuppliers dst) {
		if(this.type != primitiveType) {
			throw new RuntimeException("Primitive type is " + primitiveType.name() + 
					" but exptected " + type.name());
		}
		
		for(int i=0; i<attributes.length; ++i) {
			final int n = i;
			dst.add(attributes[i].id(), () -> {
				if(attributes[n].id() == NormalArray.ID) {
					return transform.transformNormals(vertexData[n]);
				} else if(attributes[n].id() == PositionArray.ID) {
					return transform.transformVertices(vertexData[n]);
				} else {
					return vertexData[n];
				}
			});
		}
	}

	@Override
	public BoundingBox getBoundings() {
		return boundings;
	}

	@Override
	public Vec3 getTranslation() {
		return transform.getTranslation();
	}

	@Override
	public void setTranslation(Vec3 translation) {
		transform.setTranslation(translation);
		changed = true;
	}

	@Override
	public Vec3 getRotation() {
		return transform.getRotation();
	}

	@Override
	public void setRotation(Vec3 rotation) {
		transform.setRotation(rotation);
		changed = true;
	}

	@Override
	public Vec3 getScale() {
		return transform.getScale();
	}

	@Override
	public void setScale(Vec3 scale) {
		transform.setScale(scale);
		changed = true;
	}

	@Override
	public Vec3 getOrigin() {
		return transform.getOrigin();
	}

	@Override
	public void setOrigin(Vec3 origin) {
		transform.setOrigin(origin);
		changed = true;
	}

	@Override
	public boolean hasChanged() {
		return changed;
	}
	
	public float[] getVertexData(int i) {
		if(i >= vertexData.length) return new float[0];
		return vertexData[i];
	}

}
