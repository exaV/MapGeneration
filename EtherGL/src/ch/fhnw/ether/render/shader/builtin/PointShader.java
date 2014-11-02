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

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.attribute.base.BooleanUniformAttribute;
import ch.fhnw.ether.render.attribute.base.FloatUniformAttribute;
import ch.fhnw.ether.render.attribute.base.StateInjectAttribute;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.ColorUniform;
import ch.fhnw.ether.render.attribute.builtin.PointSizeArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;

public class PointShader extends AbstractShader {
	public PointShader(Attributes attributes) {
		super(IShader.class, "builtin.points", "point_vc", Primitive.POINTS);

		boolean useVertexColors = attributes.contains(IMaterial.COLOR_ARRAY);
		boolean useVertexPointSize = attributes.contains(IMaterial.POINT_SIZE_ARRAY);

		addArray(new PositionArray());

		if (useVertexColors)
			addArray(new ColorArray());

		if (useVertexPointSize)
			addArray(new PointSizeArray());

		addUniform(new BooleanUniformAttribute("shader.vertex_colors_flag", "useVertexColors", () -> useVertexColors));
		addUniform(new BooleanUniformAttribute("shader.texture_flag", "useTexture", () -> false));

		addUniform(new ColorUniform(attributes.contains(IMaterial.COLOR) ? null : () -> RGBA.WHITE.toArray()));
		addUniform(new FloatUniformAttribute(IMaterial.POINT_SIZE, "pointSize", attributes.contains(IMaterial.POINT_SIZE) ? null : () -> 1f));

		addUniform(new StateInjectAttribute("shader.point_size_program", (gl, p) -> gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE),
				(gl, p) -> gl.glDisable(GL3.GL_PROGRAM_POINT_SIZE)));

		addUniform(new ProjMatrixUniform());
		addUniform(new ViewMatrixUniform());
	}
}
