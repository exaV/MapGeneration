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

package ch.fhnw.ether.render.shader.builtin;

import ch.fhnw.ether.render.attribute.base.BooleanUniformAttribute;
import ch.fhnw.ether.render.attribute.base.FloatUniformAttribute;
import ch.fhnw.ether.render.attribute.base.Vec3FloatUniformAttribute;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.ColorMapArray;
import ch.fhnw.ether.render.attribute.builtin.ColorMapUniform;
import ch.fhnw.ether.render.attribute.builtin.LightUniforms;
import ch.fhnw.ether.render.attribute.builtin.NormalArray;
import ch.fhnw.ether.render.attribute.builtin.NormalMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.IMaterial;

public class ShadedTriangleShader extends AbstractShader {
	public ShadedTriangleShader(Attributes attributes) {
		super(IShader.class, "builtin.shader.shaded_triangles", "shaded_vct", Primitive.TRIANGLES);

		boolean useVertexColors = attributes.contains(IMaterial.COLOR_ARRAY);
		boolean useTexture = attributes.contains(IMaterial.COLOR_MAP_ARRAY);

		addArray(new PositionArray());
		addArray(new NormalArray());

		if (useVertexColors)
			addArray(new ColorArray());

		if (useTexture)
			addArray(new ColorMapArray());

		addUniform(new BooleanUniformAttribute("shader.vertex_colors_flag", "useVertexColors", () -> useVertexColors));
		addUniform(new BooleanUniformAttribute("shader.texture_flag", "useTexture", () -> useTexture));

		addUniform(new Vec3FloatUniformAttribute(IMaterial.EMISSION, "materialEmissionColor"));
		addUniform(new Vec3FloatUniformAttribute(IMaterial.AMBIENT, "materialAmbientColor"));
		addUniform(new Vec3FloatUniformAttribute(IMaterial.DIFFUSE, "materialDiffuseColor"));
		addUniform(new Vec3FloatUniformAttribute(IMaterial.SPECULAR, "materialSpecularColor"));
		addUniform(new FloatUniformAttribute(IMaterial.SHININESS, "materialShininess"));
		addUniform(new FloatUniformAttribute(IMaterial.STRENGTH, "materialStrength"));
		addUniform(new FloatUniformAttribute(IMaterial.ALPHA, "materialAlpha"));
		
		addUniform(new LightUniforms());

		if (useTexture)
			addUniform(new ColorMapUniform());

		addUniform(new ProjMatrixUniform());
		addUniform(new ViewMatrixUniform());
		addUniform(new NormalMatrixUniform());
	}
}
