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

import ch.fhnw.ether.examples.raytracing.surface.IParametricSurface;
import ch.fhnw.ether.examples.raytracing.util.IntersectResult;
import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class RayTraceObject implements IMesh {

	private IParametricSurface surface;
	private Vec3 position = Vec3.ZERO;
	private RGBA color = RGBA.WHITE;

	public RayTraceObject(IParametricSurface surface) {
		this.surface = surface;
	}

	public RayTraceObject(IParametricSurface surface, RGBA color) {
		this(surface);
		this.color = color;
	}

	@Override
	public BoundingBox getBounds() {
		return new BoundingBox();
	}

	@Override
	public Vec3 getPosition() {
		return surface.getPosition();
	}

	@Override
	public void setPosition(Vec3 position) {
		surface.setPosition(position);
	}

	@Override
	public IGeometry getGeometry() {
		return null;
	}

	public IntersectResult intersect(Ray ray) {
		Vec3 point = surface.intersect(new Ray(ray.origin.add(position.negate()), ray.direction));
		if (point == null) {
			return IntersectResult.VOID;
		}
		return new IntersectResult(surface, point, color, ray.origin.subtract(point).length());
	}

	@Override
	public IMaterial getMaterial() {
		return new ColorMaterial(color);
	}

	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public String toString() {
		return "object=(" + surface + ", rgba=" + color + ")";
	}

}
