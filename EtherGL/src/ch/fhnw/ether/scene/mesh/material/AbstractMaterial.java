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

import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.util.UpdateRequest;

public abstract class AbstractMaterial implements IMaterial {
	protected static final IAttribute[] NO_ATTRIBUTES = {};
	protected static final Object[] NO_DATA = {};

	private final UpdateRequest update = new UpdateRequest();
	
	private String name = "material";

	@Override
	public final String getName() {
		return name;
	}
	
	@Override
	public final void setName(String name) {
		this.name = name;
		updateRequest();
	}
	
	@Override
	public Primitive getType() {
		// default to triangles, as this is the majority of all materials
		return Primitive.TRIANGLES;
	}
	
	@Override
	public IAttribute[] getProvidedAttributes() {
		return NO_ATTRIBUTES;
	}

	@Override
	public IAttribute[] getRequiredAttributes() {
		return NO_ATTRIBUTES;
	}
	
	@Override
	public Object[] getData() {
		return NO_DATA;
	}

	@Override
	public final UpdateRequest getUpdater() {
		return update;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	protected final IAttribute[] attributes(IAttribute... attributes) {
		return attributes;
	}
	
	protected final Object[] data(Object... data) {
		return data;
	}
	
	protected final void updateRequest() {
		update.request();
	}
}
