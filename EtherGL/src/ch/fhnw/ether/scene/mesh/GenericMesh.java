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

package ch.fhnw.ether.scene.mesh;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.NormalArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.TexCoordArray;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.geometry.VertexGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

/**
 * Created by radar on 05/12/13.
 */
public class GenericMesh implements IMesh {
	private String name = "GenericMesh";

	private IMaterial material = null;
	private IGeometry geometry = null;
	private PrimitiveType type;
	
	private final UpdateRequest updater = new UpdateRequest();

	public GenericMesh(PrimitiveType type) {
		this.type = type;
	}

	public GenericMesh(PrimitiveType type, IMaterial material) {
		this(type);
		this.material = material;
	}

	public GenericMesh(VertexGeometry geometry, IMaterial material) {
		this(geometry.getPrimitiveType(), material);
		this.geometry = geometry;
		requestUpdate();
	}

	public GenericMesh(VertexGeometry geometry) {
		this(geometry.getPrimitiveType());
		this.geometry = geometry;
	}

	public void setGeometry(VertexGeometry geometry) {
		this.type = geometry.getPrimitiveType();
		this.geometry = geometry;
		requestUpdate();
	}

	// FIXME: move these as static methods to helper classes...
	public void setGeometry(float[] vertices) {
		setGeometry(vertices, RGBA.WHITE.generateColorArray(vertices.length / 3));
	}

	public void setGeometry(float[] vertices, float[] colors) {
		IArrayAttribute[] attributes = new IArrayAttribute[] { new PositionArray(), new ColorArray() };
		float[][] data = new float[][] { vertices, colors };

		geometry = new VertexGeometry(type, attributes, data);
		requestUpdate();
	}

	public void setGeometry(float[] vertices, float[] normals, float[] colors, float[] texCoords) {
		IArrayAttribute[] attributes = new IArrayAttribute[] { new PositionArray(), new NormalArray(), new ColorArray(), new TexCoordArray() };
		float[][] data = new float[][] { vertices, normals, colors, texCoords };

		geometry = new VertexGeometry(type, attributes, data);
		requestUpdate();
	}

	public void setGeometry(float[] vertices, float[] normals, float[] colors) {
		IArrayAttribute[] attributes = new IArrayAttribute[] { new PositionArray(), new NormalArray(), new ColorArray() };
		float[][] data = new float[][] { vertices, normals, colors };

		geometry = new VertexGeometry(type, attributes, data);
		requestUpdate();
	}

	public void setMaterial(IMaterial material) {
		this.material = material;
		requestUpdate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		requestUpdate();
	}

	@Override
	public IGeometry getGeometry() {
		return geometry;
	}

	@Override
	public IMaterial getMaterial() {
		return material != null ? material : IMaterial.EMPTY_MATERIAL;
	}

	@Override
	public BoundingBox getBounds() {
		return geometry.getBounds();
	}

	@Override
	public Vec3 getPosition() {
		return geometry.getTranslation();
	}

	@Override
	public void setPosition(Vec3 position) {
		geometry.setTranslation(position);
		requestUpdate();
	}

	@Override
	public boolean needsUpdate() {
		return updater.needsUpdate() || geometry.needsUpdate();
	}

	@Override
	public String toString() {
		return name;
	}
	
	private void requestUpdate() {
		updater.requestUpdate();
	}	
}
