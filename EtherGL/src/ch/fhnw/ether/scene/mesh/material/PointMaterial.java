/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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

package ch.fhnw.ether.scene.mesh.material;

import java.util.List;

import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.util.color.RGBA;

public class PointMaterial extends AbstractMaterial {
	private RGBA color;
	private float size;
	private final boolean perVertexColor;
	private final boolean perVertexSize;

	public PointMaterial(RGBA color, float size) {
		this(color, size, false, false);
	}

	public PointMaterial(RGBA color, float size, boolean perVertexColor, boolean perVertexSize) {
		this.color = color;
		this.size = size;
		this.perVertexColor = perVertexColor;
		this.perVertexSize = perVertexSize;
	}

	public final RGBA getColor() {
		return color;
	}

	public final void setColor(RGBA color) {
		this.color = color;
		updateRequest();
	}

	public final float getSize() {
		return size;
	}

	public final void setSize(float size) {
		this.size = size;
		updateRequest();
	}

	@Override
	public Primitive getType() {
		return Primitive.POINTS;
	}

	@Override
	public List<IAttribute> getProvidedAttributes() {
		List<IAttribute> attributes = super.getProvidedAttributes();
		attributes.add(IMaterial.COLOR);
		attributes.add(IMaterial.POINT_SIZE);
		return attributes;
	}

	@Override
	public List<IAttribute> getRequiredAttributes() {
		List<IAttribute> attributes = super.getRequiredAttributes();
		if (perVertexColor)
			attributes.add(IGeometry.COLOR_ARRAY);
		if (perVertexSize)
			attributes.add(IGeometry.POINT_SIZE_ARRAY);
		return attributes;
	}

	@Override
	public List<Object> getData() {
		List<Object> data = super.getData();
		data.add(color);
		data.add(size);
		return data;
	}
}
