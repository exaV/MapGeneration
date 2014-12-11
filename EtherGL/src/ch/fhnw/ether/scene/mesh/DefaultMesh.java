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

import java.util.EnumSet;

import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

// FIXME: we need some good mesh factory, this here is a mess
public final class DefaultMesh implements IMesh {
	private final IMaterial material;
	private final IGeometry geometry;
	private final Queue queue;
	private final EnumSet<Flags> flags;

	private String name = "unnamed_mesh";

	private final UpdateRequest updater = new UpdateRequest(true);

	public DefaultMesh(IMaterial material, IGeometry geometry) {
		this(material, geometry, Queue.DEPTH);
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue) {
		this(material, geometry, queue, NO_FLAGS);
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Flags flag, Flags... flags) {
		this(material, geometry, Queue.DEPTH, EnumSet.of(flag, flags));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Flags flag) {
		this(material, geometry, Queue.DEPTH, EnumSet.of(flag));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, EnumSet<Flags> flags) {
		this(material, geometry, Queue.DEPTH, flags);
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue, Flags flag) {
		this(material, geometry, queue, EnumSet.of(flag));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue, Flags flag, Flags... flags) {
		this(material, geometry, queue, EnumSet.of(flag, flags));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue, EnumSet<Flags> flags) {
		this.material = material;
		this.material.addUpdateListener(this);
		this.geometry = geometry;
		this.geometry.addUpdateListener(this);
		this.queue = queue;
		this.flags = flags;
	}

	// I3DObject implementation

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
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		requestUpdate();
	}

	// IMesh implementation

	@Override
	public Queue getQueue() {
		return queue;
	}

	@Override
	public EnumSet<Flags> getFlags() {
		return flags;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public IGeometry getGeometry() {
		return geometry;
	}

	@Override
	public boolean needsUpdate() {
		return updater.needsUpdate();
	}

	@Override
	public void requestUpdate(Object source) {
		requestUpdate();
	}

	@Override
	public String toString() {
		return name;
	}

	private void requestUpdate() {
		updater.requestUpdate();
	}
}
