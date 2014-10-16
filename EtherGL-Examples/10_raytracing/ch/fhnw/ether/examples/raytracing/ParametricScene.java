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

package ch.fhnw.ether.examples.raytracing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.examples.raytracing.util.IntersectResult;
import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.I3DObject;

public class ParametricScene implements IScene {

	private List<RayTraceObject> meshes = new ArrayList<>();
	private ICamera camera;
	private ILight light;
	private RGBA background = RGBA.WHITE;

	public ParametricScene(ICamera camera, ILight light) {
		this.camera = camera;
		this.light = light;
	}

	@Override
	public List<? extends I3DObject> getObjects() {
		return meshes;
	}

	@Override
	public List<? extends IMesh> getMeshes() {
		return meshes;
	}

	@Override
	public List<ICamera> getCameras() {
		return Collections.singletonList(camera);
	}

	@Override
	public List<ILight> getLights() {
		return Collections.singletonList(light);
	}

	@Override
	public void setRenderer(IRenderer renderer) {
	}

	@Override
	public void renderUpdate() {
	}

	/**
	 * @param origin
	 *            ray origin
	 * @param direction
	 *            ray direction
	 * @return The color which results for the given ray in the scene. As float array with rgba-components
	 */
	public RGBA intersection(Ray ray) {

		// find nearest intersection point in scene
		IntersectResult nearest = new IntersectResult(null, null, background, Float.POSITIVE_INFINITY);
		for (RayTraceObject r : meshes) {
			IntersectResult intersect = r.intersect(ray);
			if (intersect.isValid() && intersect.dist < nearest.dist) {
				nearest = intersect;
			}
		}

		// no object found
		if (!nearest.isValid())
			return background;

		// create vector which is sure over surface
		Vec3 position_ontop_surface = nearest.position.subtract(ray.direction.scale(0.01f));
		position_ontop_surface = position_ontop_surface.add(nearest.surface.getNormalAt(nearest.position).scale(0.0001f));

		float dist_to_light = light.getPosition().subtract(nearest.position).length();

		// check if path to light is clear
		Ray light_ray = new Ray(position_ontop_surface, light.getPosition().subtract(position_ontop_surface));
		for (RayTraceObject r : meshes) {
			IntersectResult intersect = r.intersect(light_ray);
			if (intersect.isValid() && intersect.dist < dist_to_light) {
				return RGBA.BLACK;
			}
		}

		// diffuse color
		float f = Math.max(0, light_ray.direction.dot(nearest.surface.getNormalAt(nearest.position)));
		RGBA c = nearest.color;
		RGBA lc = light.getColor();

		return new RGBA(f * c.x * lc.x, f * c.y * lc.y, f * c.z * lc.z, c.w);
	}

	public void addMesh(RayTraceObject mesh) {
		meshes.add(mesh);
	}

}
