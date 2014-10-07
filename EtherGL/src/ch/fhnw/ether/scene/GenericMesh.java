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

package ch.fhnw.ether.scene;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.NormalArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.TexCoordArray;
import ch.fhnw.ether.reorg.api.IGeometry;
import ch.fhnw.ether.reorg.api.IMaterial;
import ch.fhnw.ether.reorg.api.IMesh;
import ch.fhnw.ether.reorg.base.VertexGeometry;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;

/**
 * Created by radar on 05/12/13.
 */
public class GenericMesh implements IMesh {
	private String name = "GenericMesh";

	private IMaterial material = null;
	private IGeometry geometry = null;
	private final PrimitiveType type;

	public GenericMesh(PrimitiveType type) {
		this.type = type;
	}
	
	public GenericMesh(PrimitiveType type, IMaterial material) {
		this.type = type;
		this.material = material;
	}

	public void setGeometry(float[] vertices) {
		setGeometry(vertices, RGBA.WHITE.generateColorArray(vertices.length/3));
	}
	
	public void setGeometry(float[] vertices, float[] colors) {
		IArrayAttribute[] attributes = new IArrayAttribute[]{new PositionArray(), new ColorArray()};
		float[][] data = new float[][]{vertices, colors};

		geometry = new VertexGeometry(data, attributes, type);
	}

	public void setGeometry(float[] vertices, float[] normals, float[] colors, float[] texCoords) {
		IArrayAttribute[] attributes = new IArrayAttribute[]{
				new PositionArray(), 
				new NormalArray(), 
				new ColorArray(), 
				new TexCoordArray()};
		float[][] data = new float[][]{vertices, normals, colors, texCoords};

		geometry = new VertexGeometry(data, attributes, type);
	}
	
	public void setGeometry(float[] vertices, float[] normals, float[] colors) {
		IArrayAttribute[] attributes = new IArrayAttribute[]{
				new PositionArray(), 
				new NormalArray(), 
				new ColorArray()};
		float[][] data = new float[][]{vertices, normals, colors};

		geometry = new VertexGeometry(data, attributes, type);
	}
	
	public void setMaterial(IMaterial material) {
		this.material = material;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public IGeometry getGeometry() {
		return geometry;
	}

	@Override
	public IMaterial getMaterial() {
		return material != null ? material : IMaterial.EmptyMaterial;
	}

	@Override
	public BoundingBox getBoundings() {
		return geometry.getBoundings();
	}

	@Override
	public float[] getPosition() {
		Vec3 o = geometry.getOrigin();
		return new float[]{o.x, o.y, o.z};
	}

	@Override
	public void setPosition(float[] position) {
		geometry.setOrigin(new Vec3(position[0], position[1], position[2]));
	}

}
