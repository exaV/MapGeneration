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

package ch.fhnw.ether.render;

import javax.media.opengl.GL3;

import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.light.GenericLight.LightParameters;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public abstract class AbstractRenderer implements IRenderer {
	private static final LightParameters DEFAULT_LIGHT_PARAMETERS = new LightParameters(false, false, Vec3.Z, RGB.BLACK, RGB.WHITE, Vec3.Z_NEG, 0, 0, 0, 0, 0);

	private final Renderables renderables = new Renderables();

	private final AttributeProviders attributes = new AttributeProviders();

	private LightParameters lightParameters = DEFAULT_LIGHT_PARAMETERS;;

	public AbstractRenderer() {
		addAttributeProvider(new IAttributeProvider() {
			@Override
			public void getAttributeSuppliers(ISuppliers suppliers) {
				suppliers.provide(GenericLight.GENERIC_LIGHT, () -> lightParameters);
			}
		});
	}
	
	@Override
	public void addMesh(IMesh mesh) {
		renderables.addMesh(mesh, attributes);
	}

	@Override
	public void removeMesh(IMesh mesh) {
		renderables.removeMesh(mesh);
	}

	@Override
	public void addLight(ILight light) {
		if (light instanceof GenericLight)
			lightParameters = ((GenericLight) light).getLightParameters();
		else
			throw new IllegalArgumentException("can only handle GenericLight");
	}

	@Override
	public void removeLight(ILight light) {
		lightParameters = DEFAULT_LIGHT_PARAMETERS;
	}

	public void addAttributeProvider(IAttributeProvider provider) {
		attributes.addProvider(provider);
	}

	protected void update(GL3 gl) {
		this.renderables.update(gl);
	}

	protected void renderPass(GL3 gl, Pass pass, boolean interactive) {
		this.renderables.render(gl, pass, interactive);
	}
}
