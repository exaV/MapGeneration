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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.LineShader;
import ch.fhnw.ether.render.shader.builtin.PointShader;
import ch.fhnw.ether.render.shader.builtin.ShadedTriangleShader;
import ch.fhnw.ether.render.shader.builtin.UnshadedTriangleShader;
import ch.fhnw.ether.render.variable.IShaderUniform;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.CustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;
import ch.fhnw.util.Pair;

public final class ShaderBuilder {
	private static final class Attributes {
		final Map<IAttribute, Pair<Integer, Supplier<?>>> attributes = new HashMap<>();

		void provide(IAttribute attribute, Pair<Integer, Supplier<?>> link) {
			if (attributes.put(attribute, link) != null)
				throw new IllegalArgumentException("duplicate attribute: " + attribute);
		}

		Pair<Integer, Supplier<?>> getSupplier(IShader shader, IShaderUniform<?> uniform) {
			for (Entry<IAttribute, Pair<Integer, Supplier<?>>> entry : attributes.entrySet()) {
				if (entry.getKey().id().equals(uniform.id()))
					return entry.getValue();
			}
			throw new IllegalArgumentException("shader " + shader + " requires attribute " + uniform.id());
		}

		@Override
		public String toString() {
			final StringBuffer s = new StringBuffer();
			attributes.forEach((key, value) -> s.append("[").append(key).append(", ").append(value).append("] "));
			return s.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public static <S extends IShader> S create(S shader, IMaterial material, Map<IAttribute, Supplier<?>> globals) {
		Attributes attributes = new Attributes();

		// add material & geometry attributes
		if (material != null) {
			List<IAttribute> provided = material.getProvidedAttributes();
			for (int i = 0; i < provided.size(); ++i) {
				final int index = i;
				attributes.provide(provided.get(i), new Pair<>(index, null));
			}
			for (IAttribute required : material.getRequiredAttributes()) {
				attributes.provide(required, null);
			}
		}

		// add global attributes
		if (globals != null) {
			globals.forEach((attribute, supplier) -> attributes.provide(attribute,
					new Pair<>(-1, supplier)));
		}

		// create shader and attach all attributes this shader requires
		if (shader == null)
			shader = (S) createShader(material, Collections.unmodifiableSet(attributes.attributes.keySet()));

		// attach attribute suppliers to uniforms
		for (IShaderUniform<?> uniform : shader.getUniforms()) {
			if (!uniform.isLinked()) {
				Pair<Integer, Supplier<?>> link = attributes.getSupplier(shader, uniform);
				if (link.first == -1)
					uniform.setSupplier(link.second);
				else
					uniform.setIndex(link.first);
			}
		}

		return shader;
	}

	// as soon as we have more builtin shaders we should move to a more flexible
	// scheme, e.g. derive shader from provided attributes
	private static IShader createShader(IMaterial material, Collection<IAttribute> attributes) {
		if (material instanceof CustomMaterial) {
			return ((CustomMaterial) material).getShader();
		}

		switch (material.getType()) {
		case POINTS:
			return new PointShader(attributes);
		case LINES:
			return new LineShader(attributes);
		case TRIANGLES:
			if (material instanceof ColorMaterial) {
				return new UnshadedTriangleShader(attributes);
			} else if (material instanceof ShadedMaterial) {
				return new ShadedTriangleShader(attributes);
			}
		default:
			throw new UnsupportedOperationException("material type not supported: " + material);
		}
	}
}
