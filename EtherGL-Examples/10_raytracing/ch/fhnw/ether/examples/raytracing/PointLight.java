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
 */package ch.fhnw.ether.examples.raytracing;

import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IAttribute.ISuppliers;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class PointLight implements ILight {
	private Vec3 position;
	private RGBA color;

	private String name = "point_light";

	public PointLight(Vec3 position, RGBA color) {
		this.position = position;
		this.color = color;
	}

	@Override
	public BoundingBox getBounds() {
		BoundingBox b = new BoundingBox();
		b.add(position);
		return b;
	}

	@Override
	public void getAttributeSuppliers(ISuppliers dst) {
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vec3 position) {
		this.position = position;
	}

	@Override
	public RGBA getColor() {
		return color;
	}

	@Override
	public void setColor(RGBA color) {
		this.color = color;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
