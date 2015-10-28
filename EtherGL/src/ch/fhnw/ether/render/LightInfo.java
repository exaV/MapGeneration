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

package ch.fhnw.ether.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.render.gl.FloatUniformBuffer;
import ch.fhnw.ether.render.variable.builtin.LightUniformBlock;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.ViewCameraState;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

import com.jogamp.opengl.GL3;

public final class LightInfo {
	private static final GenericLight DEFAULT_LIGHT = new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);

	private final List<GenericLight> lights = new ArrayList<>(Collections.singletonList(DEFAULT_LIGHT));
	private final FloatUniformBuffer uniforms = new FloatUniformBuffer(LightUniformBlock.BLOCK_SIZE);

	public LightInfo() {
	}

	public List<GenericLight> getLights() {
		return lights;
	}

	public IAttributeProvider getAttributeProvider() {
		return new IAttributeProvider() {
			@Override
			public void getAttributes(IAttributes attributes) {
				attributes.provide(LightUniformBlock.ATTRIBUTE, () -> uniforms.getBindingPoint());
			}
		};
	}

	public synchronized void addLight(ILight light) {
		if (!(light instanceof GenericLight)) {
			throw new IllegalArgumentException("can only handle GenericLight");
		}
		if (lights.contains(light)) {
			throw new IllegalArgumentException("light already in renderer: " + light);
		}
		if (lights.size() == LightUniformBlock.MAX_LIGHTS) {
			throw new IllegalStateException("too many lights in renderer: " + LightUniformBlock.MAX_LIGHTS);
		}
		if (lights.get(0) == DEFAULT_LIGHT)
			lights.remove(0);
		lights.add((GenericLight) light);
	}

	public synchronized void removeLight(ILight light) {
		synchronized (lights) {
			if (!lights.contains(light)) {
				throw new IllegalArgumentException("light not in renderer: " + light);
			}
			lights.remove(light);
			if (lights.isEmpty())
				lights.add(DEFAULT_LIGHT);
		}
	}

	public synchronized void update(GL3 gl, ViewCameraState matrices) {
		LightUniformBlock.loadUniforms(gl, uniforms, lights, matrices);
		uniforms.bind(gl);
	}
}