/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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

package ch.fhnw.ether.render.shader;

import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.geom.RGBA;
import ch.fhnw.ether.render.attribute.BooleanUniformAttribute;
import ch.fhnw.ether.render.attribute.FloatUniformAttribute;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.PointSizeArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.attribute.StateInjectAttribute;
import ch.fhnw.ether.render.attribute.Vec4FloatUniformAttribute;

public class Points extends AbstractShader {
	private RGBA rgba;
	private float pointSize;
	private float pointDecay;

	public Points() {
		this(null, 1, 0);
	}

	public Points(RGBA rgba) {
		this(rgba, 1, 0);
	}

	public Points(RGBA rgba, float pointSize, float pointDecay) {
		super("point_vc", PrimitiveType.POINT);
		this.rgba = rgba;
		this.pointSize = pointSize;
		this.pointDecay = pointDecay;
	}

	// TODO: point size array flag
	@Override
	public void getUniformAttributes(List<IUniformAttribute> dst) {
		dst.add(new ProjMatrixUniform());
		dst.add(new ViewMatrixUniform());

		dst.add(new BooleanUniformAttribute("shader.color_array_flag", "hasColor", () -> rgba == null));
		dst.add(new Vec4FloatUniformAttribute("shader.color", "color", () -> rgba == null ? null : rgba.v));
		dst.add(new FloatUniformAttribute("shader.point_size", "pointSize", () -> pointSize));
		dst.add(new FloatUniformAttribute("shader.point_decay", "pointDecay", () -> pointDecay));
		dst.add(new StateInjectAttribute("shader.point_size_program", (gl, p) -> gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE), (gl, p) -> gl.glDisable(GL3.GL_PROGRAM_POINT_SIZE)));
	}

	@Override
	public void getArrayAttributes(List<IArrayAttribute> dst) {
		dst.add(new PositionArray());
		if (rgba == null)
			dst.add(new ColorArray());
		if (pointSize == 0)
			dst.add(new PointSizeArray());
	}

	@Override
	public String toString() {
		return "points[rgba=" + rgba + " point_size=" + pointSize + " point_decay=" + pointDecay + "]";
	}
}
