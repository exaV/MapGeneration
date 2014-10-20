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

import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.base.BooleanUniformAttribute;
import ch.fhnw.ether.render.attribute.base.FloatUniformAttribute;
import ch.fhnw.ether.render.attribute.base.StateInjectAttribute;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.ColorMaterialUniform;
import ch.fhnw.ether.render.attribute.builtin.PointSizeArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.base.AbstractShader;

public class PointShader extends AbstractShader {
	public PointShader(boolean useVertexColors) {
		this(useVertexColors, 1, 0);
	}

	public PointShader(boolean useVertexColors, float pointSize, float pointDecay) {
		super(IShader.class, "builtin.points", "point_vc", PrimitiveType.POINT);

		if (!useVertexColors) {
			addUniform(new ColorMaterialUniform());
		} else {
			addArray(new ColorArray());
		}
		if (pointSize == 0)
			addArray(new PointSizeArray());

		addArray(new PositionArray());
		addUniform(new FloatUniformAttribute("shader.point_size", "pointSize", () -> pointSize));
		addUniform(new FloatUniformAttribute("shader.point_decay", "pointDecay", () -> pointDecay));
		addUniform(new StateInjectAttribute("shader.point_size_program", (gl, p) -> gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE),
				(gl, p) -> gl.glDisable(GL3.GL_PROGRAM_POINT_SIZE)));
		addUniform(new ProjMatrixUniform());
		addUniform(new ViewMatrixUniform());
		addUniform(new BooleanUniformAttribute("shader.vertex_colors_flag", "useVertexColors", () -> useVertexColors));
		addUniform(new BooleanUniformAttribute("shader.texture_flag", "useTexture", () -> false));

	}
}
