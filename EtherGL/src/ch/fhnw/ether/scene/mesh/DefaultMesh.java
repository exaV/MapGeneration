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

package ch.fhnw.ether.scene.mesh;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.IGeometryAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.math.Mat3;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public final class DefaultMesh implements IMesh {
	private final Queue queue;
	private final EnumSet<Flag> flags;
	private final IMaterial material;
	private final IGeometry geometry;
	private Vec3 position = Vec3.ZERO;
	private Mat4 transform = Mat4.ID;
	private BoundingBox bb;

	private String name = "unnamed_mesh";

	private final UpdateRequest update = new UpdateRequest(true);

	public DefaultMesh(IMaterial material, IGeometry geometry) {
		this(material, geometry, Queue.DEPTH);
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue) {
		this(material, geometry, queue, NO_FLAGS);
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Flag flag, Flag... flags) {
		this(material, geometry, Queue.DEPTH, EnumSet.of(flag, flags));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Flag flag) {
		this(material, geometry, Queue.DEPTH, EnumSet.of(flag));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, EnumSet<Flag> flags) {
		this(material, geometry, Queue.DEPTH, flags);
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue, Flag flag) {
		this(material, geometry, queue, EnumSet.of(flag));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue, Flag flag, Flag... flags) {
		this(material, geometry, queue, EnumSet.of(flag, flags));
	}

	public DefaultMesh(IMaterial material, IGeometry geometry, Queue queue, EnumSet<Flag> flags) {
		this.material = material;
		this.geometry = geometry;
		this.queue = queue;
		this.flags = flags;
		checkAttributeConsistency(material, geometry);
	}

	// I3DObject implementation

	@Override
	public BoundingBox getBounds() {
		if (bb == null) {
			bb = new BoundingBox();
			bb.add(getTransformedPositionData());
		}
		return bb;
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vec3 position) {
		this.position = position;
		bb = null;
		updateRequest();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	// IMesh implementation

	@Override
	public Queue getQueue() {
		return queue;
	}

	@Override
	public EnumSet<Flag> getFlags() {
		return flags;
	}

	@Override
	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
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
	public Mat4 getTransform() {
		return transform;
	}

	@Override
	public void setTransform(Mat4 transform) {
		if (this.transform != transform) {
			this.transform = transform;
			bb = null;
			updateRequest();
		}
	}
	
	@Override
	public float[] getTransformedPositionData() {
		Mat4 tp = Mat4.multiply(Mat4.translate(position), transform);
		return tp.transform(geometry.getData()[0]);
	}
	
	@Override
	public float[][] getTransformedGeometryData() {
		float[][] src = geometry.getData();
		float[][] dst = new float[src.length][];
		IGeometryAttribute[] attrs = geometry.getAttributes();
		Mat4 tp = Mat4.multiply(Mat4.translate(position), transform);
		dst[0] = tp.transform(src[0]);
		for (int i = 1; i < src.length; ++i) {
			if (attrs[i].equals(IGeometry.NORMAL_ARRAY)) {
				Mat3 tn = new Mat3(tp).inverse().transpose();
				dst[i] = tn.transform(src[i]);
			} else {
				dst[i] = Arrays.copyOf(src[i], src[i].length);
			}
		}
		return dst;
	}

	@Override
	public UpdateRequest getUpdater() {
		return update;
	}

	private void updateRequest() {
		update.request();
	}

	// we purposely leave equals and hashcode at default (identity)
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	private static void checkAttributeConsistency(IMaterial material, IGeometry geometry) {
		// primitive types must match
		Primitive m = material.getType();
		Primitive g = geometry.getType();
		if (m != g)
			throw new IllegalArgumentException("primitive types of material and geometry do not match: " + m + " " + g);

		// geometry must provide all materials required by material
		List<IGeometryAttribute> geometryAttributes = Arrays.asList(geometry.getAttributes());
		for (IAttribute attr : material.getGeometryAttributes()) {
			if (!geometryAttributes.contains(attr))
				throw new IllegalArgumentException("geometry does not provide required attribute: " + attr);				
		}
	}
}
